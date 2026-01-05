import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainDashboard extends JFrame {

    private JPanel pnlCenterContent; 
    private CardLayout cardLayout;
    
    // --- KHAI BÁO BIẾN LỚP ---
    private SchedulePanel pnlSchedule; 
    private ResearchPanel pnlResearch; 
    private CVPanel pnlCV;             
    private EvaluationPanel pnlEval; // Panel Thi đua

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

        JLabel lblTitle = new JLabel("UNIVERSITY HRM SYSTEM v2.0 | Group 13");
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
        JButton btnEval = new JButton("🏆 Thi Đua & Khen Thưởng");
        
        styleButton(btnHome); styleButton(btnEmp); styleButton(btnFaculty); 
        styleButton(btnResearch); styleButton(btnCV); 
        styleButton(btnSchedule); styleButton(btnSalary); styleButton(btnEval);

        // SỰ KIỆN CHUYỂN TAB
        btnHome.addActionListener(e -> cardLayout.show(pnlCenterContent, "HOME"));
        btnEmp.addActionListener(e -> cardLayout.show(pnlCenterContent, "EMP"));
        btnFaculty.addActionListener(e -> cardLayout.show(pnlCenterContent, "FACULTY"));
        
        btnResearch.addActionListener(e -> {
            if (pnlResearch != null) pnlResearch.refreshData(); 
            cardLayout.show(pnlCenterContent, "RESEARCH");
        });
        
        btnCV.addActionListener(e -> {
            if (pnlCV != null) pnlCV.refreshData(); 
            cardLayout.show(pnlCenterContent, "CV");
        });

        btnSchedule.addActionListener(e -> {
            if(pnlSchedule != null) pnlSchedule.refreshStaffTable(); 
            cardLayout.show(pnlCenterContent, "SCHEDULE");
        });
        
        btnSalary.addActionListener(e -> cardLayout.show(pnlCenterContent, "SALARY"));
        
        // --- SỬA LẠI ĐOẠN NÀY ---
        btnEval.addActionListener(e -> {
            if (pnlEval != null) pnlEval.refreshData();
            cardLayout.show(pnlCenterContent, "EVAL"); // Sửa thành "EVAL" (Code cũ là "RESEARCH")
        });
        // -------------------------

        pnlSidebar.add(btnHome); pnlSidebar.add(Box.createVerticalStrut(10)); 
        pnlSidebar.add(btnEmp); pnlSidebar.add(Box.createVerticalStrut(10));
        pnlSidebar.add(btnFaculty); pnlSidebar.add(Box.createVerticalStrut(10));
        pnlSidebar.add(btnResearch); pnlSidebar.add(Box.createVerticalStrut(10));
        pnlSidebar.add(btnCV); pnlSidebar.add(Box.createVerticalStrut(10));
        pnlSidebar.add(btnSchedule); pnlSidebar.add(Box.createVerticalStrut(10));
        pnlSidebar.add(btnSalary); pnlSidebar.add(Box.createVerticalStrut(10));
        pnlSidebar.add(btnEval); pnlSidebar.add(Box.createVerticalStrut(10));
        pnlSidebar.add(Box.createVerticalGlue()); 
        add(pnlSidebar, BorderLayout.WEST);

        // --- 3. CENTER ---
        cardLayout = new CardLayout();
        pnlCenterContent = new JPanel(cardLayout);
        pnlCenterContent.setBackground(Color.WHITE);
        
        DashboardPanel pnlDashboard = new DashboardPanel(); 
        EmployeePanel pnlEmployee = new EmployeePanel(this); 
        FacultyPanel pnlFaculty = new FacultyPanel();
        
        pnlResearch = new ResearchPanel();
        pnlCV = new CVPanel();
        pnlSchedule = new SchedulePanel(); 
        pnlEval = new EvaluationPanel();
        
        SalaryPanel pnlSalary = new SalaryPanel();

        // ADD VÀO CARD LAYOUT
        pnlCenterContent.add(pnlDashboard, "HOME");
        pnlCenterContent.add(pnlEmployee, "EMP");
        pnlCenterContent.add(pnlFaculty, "FACULTY");
        pnlCenterContent.add(pnlResearch, "RESEARCH");
        pnlCenterContent.add(pnlCV, "CV");
        pnlCenterContent.add(pnlSchedule, "SCHEDULE");
        pnlCenterContent.add(pnlSalary, "SALARY"); 
        pnlCenterContent.add(pnlEval, "EVAL"); // Tên thẻ tương ứng
        
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