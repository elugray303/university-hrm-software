import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

public class ResearchPanel extends JPanel {

    private JTable tblStaff;
    private JTable tblResearch;
    private String selectedMaNV = null;
    private JLabel lblCurrentStaff;

    public ResearchPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- LEFT: DANH SÁCH GIẢNG VIÊN ---
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setPreferredSize(new Dimension(300, 0));
        pnlLeft.setBorder(BorderFactory.createTitledBorder("1. Chọn Giảng Viên"));

        tblStaff = new JTable();
        // LƯU Ý: Đã xóa dòng refreshData() ở đây để tránh lỗi NullPointer
        
        tblStaff.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = tblStaff.getSelectedRow();
                if (r != -1) {
                    selectedMaNV = tblStaff.getValueAt(r, 0).toString();
                    String ten = tblStaff.getValueAt(r, 1).toString();
                    lblCurrentStaff.setText("Đang xem: " + ten.toUpperCase());
                    loadResearchData();
                }
            }
        });
        pnlLeft.add(new JScrollPane(tblStaff), BorderLayout.CENTER);

        // --- RIGHT: QUẢN LÝ NCKH ---
        JPanel pnlRight = new JPanel(new BorderLayout(0, 10));
        pnlRight.setBorder(BorderFactory.createTitledBorder("2. Danh Sách Công Trình / Bài Báo"));

        JPanel pnlRightTop = new JPanel(new BorderLayout());
        
        // --- KHỞI TẠO LABEL Ở ĐÂY ---
        lblCurrentStaff = new JLabel("Vui lòng chọn giảng viên bên trái...");
        lblCurrentStaff.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCurrentStaff.setForeground(new Color(41, 128, 185));
        
        JButton btnAdd = new JButton("➕ Thêm Đề Tài Mới");
        btnAdd.setBackground(new Color(46, 204, 113)); btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> showAddDialog());
        
        JButton btnDel = new JButton("❌ Xóa");
        btnDel.setBackground(new Color(231, 76, 60)); btnDel.setForeground(Color.WHITE);
        btnDel.addActionListener(e -> deleteResearch());

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBtns.add(btnAdd); pnlBtns.add(btnDel);

        pnlRightTop.add(lblCurrentStaff, BorderLayout.WEST);
        pnlRightTop.add(pnlBtns, BorderLayout.EAST);

        tblResearch = new JTable();
        tblResearch.setRowHeight(25);
        
        pnlRight.add(pnlRightTop, BorderLayout.NORTH);
        pnlRight.add(new JScrollPane(tblResearch), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlLeft, pnlRight);
        split.setDividerLocation(300);
        add(split, BorderLayout.CENTER);

        // --- SỬA LỖI: CHUYỂN refreshData() XUỐNG CUỐI CÙNG ---
        // Lúc này các biến giao diện (lblCurrentStaff, tblResearch) đã được khởi tạo xong
        refreshData(); 
    }

    // --- HÀM LÀM MỚI DANH SÁCH (CÓ PHÂN QUYỀN) ---
    public void refreshData() {
        if (Auth.isGiangVien() && Auth.maNV != null) {
            // Nếu là GV: Chỉ load chính mình
            tblStaff.setModel(NhanSuDAO.getNhanVienByMa(Auth.maNV));
        } else {
            // Nếu là Admin: Load hết
            tblStaff.setModel(NhanSuDAO.getNhanVienModel());
        }

        // Ẩn cột không cần thiết
        if (tblStaff.getColumnCount() > 2) {
            for(int i=2; i<tblStaff.getColumnCount(); i++) {
                tblStaff.getColumnModel().getColumn(i).setMinWidth(0);
                tblStaff.getColumnModel().getColumn(i).setMaxWidth(0);
            }
        }
        
        // Tự động chọn dòng đầu tiên nếu là Giảng viên (đỡ phải click)
        if (Auth.isGiangVien() && tblStaff.getRowCount() > 0) {
            tblStaff.setRowSelectionInterval(0, 0);
            selectedMaNV = tblStaff.getValueAt(0, 0).toString();
            String ten = tblStaff.getValueAt(0, 1).toString();
            
            // Lệnh này gây lỗi cũ nếu chạy trước khi lblCurrentStaff khởi tạo
            if (lblCurrentStaff != null) { 
                lblCurrentStaff.setText("Đang xem: " + ten.toUpperCase());
            }
            loadResearchData();
        }
    }

    private void loadResearchData() {
        if (selectedMaNV != null) {
            tblResearch.setModel(NghienCuuDAO.getListNCKH(selectedMaNV));
            if(tblResearch.getColumnCount() > 0) {
                tblResearch.getColumnModel().getColumn(0).setMinWidth(0);
                tblResearch.getColumnModel().getColumn(0).setMaxWidth(0);
            }
        }
    }

    private void deleteResearch() {
        int r = tblResearch.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Chọn đề tài cần xóa!");
            return;
        }
        int id = Integer.parseInt(tblResearch.getValueAt(r, 0).toString());
        if (JOptionPane.showConfirmDialog(this, "Xóa đề tài này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (NghienCuuDAO.deleteNCKH(id)) {
                loadResearchData();
                JOptionPane.showMessageDialog(this, "Đã xóa!");
            }
        }
    }

    private void showAddDialog() {
        if (selectedMaNV == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn giảng viên!");
            return;
        }
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm NCKH", true);
        d.setSize(400, 350);
        d.setLocationRelativeTo(this);
        d.setLayout(new GridLayout(6, 1, 10, 10));
        
        JTextField txtTen = new JTextField();
        String[] loais = {"Bài báo Quốc tế (ISI/Scopus)", "Bài báo Trong nước", "Sách chuyên khảo", "Đề tài cấp Trường", "Hướng dẫn NCS"};
        JComboBox<String> cboLoai = new JComboBox<>(loais);
        JTextField txtNgay = new JTextField(LocalDate.now().toString());
        JTextField txtDiem = new JTextField("1.0");

        d.add(createInput("Tên Đề tài/Bài báo:", txtTen));
        d.add(createInput("Loại hình:", cboLoai));
        d.add(createInput("Ngày công bố (yyyy-mm-dd):", txtNgay));
        d.add(createInput("Điểm thưởng / Quy đổi:", txtDiem));
        
        JButton btnSave = new JButton("Lưu Dữ Liệu");
        btnSave.setBackground(new Color(52, 152, 219)); btnSave.setForeground(Color.WHITE);
        
        btnSave.addActionListener(e -> {
            try {
                double diem = Double.parseDouble(txtDiem.getText());
                if (NghienCuuDAO.addNCKH(selectedMaNV, txtTen.getText(), cboLoai.getSelectedItem().toString(), txtNgay.getText(), diem)) {
                    JOptionPane.showMessageDialog(d, "Thêm thành công!");
                    loadResearchData();
                    d.dispose();
                } else {
                    JOptionPane.showMessageDialog(d, "Lỗi lưu dữ liệu!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Lỗi nhập liệu: " + ex.getMessage());
            }
        });
        
        JPanel pnlBot = new JPanel(); pnlBot.add(btnSave);
        d.add(pnlBot);
        d.setVisible(true);
    }

    private JPanel createInput(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(new EmptyBorder(0, 20, 0, 20));
        p.add(new JLabel(title), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }
}