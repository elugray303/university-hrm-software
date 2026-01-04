import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FacultyPanel extends JPanel {

    private JTable tblKhoa, tblMonHoc;
    private String selectedMaKhoa = null;

    public FacultyPanel() {
        setLayout(new GridLayout(1, 2, 10, 10)); // Chia đôi màn hình
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- BÊN TRÁI: QUẢN LÝ KHOA ---
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setBorder(BorderFactory.createTitledBorder("DANH SÁCH KHOA"));
        
        tblKhoa = new JTable();
        tblKhoa.setRowHeight(25);
        pnlLeft.add(new JScrollPane(tblKhoa), BorderLayout.CENTER);

        JPanel pnlKhoaTools = new JPanel(new FlowLayout());
        JButton btnAddK = new JButton("Thêm Khoa");
        JButton btnEditK = new JButton("Sửa Khoa");
        JButton btnDelK = new JButton("Xóa Khoa");
        pnlKhoaTools.add(btnAddK); pnlKhoaTools.add(btnEditK); pnlKhoaTools.add(btnDelK);
        pnlLeft.add(pnlKhoaTools, BorderLayout.SOUTH);

        // --- BÊN PHẢI: QUẢN LÝ MÔN HỌC ---
        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setBorder(BorderFactory.createTitledBorder("MÔN HỌC (Chọn Khoa để xem)"));
        
        tblMonHoc = new JTable();
        tblMonHoc.setRowHeight(25);
        pnlRight.add(new JScrollPane(tblMonHoc), BorderLayout.CENTER);

        JPanel pnlMonTools = new JPanel(new FlowLayout());
        JButton btnAddM = new JButton("Thêm Môn");
        JButton btnEditM = new JButton("Sửa Môn");
        JButton btnDelM = new JButton("Xóa Môn");
        pnlMonTools.add(btnAddM); pnlMonTools.add(btnEditM); pnlMonTools.add(btnDelM);
        pnlRight.add(pnlMonTools, BorderLayout.SOUTH);

        add(pnlLeft);
        add(pnlRight);

        // --- SỰ KIỆN ---
        loadKhoa();

        // 1. Click vào Khoa -> Load Môn học
        tblKhoa.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = tblKhoa.getSelectedRow();
                if(r != -1) {
                    selectedMaKhoa = tblKhoa.getValueAt(r, 0).toString();
                    String tenKhoa = tblKhoa.getValueAt(r, 1).toString();
                    pnlRight.setBorder(BorderFactory.createTitledBorder("MÔN HỌC THUỘC: " + tenKhoa.toUpperCase()));
                    loadMonHoc();
                }
            }
        });

        // 2. Thêm Khoa
        btnAddK.addActionListener(e -> {
            JTextField txtMa = new JTextField(); JTextField txtTen = new JTextField();
            Object[] message = {"Mã Khoa:", txtMa, "Tên Khoa:", txtTen};
            if(JOptionPane.showConfirmDialog(this, message, "Thêm Khoa", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                if(KhoaDAO.addKhoa(txtMa.getText(), txtTen.getText())) loadKhoa();
            }
        });

        // 3. Sửa Khoa
        btnEditK.addActionListener(e -> {
            int r = tblKhoa.getSelectedRow();
            if(r == -1) return;
            String oldMa = tblKhoa.getValueAt(r, 0).toString();
            String oldTen = tblKhoa.getValueAt(r, 1).toString();
            String newTen = JOptionPane.showInputDialog(this, "Tên mới:", oldTen);
            if(newTen != null && KhoaDAO.updateKhoa(oldMa, newTen)) loadKhoa();
        });

        // 4. Xóa Khoa
        btnDelK.addActionListener(e -> {
            int r = tblKhoa.getSelectedRow();
            if(r != -1 && JOptionPane.showConfirmDialog(this, "Xóa Khoa này (sẽ xóa cả môn học)?") == JOptionPane.YES_OPTION) {
                KhoaDAO.deleteKhoa(tblKhoa.getValueAt(r, 0).toString());
                loadKhoa();
                ((DefaultTableModel)tblMonHoc.getModel()).setRowCount(0); // Xóa bảng môn
            }
        });

        // 5. Thêm Môn
        btnAddM.addActionListener(e -> {
            if(selectedMaKhoa == null) { JOptionPane.showMessageDialog(this, "Chọn Khoa trước!"); return; }
            JTextField txtMa = new JTextField(); JTextField txtTen = new JTextField(); JTextField txtTC = new JTextField();
            Object[] message = {"Mã Môn:", txtMa, "Tên Môn:", txtTen, "Số TC:", txtTC};
            if(JOptionPane.showConfirmDialog(this, message, "Thêm Môn", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    if(KhoaDAO.addMonHoc(txtMa.getText(), txtTen.getText(), Integer.parseInt(txtTC.getText()), selectedMaKhoa)) 
                        loadMonHoc();
                } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Số TC phải là số!"); }
            }
        });

        // 6. Sửa Môn
        btnEditM.addActionListener(e -> {
            int r = tblMonHoc.getSelectedRow();
            if(r == -1) return;
            String ma = tblMonHoc.getValueAt(r, 0).toString();
            String ten = tblMonHoc.getValueAt(r, 1).toString();
            String tc = tblMonHoc.getValueAt(r, 2).toString();
            
            JTextField txtTen = new JTextField(ten); JTextField txtTC = new JTextField(tc);
            Object[] message = {"Tên Môn:", txtTen, "Số TC:", txtTC};
            if(JOptionPane.showConfirmDialog(this, message, "Sửa Môn", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    if(KhoaDAO.updateMonHoc(ma, txtTen.getText(), Integer.parseInt(txtTC.getText()))) 
                        loadMonHoc();
                } catch(Exception ex) {}
            }
        });

        // 7. Xóa Môn
        btnDelM.addActionListener(e -> {
            int r = tblMonHoc.getSelectedRow();
            if(r != -1 && JOptionPane.showConfirmDialog(this, "Xóa Môn này?") == JOptionPane.YES_OPTION) {
                if(KhoaDAO.deleteMonHoc(tblMonHoc.getValueAt(r, 0).toString())) loadMonHoc();
            }
        });
    }

    private void loadKhoa() { tblKhoa.setModel(KhoaDAO.getDSKhoa()); }
    private void loadMonHoc() { if(selectedMaKhoa != null) tblMonHoc.setModel(KhoaDAO.getDSMonHoc(selectedMaKhoa)); }
}