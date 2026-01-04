import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class SchedulePanel extends JPanel {

    private JTable tblSchedule; 
    private JTable tblStaff;    
    private DefaultTableModel modelSchedule;
    
    private LocalDate currentMonday; 
    private JLabel lblDateRange;     
    private String selectedMaNV = null; 
    private String selectedTenNV = "";

    public SchedulePanel() {
        setLayout(new BorderLayout());
        
        // Khởi tạo ngày hiện tại
        currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // --- PHẦN 1: THANH ĐIỀU HƯỚNG ---
        JPanel pnlDateNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnPrev = new JButton("◀ Tuần trước");
        JButton btnNext = new JButton("Tuần sau ▶");
        JButton btnToday = new JButton("Tuần hiện tại");
        
        btnPrev.setBackground(Color.WHITE);
        btnNext.setBackground(Color.WHITE);
        btnToday.setBackground(new Color(230, 230, 230));

        lblDateRange = new JLabel();
        lblDateRange.setFont(new Font("Arial", Font.BOLD, 15));
        lblDateRange.setForeground(new Color(41, 128, 185));

        btnPrev.addActionListener(e -> changeWeek(-1));
        btnNext.addActionListener(e -> changeWeek(1));
        btnToday.addActionListener(e -> {
            currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            updateDateLabel();
            if(selectedMaNV != null) loadSchedule(selectedMaNV);
        });

        pnlDateNav.add(btnPrev); pnlDateNav.add(btnToday); pnlDateNav.add(btnNext);
        pnlDateNav.add(Box.createHorizontalStrut(20)); pnlDateNav.add(lblDateRange);
        
        updateDateLabel(); 

        // --- PHẦN 2: BẢNG TKB ---
        String[] columns = {"Tiết", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"};
        modelSchedule = new DefaultTableModel(new Object[15][8], columns) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tblSchedule = new JTable(modelSchedule);
        tblSchedule.setRowHeight(50); 
        tblSchedule.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        tblSchedule.setDefaultRenderer(Object.class, new MultiLineCellRenderer());
        tblSchedule.getColumnModel().getColumn(0).setMaxWidth(50);
        
        JScrollPane scrollSchedule = new JScrollPane(tblSchedule);
        JPanel pnlTopContainer = new JPanel(new BorderLayout());
        pnlTopContainer.add(pnlDateNav, BorderLayout.NORTH);
        pnlTopContainer.add(scrollSchedule, BorderLayout.CENTER);
        pnlTopContainer.setBorder(BorderFactory.createTitledBorder("THỜI KHÓA BIỂU CHI TIẾT"));

        // --- PHẦN 3: DANH SÁCH GIẢNG VIÊN ---
        tblStaff = new JTable();
        tblStaff.setRowHeight(25);
        tblStaff.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        refreshStaffTable(); // Load lần đầu

        JScrollPane scrollStaff = new JScrollPane(tblStaff);
        scrollStaff.setBorder(BorderFactory.createTitledBorder("DANH SÁCH GIẢNG VIÊN (Chọn để xem lịch)"));

        tblStaff.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblStaff.getSelectedRow();
                if (row != -1) {
                    selectedMaNV = tblStaff.getValueAt(row, 0).toString();
                    selectedTenNV = tblStaff.getValueAt(row, 1).toString();
                    loadSchedule(selectedMaNV);
                }
            }
        });

        // --- PHẦN 4: TOOLBAR ---
        JPanel pnlTools = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnAddLich = new JButton("➕ Phân Công");
        btnAddLich.setBackground(new Color(46, 204, 113)); btnAddLich.setForeground(Color.WHITE);
        btnAddLich.addActionListener(e -> {
            if(selectedMaNV == null) JOptionPane.showMessageDialog(this, "Vui lòng chọn Giảng viên trước!");
            else showAddScheduleDialog(selectedMaNV, selectedTenNV);
        });
        
        JButton btnExport = new JButton("📊 Xuất TKB ra Excel");
        btnExport.setBackground(new Color(39, 174, 96)); btnExport.setForeground(Color.WHITE);
        btnExport.addActionListener(e -> {
            if (selectedMaNV == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Giảng viên để xuất lịch!");
                return;
            }
            exportExcel();
        });

        pnlTools.add(btnAddLich); pnlTools.add(btnExport);

        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.add(pnlTools, BorderLayout.NORTH);
        pnlBottom.add(scrollStaff, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlTopContainer, pnlBottom);
        splitPane.setDividerLocation(400); splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);
        
        loadSchedule("");
    }
    
    // --- CÁC HÀM LOGIC ---

    private void changeWeek(int weeksToAdd) {
        currentMonday = currentMonday.plusWeeks(weeksToAdd);
        updateDateLabel();
        if (selectedMaNV != null) loadSchedule(selectedMaNV);
    }
    
    private void updateDateLabel() {
        LocalDate sunday = currentMonday.plusDays(6);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblDateRange.setText("Tuần: " + currentMonday.format(fmt) + "  ➜  " + sunday.format(fmt));
    }

    private void loadSchedule(String maNV) {
        String monStr = currentMonday.toString();
        String sunStr = currentMonday.plusDays(6).toString();
        Object[][] data = LichDayDAO.getScheduleMatrix(maNV, monStr, sunStr);
        String[] columns = {"Tiết", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"};
        modelSchedule.setDataVector(data, columns);
        tblSchedule.getColumnModel().getColumn(0).setMaxWidth(50);
        tblSchedule.setDefaultRenderer(Object.class, new MultiLineCellRenderer());
    }

    private void exportExcel() {
        try {
            LocalDate sunday = currentMonday.plusDays(6);
            DateTimeFormatter fmtFilename = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            
            // Tạo tên file gợi ý
            String fileName = selectedTenNV + " TKB " + currentMonday.format(fmtFilename) + " den " + sunday.format(fmtFilename) + ".xlsx";
            
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(fileName));
            
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                // --- SỬA DÒNG NÀY ---
                // Thêm tham số thứ 3 là tên Sheet (ví dụ: "ThoiKhoaBieu")
                ExcelExporter.exportToExcel(tblSchedule, fc.getSelectedFile(), "ThoiKhoaBieu");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + ex.getMessage());
        }
    }

    // --- QUAN TRỌNG: Đã đổi thành PUBLIC để MainDashboard gọi ---
    public void refreshStaffTable() {
        tblStaff.setModel(NhanSuDAO.getNhanVienModel());
        // Ẩn bớt cột không cần thiết
        // Thứ tự cột trong DAO: 0:Ma, 1:Ten, 2:NgaySinh, 3:PB, 4:CV, ...
        for(int i=4; i<tblStaff.getColumnCount(); i++) {
            tblStaff.getColumnModel().getColumn(i).setMinWidth(0);
            tblStaff.getColumnModel().getColumn(i).setMaxWidth(0);
            tblStaff.getColumnModel().getColumn(i).setWidth(0);
        }
    }
    
    private void showAddScheduleDialog(String maNV, String tenNV) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Xếp lịch: " + tenNV, true);
        d.setSize(450, 480); 
        d.setLocationRelativeTo(this);
        d.setLayout(new GridLayout(9, 2, 10, 10));
        
        JComboBox<String> cboMonHoc = new JComboBox<>();
        for(String mh : LichDayDAO.getDSMonHoc()) cboMonHoc.addItem(mh);
        
        JTextField txtPhong = new JTextField();
        JComboBox<String> cboThu = new JComboBox<>(new String[]{"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"});
        JTextField txtTiet = new JTextField();
        JTextField txtSoTiet = new JTextField();
        JTextField txtTuNgay = new JTextField(currentMonday.toString());
        JTextField txtDenNgay = new JTextField(currentMonday.plusMonths(4).toString());
        
        JButton btnLuu = new JButton("Lưu & Phân công");
        btnLuu.setBackground(new Color(46, 204, 113)); btnLuu.setForeground(Color.WHITE);
        
        d.add(new JLabel("  Môn học:")); d.add(cboMonHoc);
        d.add(new JLabel("  Phòng học:")); d.add(txtPhong);
        d.add(new JLabel("  Thứ:")); d.add(cboThu);
        d.add(new JLabel("  Tiết bắt đầu (1-15):")); d.add(txtTiet);
        d.add(new JLabel("  Số tiết dạy:")); d.add(txtSoTiet);
        d.add(new JLabel("  Từ ngày (yyyy-MM-dd):")); d.add(txtTuNgay);
        d.add(new JLabel("  Đến ngày (yyyy-MM-dd):")); d.add(txtDenNgay);
        d.add(new JLabel("")); d.add(btnLuu);
        
        btnLuu.addActionListener(ev -> {
            try {
                int thu = cboThu.getSelectedIndex() + 2;
                int tiet = Integer.parseInt(txtTiet.getText());
                int soTiet = Integer.parseInt(txtSoTiet.getText());
                String tenMon = cboMonHoc.getSelectedItem() != null ? cboMonHoc.getSelectedItem().toString() : "";
                
                if(LichDayDAO.addLichDay(maNV, tenMon, txtPhong.getText(), thu, tiet, soTiet, txtTuNgay.getText(), txtDenNgay.getText())){
                    JOptionPane.showMessageDialog(d, "Thành công!");
                    loadSchedule(maNV); d.dispose();
                }
            } catch(Exception ex) { JOptionPane.showMessageDialog(d, "Lỗi nhập liệu!"); }
        });
        d.setVisible(true);
    }

    class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
        public MultiLineCellRenderer() {
            setLineWrap(true); setWrapStyleWord(true); setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setBackground(isSelected ? table.getSelectionBackground() : (column==0 ? new Color(240,240,240) : Color.WHITE));
            setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
            return this;
        }
    }
}