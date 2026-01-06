import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class MainDashboard extends JFrame {

    // --- 1. CONFIG MÀU SẮC ---
    private final Color COL_GRADIENT_START = new Color(33, 47, 61); 
    private final Color COL_GRADIENT_END = new Color(44, 62, 80);   
    
    private final Color COL_HEADER_BG = new Color(41, 128, 185); 
    private final Color COL_HEADER_TEXT = Color.WHITE;

    private final Color COL_BTN_HOVER = new Color(255, 255, 255, 20); 
    private final Color COL_BTN_ACTIVE = new Color(52, 152, 219);     
    private final Color COL_TEXT_IDLE = new Color(189, 195, 199);     
    private final Color COL_TEXT_ACTIVE = Color.WHITE;                

    // --- 2. COMPONENTS ---
    private JPanel pnlCenterContent; 
    private CardLayout cardLayout;
    
    // Khai báo các Panel ở cấp độ Class để có thể gọi refresh
    private DashboardPanel pnlDashboard; // <-- QUAN TRỌNG
    private SchedulePanel pnlSchedule; 
    private ResearchPanel pnlResearch; 
    private CVPanel pnlCV;             
    private EvaluationPanel pnlEval; 
    private AccountPanel pnlAccount; 

    private List<ModernSidebarButton> sidebarButtons = new ArrayList<>();

    public MainDashboard() {
        String windowTitle = "HỆ THỐNG QUẢN LÝ ĐÀO TẠO - " + (Auth.user != null ? Auth.user.toUpperCase() : "GUEST");
        setTitle(windowTitle);
        setSize(1300, 800); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ====================================================================
        // A. KHỞI TẠO PANEL
        // ====================================================================
        cardLayout = new CardLayout();
        pnlCenterContent = new JPanel(cardLayout);
        pnlCenterContent.setBackground(new Color(240, 242, 245)); 

        // Khởi tạo các panel (gán vào biến toàn cục)
        pnlDashboard = new DashboardPanel(); 
        EmployeePanel pnlEmployee = new EmployeePanel(this); 
        FacultyPanel pnlFaculty = new FacultyPanel();
        pnlResearch = new ResearchPanel();
        pnlCV = new CVPanel();
        pnlSchedule = new SchedulePanel(); 
        pnlEval = new EvaluationPanel();
        SalaryPanel pnlSalary = new SalaryPanel();
        pnlAccount = new AccountPanel();

        // Add vào CardLayout
        pnlCenterContent.add(pnlDashboard, "HOME");
        pnlCenterContent.add(pnlEmployee, "EMP");
        pnlCenterContent.add(pnlFaculty, "FACULTY");
        pnlCenterContent.add(pnlResearch, "RESEARCH");
        pnlCenterContent.add(pnlCV, "CV");
        pnlCenterContent.add(pnlSchedule, "SCHEDULE");
        pnlCenterContent.add(pnlSalary, "SALARY"); 
        pnlCenterContent.add(pnlEval, "EVAL");
        pnlCenterContent.add(pnlAccount, "ACCOUNT");

        // ====================================================================
        // B. HEADER
        // ====================================================================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(COL_HEADER_BG); 
        pnlHeader.setPreferredSize(new Dimension(0, 60)); 
        pnlHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(31, 97, 141)));

        // UNICODE FIX
        String roleAdmin = "Qu\u1EA3n Tr\u1ECA Vi\u00AAn"; 
        String roleGV    = "Gi\u1EA3ng Vi\u00AAn";       
        String hello     = "Xin ch\u00E0o, ";            

        String roleDisplay = Auth.isAdmin() ? roleAdmin : roleGV;
        JLabel lblUser = new JLabel(hello + Auth.user + " (" + roleDisplay + ")");
        
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        lblUser.setForeground(COL_HEADER_TEXT);
        lblUser.setIcon(new TextIcon("👤", 24, COL_HEADER_TEXT)); 
        lblUser.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel pnlUserWrapper = new JPanel(new GridBagLayout()); 
        pnlUserWrapper.setOpaque(false);
        pnlUserWrapper.add(lblUser);

        pnlHeader.add(pnlUserWrapper, BorderLayout.CENTER);
        
        JLabel lblRightIcon = new JLabel("UniManager v2.0 ");
        lblRightIcon.setForeground(new Color(255,255,255, 150));
        lblRightIcon.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblRightIcon.setBorder(new EmptyBorder(0,0,0,15));
        pnlHeader.add(lblRightIcon, BorderLayout.EAST);
        
        add(pnlHeader, BorderLayout.NORTH);

        // ====================================================================
        // C. SIDEBAR
        // ====================================================================
        GradientPanel pnlSidebar = new GradientPanel();
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));
        pnlSidebar.setPreferredSize(new Dimension(250, 0));
        
        JPanel pnlLogo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20)); 
        pnlLogo.setOpaque(false);
        pnlLogo.setAlignmentX(Component.LEFT_ALIGNMENT); 
        
        JLabel lblLogo = new JLabel("HRM SYSTEM"); 
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setIcon(new TextIcon("🎓", 28, Color.WHITE)); 
        
        pnlLogo.add(lblLogo);
        pnlSidebar.add(pnlLogo);
        pnlSidebar.add(Box.createVerticalStrut(20));

        // --- BUTTONS ---
        
        // [QUAN TRỌNG] Sự kiện nút Dashboard: Gọi refreshData()
        addSidebarBtn(pnlSidebar, "Dashboard", "📊", e -> {
            if(pnlDashboard != null) pnlDashboard.refreshData(); // Refresh biểu đồ
            cardLayout.show(pnlCenterContent, "HOME");
        });
        
        if (Auth.isAdmin()) {
            addGroupTitle(pnlSidebar, "QUẢN TRỊ");
            addSidebarBtn(pnlSidebar, "Nhân Sự", "👥", e -> cardLayout.show(pnlCenterContent, "EMP"));
            
            addSidebarBtn(pnlSidebar, "Tài Khoản", "🔑", e -> {
                if(pnlAccount != null) pnlAccount.refreshData();
                cardLayout.show(pnlCenterContent, "ACCOUNT");
            });
            
            addSidebarBtn(pnlSidebar, "Khoa & Môn", "🏛️", e -> cardLayout.show(pnlCenterContent, "FACULTY"));
        }

        addGroupTitle(pnlSidebar, "CHUYÊN MÔN");
        addSidebarBtn(pnlSidebar, "Nghiên Cứu KH", "🔬", e -> {
            if(pnlResearch != null) pnlResearch.refreshData();
            cardLayout.show(pnlCenterContent, "RESEARCH");
        });
        
        addSidebarBtn(pnlSidebar, "Hồ Sơ Khoa Học", "📄", e -> {
            if(pnlCV != null) pnlCV.refreshData();
            cardLayout.show(pnlCenterContent, "CV");
        });
        
        addSidebarBtn(pnlSidebar, "Lịch Giảng Dạy", "📅", e -> {
            if(pnlSchedule != null) pnlSchedule.refreshStaffTable();
            cardLayout.show(pnlCenterContent, "SCHEDULE");
        });

        if (Auth.isAdmin()) {
            addGroupTitle(pnlSidebar, "TÀI CHÍNH");
            addSidebarBtn(pnlSidebar, "Quản Lý Lương", "💰", e -> cardLayout.show(pnlCenterContent, "SALARY"));
            addSidebarBtn(pnlSidebar, "Thi Đua & KPI", "🏆", e -> {
                if(pnlEval != null) pnlEval.refreshData();
                cardLayout.show(pnlCenterContent, "EVAL");
            });
        }
        
        pnlSidebar.add(Box.createVerticalGlue());

        ModernSidebarButton btnLogout = new ModernSidebarButton("Đăng xuất", "🚪");
        btnLogout.setForeground(new Color(255, 107, 107)); 
        btnLogout.addActionListener(e -> {
            if(JOptionPane.showConfirmDialog(this, "Bạn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                Auth.clear(); 
                new LoginForm().setVisible(true); 
                this.dispose(); 
            }
        });
        pnlSidebar.add(btnLogout);
        pnlSidebar.add(Box.createVerticalStrut(20)); 

        add(pnlSidebar, BorderLayout.WEST);
        add(pnlCenterContent, BorderLayout.CENTER);

        if (!sidebarButtons.isEmpty()) sidebarButtons.get(0).setActive(true);
    }

    // --- HELPER FUNCTIONS ---
    private void addGroupTitle(JPanel panel, String title) {
        panel.add(Box.createVerticalStrut(10));
        JLabel lbl = new JLabel(title);
        lbl.setForeground(new Color(127, 140, 141)); 
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setBorder(new EmptyBorder(5, 20, 5, 0)); 
        
        JPanel p = new JPanel(new BorderLayout()); 
        p.setOpaque(false); 
        p.add(lbl, BorderLayout.WEST);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(p);
    }

    private void addSidebarBtn(JPanel panel, String text, String icon, java.awt.event.ActionListener action) {
        ModernSidebarButton btn = new ModernSidebarButton(text, icon);
        btn.addActionListener(e -> {
            for (ModernSidebarButton b : sidebarButtons) b.setActive(false);
            btn.setActive(true);
            action.actionPerformed(e);
        });
        sidebarButtons.add(btn);
        panel.add(btn);
    }

    // --- CUSTOM UI CLASSES ---
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, COL_GRADIENT_START, 0, getHeight(), COL_GRADIENT_END);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    class ModernSidebarButton extends JButton {
        private boolean isActive = false;
        private boolean isHover = false;
        private String iconSymbol;

        public ModernSidebarButton(String text, String iconSymbol) {
            super(text);
            this.iconSymbol = iconSymbol;
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(COL_TEXT_IDLE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setBorder(new EmptyBorder(12, 15, 12, 10)); 
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHover = false; repaint(); }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            if (active) {
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setForeground(COL_TEXT_ACTIVE);
            } else {
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setForeground(COL_TEXT_IDLE);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                g2.setColor(COL_BTN_ACTIVE);
                g2.fillRect(0, 0, 4, getHeight()); 
                GradientPaint gp = new GradientPaint(0, 0, new Color(52, 152, 219, 40), getWidth(), 0, new Color(52, 152, 219, 0));
                g2.setPaint(gp);
                g2.fillRect(4, 0, getWidth()-4, getHeight());
            } else if (isHover) {
                g2.setColor(COL_BTN_HOVER);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            super.paintComponent(g); 
        }
        
        @Override
        public void setText(String text) {
            if (iconSymbol != null) super.setText(String.format("%-4s  %s", iconSymbol, text)); 
            else super.setText(text);
        }
    }
    
    // NÂNG CẤP TEXT ICON ĐỂ KHÔNG BỊ KHUYẾT CHỮ KHI ICON TO
    class TextIcon implements Icon {
        String str; 
        int size; 
        Color color;
        
        public TextIcon(String s, int sz, Color c) { 
            str = s; 
            size = sz; 
            color = c; 
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            g2.setColor(color);
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size));
            
            FontMetrics fm = g2.getFontMetrics();
            int dy = y + (getIconHeight() - fm.getHeight()) / 2 + fm.getAscent();
            int dx = x + (getIconWidth() - fm.stringWidth(str)) / 2;
            
            g2.drawString(str, dx, dy);
            g2.dispose();
        }
        
        @Override
        public int getIconWidth() { return size + 4; }
        
        @Override
        public int getIconHeight() { return size + 4; }
    }
}