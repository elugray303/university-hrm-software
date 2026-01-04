import java.sql.*;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class NhanSuDAO {

    // 1. LẤY DANH SÁCH NHÂN VIÊN (Đã thêm Ngày Sinh + Thông tin Lương)
    public static DefaultTableModel getNhanVienModel() {
        Vector<String> columns = new Vector<>();
        columns.add("Mã NV"); 
        columns.add("Họ Tên"); 
        columns.add("Ngày Sinh"); // Mới
        columns.add("Khoa");
        columns.add("Chức Vụ"); 
        columns.add("Trình Độ"); 
        columns.add("Loại Hình");
        columns.add("Trạng Thái"); 
        
        // Các cột lương (để hiển thị hoặc ẩn tùy ý, nhưng cần có để truyền vào Dialog Sửa)
        columns.add("Hệ Số"); 
        columns.add("Lương CB");
        columns.add("Phụ Cấp");
        
        columns.add("Hình Ảnh"); // Cột cuối cùng (sẽ ẩn)

        Vector<Vector<Object>> rows = new Vector<>();
        
        // SQL lấy đầy đủ thông tin
        String sql = "SELECT nv.MaNV, nv.HoTen, nv.NgaySinh, k.TenKhoa, nv.ChucVu, nv.TrinhDo, " +
                     "nv.LoaiHinh, nv.TrangThai, nv.HeSoLuong, nv.LuongCoBan, nv.PhuCap, nv.HinhAnh " +
                     "FROM NhanVien nv LEFT JOIN Khoa k ON nv.MaKhoa = k.MaKhoa";

        try (Connection conn = ConnectDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            java.text.NumberFormat nf = java.text.NumberFormat.getInstance(); // Format số tiền

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("MaNV"));
                row.add(rs.getString("HoTen"));
                row.add(rs.getString("NgaySinh")); // Lấy ngày sinh
                row.add(rs.getString("TenKhoa"));
                row.add(rs.getString("ChucVu"));
                row.add(rs.getString("TrinhDo"));
                row.add(rs.getString("LoaiHinh"));
                row.add(rs.getString("TrangThai"));
                
                // Lấy thông tin lương
                row.add(rs.getDouble("HeSoLuong"));
                row.add(nf.format(rs.getDouble("LuongCoBan"))); // Format tiền cho đẹp
                row.add(nf.format(rs.getDouble("PhuCap")));
                
                row.add(rs.getString("HinhAnh"));
                rows.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return new DefaultTableModel(rows, columns);
    }

    // 2. LẤY DANH SÁCH KHOA
    public static List<String> getKhoaList() {
        List<String> list = new ArrayList<>();
        try (Connection conn = ConnectDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MaKhoa, TenKhoa FROM Khoa")) {
            while (rs.next()) {
                list.add(rs.getString("MaKhoa") + " - " + rs.getString("TenKhoa"));
            }
        } catch (SQLException e) {} 
        return list;
    }

    // 3. THÊM NHÂN VIÊN (Đã thêm HeSo, LuongCB, PhuCap)
    public static boolean addNhanVien(String ma, String ten, String ns, String gt, String makhoa, 
                                      String cv, String td, String lh, String tt, 
                                      double heSo, double luongCB, double phuCap, String hinh) {
        
        String sql = "INSERT INTO NhanVien (MaNV, HoTen, NgaySinh, GioiTinh, MaKhoa, ChucVu, TrinhDo, LoaiHinh, TrangThai, HeSoLuong, LuongCoBan, PhuCap, HinhAnh) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, ma); 
            p.setString(2, ten); 
            p.setString(3, ns); 
            p.setString(4, gt);
            p.setString(5, makhoa); 
            p.setString(6, cv); 
            p.setString(7, td); 
            p.setString(8, lh);
            p.setString(9, tt);
            // Các trường lương
            p.setDouble(10, heSo);
            p.setDouble(11, luongCB);
            p.setDouble(12, phuCap);
            p.setString(13, hinh);
            
            return p.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 4. SỬA NHÂN VIÊN (Đã thêm HeSo, LuongCB, PhuCap)
    public static boolean updateNhanVien(String ma, String ten, String ns, String gt, String makhoa, 
                                         String cv, String td, String lh, String tt, 
                                         double heSo, double luongCB, double phuCap, String hinh) {
        
        String sql = "UPDATE NhanVien SET HoTen=?, NgaySinh=?, GioiTinh=?, MaKhoa=?, ChucVu=?, " +
                     "TrinhDo=?, LoaiHinh=?, TrangThai=?, HeSoLuong=?, LuongCoBan=?, PhuCap=?, HinhAnh=? WHERE MaNV=?";
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, ten); 
            p.setString(2, ns); 
            p.setString(3, gt); 
            p.setString(4, makhoa);
            p.setString(5, cv); 
            p.setString(6, td); 
            p.setString(7, lh); 
            p.setString(8, tt);
            // Các trường lương
            p.setDouble(9, heSo);
            p.setDouble(10, luongCB);
            p.setDouble(11, phuCap);
            p.setString(12, hinh); 
            p.setString(13, ma); // Điều kiện WHERE
            
            return p.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    // 5. XÓA NHÂN VIÊN
    public static boolean deleteNhanVien(String ma) {
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement("DELETE FROM NhanVien WHERE MaNV=?")) {
            p.setString(1, ma);
            return p.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }
}