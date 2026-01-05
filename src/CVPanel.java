import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;

public class CVPanel extends JPanel {

    private JTable tblStaff;
    private JTable tblHistory;
    private String selectedMaNV = null;
    private String selectedTenNV = "";
    
    // Thông tin cơ bản
    private String currentTrinhDo = "";
    private String currentChucVu = "";

    public CVPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- LEFT: LIST NV ---
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setPreferredSize(new Dimension(280, 0));
        pnlLeft.setBorder(BorderFactory.createTitledBorder("1. Chọn Giảng Viên"));
        
        tblStaff = new JTable();
        refreshData(); 

        tblStaff.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = tblStaff.getSelectedRow();
                if(r != -1) {
                    selectedMaNV = tblStaff.getValueAt(r, 0).toString();
                    selectedTenNV = tblStaff.getValueAt(r, 1).toString();
                    try {
                        // Lấy chức vụ/trình độ từ các cột ẩn (nếu có)
                        if (tblStaff.getColumnCount() > 5) {
                            currentChucVu = tblStaff.getModel().getValueAt(r, 4).toString(); 
                            currentTrinhDo = tblStaff.getModel().getValueAt(r, 5).toString();
                        }
                    } catch (Exception ex) { }
                    loadHistory();
                }
            }
        });
        pnlLeft.add(new JScrollPane(tblStaff), BorderLayout.CENTER);

        // --- CENTER: QUÁ TRÌNH CÔNG TÁC (MỤC II) ---
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBorder(BorderFactory.createTitledBorder("2. Quá Trình Đào Tạo & Công Tác (Mục II)"));
        
        tblHistory = new JTable();
        tblHistory.setRowHeight(25);
        pnlCenter.add(new JScrollPane(tblHistory), BorderLayout.CENTER);
        
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("➕ Thêm mốc thời gian");
        JButton btnDel = new JButton("❌ Xóa dòng chọn");
        
        btnAdd.addActionListener(e -> showAddDialog());
        btnDel.addActionListener(e -> deleteHistory());
        
        pnlAction.add(btnAdd); pnlAction.add(btnDel);
        pnlCenter.add(pnlAction, BorderLayout.SOUTH);

        // --- RIGHT: XUẤT FILE (TỔNG HỢP CẢ II VÀ III) ---
        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setPreferredSize(new Dimension(300, 0));
        pnlRight.setBorder(BorderFactory.createTitledBorder("3. Lý Lịch Khoa Học"));
        
        JPanel pnlExportInfo = new JPanel(new GridLayout(0, 1, 10, 10));
        pnlExportInfo.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel lblIcon = new JLabel("📄");
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 60));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblDesc = new JLabel("<html><center>Hệ thống sẽ tự động ghép nối:<br>" +
                "- Mục I: Thông tin cá nhân<br>" +
                "- Mục II: Quá trình công tác (Nhập ở giữa)<br>" +
                "- Mục III: Công trình NCKH (Lấy từ tab NCKH)</center></html>");
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton btnPreview = new JButton("XEM TRƯỚC & XUẤT FILE");
        btnPreview.setBackground(new Color(52, 152, 219)); btnPreview.setForeground(Color.WHITE);
        btnPreview.setFont(new Font("Arial", Font.BOLD, 14));
        btnPreview.setPreferredSize(new Dimension(0, 50));
        btnPreview.addActionListener(e -> generateCV());

        pnlExportInfo.add(lblIcon); pnlExportInfo.add(lblDesc); pnlExportInfo.add(btnPreview);
        pnlRight.add(pnlExportInfo, BorderLayout.CENTER);

        add(pnlLeft, BorderLayout.WEST);
        add(pnlCenter, BorderLayout.CENTER);
        add(pnlRight, BorderLayout.EAST);
    }

    public void refreshData() {
        tblStaff.setModel(NhanSuDAO.getNhanVienModel());
        if (tblStaff.getColumnCount() > 2) {
            for(int i=2; i<tblStaff.getColumnCount(); i++) {
                tblStaff.getColumnModel().getColumn(i).setMinWidth(0);
                tblStaff.getColumnModel().getColumn(i).setMaxWidth(0);
            }
        }
    }

    private void loadHistory() {
        if(selectedMaNV != null) {
            tblHistory.setModel(QuaTrinhDAO.getQuaTrinh(selectedMaNV));
            if(tblHistory.getColumnCount() > 0) tblHistory.getColumnModel().getColumn(0).setMaxWidth(0);
        }
    }

    private void deleteHistory() {
        int r = tblHistory.getSelectedRow();
        if (r == -1) return;
        int id = Integer.parseInt(tblHistory.getValueAt(r, 0).toString());
        if(QuaTrinhDAO.deleteQuaTrinh(id)) loadHistory();
    }

    // --- FORM NHẬP LIỆU THỦ CÔNG (Cho Mục II) ---
    private void showAddDialog() {
        if(selectedMaNV == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên trước!");
            return;
        }
        
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Quá Trình Công Tác", true);
        d.setSize(400, 300);
        d.setLocationRelativeTo(this);
        
        JPanel pContent = new JPanel(new GridLayout(0, 1, 10, 10));
        pContent.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField txtTime = new JTextField();
        JTextField txtPlace = new JTextField();
        JTextField txtRole = new JTextField();
        
        pContent.add(new JLabel("Thời gian (VD: 2010-2014):"));
        pContent.add(txtTime);
        
        pContent.add(new JLabel("Đơn vị / Trường học:"));
        pContent.add(txtPlace);
        
        pContent.add(new JLabel("Chức vụ / Văn bằng:"));
        pContent.add(txtRole);

        JButton btnSave = new JButton("LƯU VÀO MỤC II");
        btnSave.setBackground(new Color(52, 152, 219));
        btnSave.setForeground(Color.WHITE);
        
        btnSave.addActionListener(e -> {
            if(txtTime.getText().isEmpty() || txtPlace.getText().isEmpty()) {
                JOptionPane.showMessageDialog(d, "Vui lòng nhập đủ thông tin!");
                return;
            }
            if(QuaTrinhDAO.addQuaTrinh(selectedMaNV, txtTime.getText(), txtPlace.getText(), txtRole.getText(), "")) {
                loadHistory();
                d.dispose();
            } else {
                JOptionPane.showMessageDialog(d, "Lỗi khi lưu!");
            }
        });
        
        JPanel pBot = new JPanel(); pBot.add(btnSave);
        d.add(pContent, BorderLayout.CENTER);
        d.add(pBot, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // --- TỔNG HỢP CV TỰ ĐỘNG ---
    private void generateCV() {
        if(selectedMaNV == null) return;
        
        // 1. Lấy dữ liệu Mục II (Quá trình nhập tay)
        DefaultTableModel modelQT = QuaTrinhDAO.getQuaTrinh(selectedMaNV);
        
        // 2. Lấy dữ liệu Mục III (NCKH tự động từ tab bên kia)
        // Không cần nhập lại, hệ thống tự query DB để lấy cái mới nhất
        DefaultTableModel modelNCKH = NghienCuuDAO.getListNCKH(selectedMaNV);

        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial; padding: 30px;'>");
        html.append("<h1 style='color: #2c3e50; text-align: center;'>LÝ LỊCH KHOA HỌC</h1><hr>");
        
        // I. THÔNG TIN CHUNG
        html.append("<h3>I. THÔNG TIN CHUNG</h3>");
        html.append("<p><b>Họ và tên:</b> ").append(selectedTenNV.toUpperCase()).append("</p>");
        html.append("<p><b>Mã cán bộ:</b> ").append(selectedMaNV).append("</p>");
        html.append("<p><b>Học vị/Trình độ:</b> ").append(currentTrinhDo).append("</p>");
        html.append("<p><b>Chức vụ hiện tại:</b> ").append(currentChucVu).append("</p>");

        // II. QUÁ TRÌNH ĐÀO TẠO & CÔNG TÁC (Lấy từ bảng tblHistory)
        html.append("<h3>II. QUÁ TRÌNH ĐÀO TẠO & CÔNG TÁC</h3>");
        if (modelQT.getRowCount() > 0) {
            html.append("<table border='1' cellspacing='0' cellpadding='5' width='100%' style='border-collapse: collapse;'>");
            html.append("<tr style='background-color: #ecf0f1;'><th>Thời gian</th><th>Đơn vị / Trường học</th><th>Chức vụ / Văn bằng</th></tr>");
            for(int i=0; i<modelQT.getRowCount(); i++) {
                html.append("<tr>");
                html.append("<td>").append(modelQT.getValueAt(i, 1)).append("</td>"); // Thời gian
                html.append("<td>").append(modelQT.getValueAt(i, 2)).append("</td>"); // Đơn vị
                html.append("<td>").append(modelQT.getValueAt(i, 3)).append("</td>"); // Chức vụ
                html.append("</tr>");
            }
            html.append("</table>");
        } else {
            html.append("<p><i>(Chưa cập nhật quá trình công tác)</i></p>");
        }

        // III. CÔNG TRÌNH KHOA HỌC (Lấy tự động từ NCKH)
        html.append("<h3>III. CÔNG TRÌNH KHOA HỌC ĐÃ CÔNG BỐ</h3>");
        if (modelNCKH.getRowCount() > 0) {
            html.append("<ul>");
            for(int i=0; i<modelNCKH.getRowCount(); i++) {
                String tenBai = modelNCKH.getValueAt(i, 1).toString();
                String loai = modelNCKH.getValueAt(i, 2).toString();
                String ngay = modelNCKH.getValueAt(i, 3).toString();
                
                // Format: [2025-01-01] Tên bài báo (Loại hình)
                html.append("<li style='margin-bottom: 5px;'>")
                    .append("<b>[").append(ngay).append("]</b> ")
                    .append(tenBai)
                    .append(" <i>(").append(loai).append(")</i>")
                    .append("</li>");
            }
            html.append("</ul>");
        } else {
            html.append("<p><i>Chưa có công trình nào được ghi nhận trong hệ thống.</i></p>");
        }
        
        // NGÀY THÁNG HIỆN TẠI
        LocalDate now = LocalDate.now();
        html.append("<br><br><div style='text-align: right; margin-right: 50px;'>");
        html.append("<i>Hà Nội, ngày ").append(now.getDayOfMonth())
            .append(" tháng ").append(now.getMonthValue())
            .append(" năm ").append(now.getYear())
            .append("</i><br><br><br><b>Người khai</b><br>").append(selectedTenNV);
        html.append("</div>");
        html.append("</body></html>");

        // HIỂN THỊ PREVIEW
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Xem trước CV: " + selectedTenNV, true);
        d.setSize(750, 850);
        d.setLocationRelativeTo(this);
        JEditorPane ed = new JEditorPane("text/html", html.toString());
        ed.setEditable(false);
        d.add(new JScrollPane(ed));
        
        JPanel pBot = new JPanel();
        JButton btnExport = new JButton("Lưu ra file HTML");
        btnExport.addActionListener(ev -> {
            try {
                JFileChooser fc = new JFileChooser();
                fc.setSelectedFile(new File("CV_" + selectedMaNV + ".html"));
                if(fc.showSaveDialog(d) == JFileChooser.APPROVE_OPTION) {
                    FileWriter fw = new FileWriter(fc.getSelectedFile());
                    fw.write(html.toString());
                    fw.close();
                    JOptionPane.showMessageDialog(d, "Lưu thành công!");
                }
            } catch(Exception ex) { ex.printStackTrace(); }
        });
        pBot.add(btnExport);
        d.add(pBot, BorderLayout.SOUTH);
        d.setVisible(true);
    }
}