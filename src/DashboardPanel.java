import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DashboardPanel extends JPanel {
    
    // Màu sắc chủ đạo
    Color colPrimary = new Color(52, 152, 219);
    Color colDanger = new Color(231, 76, 60);
    Color colSuccess = new Color(46, 204, 113);
    Color colWarning = new Color(241, 196, 15);

    public DashboardPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        // 1. HEADER
        JLabel lblTitle = new JLabel("TỔNG QUAN QUẢN TRỊ NHÂN SỰ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(44, 62, 80));
        add(lblTitle, BorderLayout.NORTH);

        // 2. CARDS PANEL (Thống kê số lượng)
        JPanel pnlCards = new JPanel(new GridLayout(1, 4, 20, 0));
        pnlCards.setOpaque(false);
        pnlCards.setPreferredSize(new Dimension(0, 120));

        // Lấy số liệu thật từ DB (Giả lập hàm lấy số liệu)
        int tongNV = getCount("SELECT COUNT(*) FROM NhanVien");
        int tongTS = getCount("SELECT COUNT(*) FROM NhanVien WHERE TrinhDo = N'Tiến sĩ'");
        int sapHetHD = 0;
        //int sapHetHD = getCount("SELECT COUNT(*) FROM NhanVien WHERE DATEDIFF(day, GETDATE(), NgayHetHanHD) < 30"); 
        // Lưu ý: Cần có cột NgayHetHanHD trong DB để query trên chạy đúng, tạm thời fix cứng demo
        
        pnlCards.add(createCard("Tổng Nhân Sự", String.valueOf(tongNV), "icon_users.png", colPrimary));
        pnlCards.add(createCard("Tiến Sĩ/PGS", String.valueOf(tongTS), "icon_hat.png", colSuccess));
        pnlCards.add(createCard("Sắp Hết HĐ", "3", "icon_alert.png", colDanger)); // Demo số 3
        pnlCards.add(createCard("Quỹ Lương T12", "2.4 Tỷ", "icon_money.png", colWarning));

        // 3. CHARTS PANEL
        JPanel pnlCharts = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlCharts.setOpaque(false);
        
        // Biểu đồ 1: Cơ cấu trình độ (Pie Chart tự vẽ)
        pnlCharts.add(new ChartPanel("Cơ cấu Trình độ", true));
        
        // Biểu đồ 2: Tỷ lệ Giới tính (Pie Chart tự vẽ)
        pnlCharts.add(new ChartPanel("Tỷ lệ Nam/Nữ", false));

        // Gộp lại
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 20));
        pnlCenter.setOpaque(false);
        pnlCenter.add(pnlCards, BorderLayout.NORTH);
        pnlCenter.add(pnlCharts, BorderLayout.CENTER);
        
        add(pnlCenter, BorderLayout.CENTER);
    }

    // --- HELPER: Tạo thẻ Card thống kê ---
    private JPanel createCard(String title, String value, String iconPath, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, color)); // Viền dưới màu
        
        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Arial", Font.BOLD, 32));
        lblVal.setForeground(color);
        lblVal.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(new EmptyBorder(0,0,10,0));

        p.add(lblVal, BorderLayout.CENTER);
        p.add(lblTitle, BorderLayout.SOUTH);
        
        // Hiệu ứng bóng đổ đơn giản (Shadow)
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230,230,230), 1),
            p.getBorder()
        ));
        return p;
    }

    // --- HELPER: Lấy số liệu từ DB ---
    private int getCount(String sql) {
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // --- CLASS NỘI BỘ: Vẽ Biểu đồ tròn đơn giản ---
    class ChartPanel extends JPanel {
        String title;
        boolean isDegree; // true: Trình độ, false: Giới tính

        public ChartPanel(String title, boolean isDegree) {
            this.title = title;
            this.isDegree = isDegree;
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            
            // Vẽ tiêu đề
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.drawString(title, 20, 30);

            // Vẽ Pie Chart
            int size = Math.min(w, h) - 100;
            int x = (w - size) / 2;
            int y = (h - size) / 2 + 20;

            if (isDegree) {
                // Giả lập số liệu Trình độ: TS(20%), ThS(50%), CN(30%)
                drawSlice(g2, x, y, size, 0, 72, colDanger, "Tiến sĩ (20%)");
                drawSlice(g2, x, y, size, 72, 180, colPrimary, "Thạc sĩ (50%)");
                drawSlice(g2, x, y, size, 252, 108, colSuccess, "Cử nhân (30%)");
            } else {
                // Giả lập số liệu Nam/Nữ: Nam(60%), Nữ(40%)
                drawSlice(g2, x, y, size, 0, 216, new Color(52, 152, 219), "Nam (60%)");
                drawSlice(g2, x, y, size, 216, 144, new Color(231, 76, 60), "Nữ (40%)");
            }
        }

        private void drawSlice(Graphics2D g2, int x, int y, int s, int start, int extent, Color c, String label) {
            g2.setColor(c);
            g2.fill(new Arc2D.Double(x, y, s, s, start, extent, Arc2D.PIE));
            
            // Vẽ chú thích đơn giản (Legend) - Logic vẽ tọa độ hơi phức tạp, đây là demo đơn giản
            // Bạn có thể vẽ 1 hình vuông nhỏ + text ở góc dưới panel thay vì vẽ trực tiếp lên chart
        }
    }
}