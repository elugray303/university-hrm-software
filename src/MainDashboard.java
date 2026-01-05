import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainDashboard extends JFrame {

    private JPanel pnlCenterContent; 
    private CardLayout cardLayout;
    
    // --- KHAI BÁO BIẾN LỚP (Global Variables) ---
    private SchedulePanel pnlSchedule; 
    private ResearchPanel pnlResearch; // Phải khai báo ở đây để dùng trong nút bấm
    private CVPanel pnlCV;             // Phải khai báo ở đây để dùng trong nút bấm

    public MainDashboard() {
        setTitle("HỆ THỐNG QUẢN LÝ ĐÀO TẠO ĐẠI HỌC");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(44, 62, 80)); 
        pnlHeader.setPreferredSize(new Dimension(100, 60));
        pnlHeader.setBorder(new EmptyBorder(0, 15, 0, 15));

        JLabel lblTitle = new JLabel("UNIVERSITY HRM SYSTEM v2.0");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setBackground(new Color(231, 76, 60)); btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            if(JOptionPane.showConfirmDialog(this, "Đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                this.dispose(); 
            }
        });

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(btnLogout, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. SIDEBAR ---
        JPanel pnlSidebar = new JPanel();
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));
        pnlSidebar.setBackground(new Color(236, 240, 241));
        pnlSidebar.setPreferredSize(new Dimension(220, 0));
        pnlSidebar.setBorder(new EmptyBorder(20, 10, 0, 10));
        
        JButton btnHome = new JButton("🏠 Trang chủ");
        JButton btnEmp = new JButton("👥 Quản lý Nhân sự");
        JButton btnFaculty = new JButton("📚 Khoa & Môn học");
        JButton btnResearch = new JButton("🔬 Nghiên cứu KH");
        JButton btnCV = new JButton("📄 Hồ sơ Khoa học"); 
        JButton btnSchedule = new JButton("📅 Xếp Thời Khóa Biểu");
        JButton btnSalary = new JButton("💰 Tính Lương"); 
        
        styleButton(btnHome); styleButton(btnEmp); styleButton(btnFaculty); 
        styleButton(btnResearch); styleButton(btnCV); 
        styleButton(btnSchedule); styleButton(btnSalary);

        // SỰ KIỆN CHUYỂN TAB (Có gọi hàm Refresh)
        btnHome.addActionListener(e -> cardLayout.show(pnlCenterContent, "HOME"));
        btnEmp.addActionListener(e -> cardLayout.show(pnlCenterContent, "EMP"));
        btnFaculty.addActionListener(e -> cardLayout.show(pnlCenterContent, "FACULTY"));
        
        // --- SỬA Ở ĐÂY: Gọi refreshData() ---
        btnResearch.addActionListener(e -> {
            if (pnlResearch != null) pnlResearch.refreshData(); // Refresh danh sách nhân viên
            cardLayout.show(pnlCenterContent, "RESEARCH");
        });
        
        btnCV.addActionListener(e -> {
            if (pnlCV != null) pnlCV.refreshData(); // Refresh danh sách nhân viên
            cardLayout.show(pnlCenterContent, "CV");
        });

        btnSchedule.addActionListener(e -> {
            if(pnlSchedule != null) pnlSchedule.refreshStaffTable(); 
            cardLayout.show(pnlCenterContent, "SCHEDULE");
        });
        
        btnSalary.addActionListener(e -> cardLayout.show(pnlCenterContent, "SALARY"));

        pnlSidebar.add(btnHome); pnlSidebar.add(Box.createVerticalStrut(10)); 
        pnlSidebar.add(btnEmp); pnlSidebar.add(Box.createVerticalStrut(10));
        pnlSidebar.add(btnFaculty); pnlSidebar.add(Box.createVerticalStrut(10));
        pnlSidebar.add(btnResearch); pnlSidebar.add(Box.createVerticalStrut(10));
        pnlSidebar.add(btnCV); pnlSidebar.add(Box.createVerticalStrut(10));
        pnlSidebar.add(btnSchedule); pnlSidebar.add(Box.createVerticalStrut(10));
        pnlSidebar.add(btnSalary); 
        pnlSidebar.add(Box.createVerticalGlue()); 
        add(pnlSidebar, BorderLayout.WEST);

        // --- 3. CENTER ---
        cardLayout = new CardLayout();
        pnlCenterContent = new JPanel(cardLayout);
        pnlCenterContent.setBackground(Color.WHITE);
        
        DashboardPanel pnlDashboard = new DashboardPanel(); 
        EmployeePanel pnlEmployee = new EmployeePanel(this); 
        FacultyPanel pnlFaculty = new FacultyPanel();
        
        // KHỞI TẠO BIẾN LỚP (Không có chữ ResearchPanel/CVPanel ở đầu)
        pnlResearch = new ResearchPanel();
        pnlCV = new CVPanel();
        pnlSchedule = new SchedulePanel(); 
        
        SalaryPanel pnlSalary = new SalaryPanel();

        pnlCenterContent.add(pnlDashboard, "HOME");
        pnlCenterContent.add(pnlEmployee, "EMP");
        pnlCenterContent.add(pnlFaculty, "FACULTY");
        pnlCenterContent.add(pnlResearch, "RESEARCH");
        pnlCenterContent.add(pnlCV, "CV");
        pnlCenterContent.add(pnlSchedule, "SCHEDULE");
        pnlCenterContent.add(pnlSalary, "SALARY"); 
        
        add(pnlCenterContent, BorderLayout.CENTER);
    }

    private void styleButton(JButton btn) {
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); 
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
}