import java.sql.*;

public class TaiKhoanDAO {
    
    // Hàm kiểm tra đăng nhập
    public static boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Trả về true nếu tìm thấy tài khoản
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}