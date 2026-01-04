import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class LuongDAO {

    /**
     * Lấy danh sách lương.
     * SỬA LỖI: Ưu tiên hiển thị dữ liệu đã lưu trong bảng BangLuong (bl).
     * Nếu chưa lưu (bl null) thì mới hiển thị dữ liệu gốc từ NhanVien (nv).
     */
    public static DefaultTableModel getBangLuong(int thang, int nam) {
        Vector<String> cols = new Vector<>();
        cols.add("Mã NV");
        cols.add("Họ Tên");
        cols.add("Chức Vụ");
        cols.add("Hệ Số");
        cols.add("Lương CB");
        cols.add("Phụ Cấp");
        cols.add("THỰC LĨNH");

        Vector<Vector<Object>> rows = new Vector<>();

        // --- CÂU SQL ĐÃ SỬA ---
        // Sử dụng ISNULL(bl.Cot, nv.Cot) để ưu tiên lấy dữ liệu lịch sử
        String sql = "SELECT " +
                     "nv.MaNV, nv.HoTen, nv.ChucVu, " +
                     "ISNULL(bl.HeSo, nv.HeSoLuong) AS HienThiHeSo, " +   // Lấy Hệ số lịch sử
                     "ISNULL(bl.LuongCB, nv.LuongCoBan) AS HienThiLuongCB, " + // Lấy Lương CB lịch sử
                     "ISNULL(bl.PhuCap, nv.PhuCap) AS HienThiPhuCap, " +  // Lấy Phụ cấp lịch sử
                     "ISNULL(bl.ThucLinh, 0) AS DaLinh " +
                     "FROM NhanVien nv " +
                     "LEFT JOIN BangLuong bl ON nv.MaNV = bl.MaNV AND bl.Thang = ? AND bl.Nam = ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            
            p.setInt(1, thang);
            p.setInt(2, nam);
            
            ResultSet rs = p.executeQuery();
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance();

            while (rs.next()) {
                Vector<Object> r = new Vector<>();
                r.add(rs.getString("MaNV"));
                r.add(rs.getString("HoTen"));
                r.add(rs.getString("ChucVu"));
                
                // Lấy dữ liệu từ các cột Alias đã định nghĩa ở trên
                r.add(rs.getDouble("HienThiHeSo")); 
                r.add(nf.format(rs.getDouble("HienThiLuongCB")));
                r.add(nf.format(rs.getDouble("HienThiPhuCap")));
                
                double thucLinh = rs.getDouble("DaLinh");
                r.add(nf.format(thucLinh)); 
                rows.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DefaultTableModel(rows, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /**
     * Lấy chi tiết lương để đổ vào Form nhập liệu.
     * Cũng áp dụng logic ưu tiên dữ liệu lịch sử.
     */
    public static Object[] getChiTietLuong(String maNV, int thang, int nam) {
        String sql = "SELECT " +
                     "nv.MaNV, nv.HoTen, nv.LoaiHinh, " +
                     // Ưu tiên lấy dữ liệu đã lưu trong BangLuong
                     "ISNULL(bl.HeSo, nv.HeSoLuong) AS HeSo, " +
                     "ISNULL(bl.LuongCB, nv.LuongCoBan) AS LuongCB, " +
                     "ISNULL(bl.PhuCap, nv.PhuCap) AS PhuCap, " +
                     "bl.TongTiet, bl.ThuLao, bl.ThucLinh " +
                     "FROM NhanVien nv " +
                     "LEFT JOIN BangLuong bl ON nv.MaNV = bl.MaNV AND bl.Thang = ? AND bl.Nam = ? " +
                     "WHERE nv.MaNV = ?";
                     
        try (Connection conn = ConnectDatabase.getConnection(); 
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, thang);
            p.setInt(2, nam);
            p.setString(3, maNV);
            
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return new Object[] {
                    rs.getString("MaNV"),
                    rs.getString("HoTen"),
                    rs.getString("LoaiHinh"),
                    rs.getDouble("HeSo"),     // Số liệu chính xác tại thời điểm đó
                    rs.getDouble("LuongCB"),  // Số liệu chính xác tại thời điểm đó
                    rs.getDouble("PhuCap"),   // Số liệu chính xác tại thời điểm đó
                    rs.getObject("TongTiet") != null ? rs.getInt("TongTiet") : 0,
                    rs.getObject("ThucLinh") != null ? rs.getDouble("ThucLinh") : 0
                };
            }
        } catch(Exception e) {
            e.printStackTrace();
        } 
        return null;
    }

    // Hàm lưu (Đảm bảo logic Update/Insert đúng cột)
    public static boolean saveSingleSalary(String maNV, int thang, int nam, 
                                           double luongCB, double heSo, double phuCap, 
                                           double luongCung, int tongTiet, double thuLao, double thucLinh) {
        Connection conn = null;
        try {
            conn = ConnectDatabase.getConnection();
            
            String checkSql = "SELECT COUNT(*) FROM BangLuong WHERE MaNV=? AND Thang=? AND Nam=?";
            PreparedStatement check = conn.prepareStatement(checkSql);
            check.setString(1, maNV); check.setInt(2, thang); check.setInt(3, nam);
            ResultSet rs = check.executeQuery();
            rs.next();
            boolean exists = rs.getInt(1) > 0;

            String sql;
            if (exists) {
                // UPDATE: Cập nhật lại toàn bộ các thông số mới nhập
                sql = "UPDATE BangLuong SET " +
                      "LuongCB=?, HeSo=?, PhuCap=?, " + 
                      "LuongCung=?, TongTiet=?, ThuLao=?, ThucLinh=?, NgayChot=GETDATE() " +
                      "WHERE MaNV=? AND Thang=? AND Nam=?";
            } else {
                // INSERT
                sql = "INSERT INTO BangLuong (LuongCB, HeSo, PhuCap, LuongCung, TongTiet, ThuLao, ThucLinh, MaNV, Thang, Nam) " +
                      "VALUES (?,?,?,?,?,?,?,?,?,?)";
            }

            PreparedStatement p = conn.prepareStatement(sql);
            p.setDouble(1, luongCB);
            p.setDouble(2, heSo);
            p.setDouble(3, phuCap);
            p.setDouble(4, luongCung);
            p.setInt(5, tongTiet);
            p.setDouble(6, thuLao);
            p.setDouble(7, thucLinh);
            p.setString(8, maNV);
            p.setInt(9, thang);
            p.setInt(10, nam);

            return p.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if(conn != null) conn.close(); } catch(Exception e) {}
        }
    }
}