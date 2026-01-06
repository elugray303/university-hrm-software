import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

public class AccountPanel extends JPanel {

    private JTable tblAcc;
    private JTextField txtUser, txtPass;
    private JComboBox<String> cboRole;
    private JComboBox<String> cboNhanVien;
    private JButton btnAdd, btnDel, btnReset;

    public AccountPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        //1. FORM NHẬP LIỆU
        JPanel pnlTop = new JPanel(new GridLayout(2, 4, 10, 10));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thông tin tài khoản"));
        
        txtUser = new JTextField();
        txtPass = new JTextField();
        
        String[] roles = {"GiangVien", "Admin"};
        cboRole = new JComboBox<>(roles);
        
        cboNhanVien = new JComboBox<>();
        loadCboNhanVien(); // Load danh sách nhân viên vào combobox

        pnlTop.add(new JLabel("Tên Đăng Nhập:")); pnlTop.add(txtUser);
        pnlTop.add(new JLabel("Mật Khẩu:")); pnlTop.add(txtPass);
        pnlTop.add(new JLabel("Phân Quyền:")); pnlTop.add(cboRole);
        pnlTop.add(new JLabel("Gán cho NV:")); pnlTop.add(cboNhanVien);

        //2. BUTTONS
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("➕ Thêm Tài Khoản");
        btnDel = new JButton("❌ Xóa");
        btnReset = new JButton("🔄 Đặt lại mật khẩu (123)");
        
        // Style
        btnAdd.setBackground(new Color(46, 204, 113)); btnAdd.setForeground(Color.WHITE);
        btnDel.setBackground(new Color(231, 76, 60)); btnDel.setForeground(Color.WHITE);
        btnReset.setBackground(new Color(52, 152, 219)); btnReset.setForeground(Color.WHITE);

        pnlBtn.add(btnAdd); pnlBtn.add(btnDel); pnlBtn.add(btnReset);

        JPanel pnlNorth = new JPanel(new BorderLayout());
        pnlNorth.add(pnlTop, BorderLayout.CENTER);
        pnlNorth.add(pnlBtn, BorderLayout.SOUTH);

        //3. TABLE (CENTER)
        tblAcc = new JTable();
        tblAcc.setRowHeight(25);
        refreshData();
        
        // Sự kiện click bảng -> Đổ dữ liệu lên form
        tblAcc.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = tblAcc.getSelectedRow();
                if(r != -1) {
                    txtUser.setText(tblAcc.getValueAt(r, 0).toString());
                    txtPass.setText(tblAcc.getValueAt(r, 1).toString());
                    cboRole.setSelectedItem(tblAcc.getValueAt(r, 2).toString());
                    // Cố gắng chọn đúng nhân viên trong combobox
                    String maNV = tblAcc.getValueAt(r, 3) != null ? tblAcc.getValueAt(r, 3).toString() : "";
                    setSelectedNhanVien(maNV);
                }
            }
        });

        add(pnlNorth, BorderLayout.NORTH);
        add(new JScrollPane(tblAcc), BorderLayout.CENTER);

        //EVENTS
        btnAdd.addActionListener(e -> {
            String u = txtUser.getText();
            String p = txtPass.getText();
            String r = cboRole.getSelectedItem().toString();
            String nvString = cboNhanVien.getSelectedItem().toString();
            String maNV = nvString.split(" - ")[0]; // Lấy mã từ chuỗi "MaNV - TenNV"

            if(u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ User/Pass!");
                return;
            }
            if(TaiKhoanDAO.addTaiKhoan(u, p, r, maNV)) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi! Có thể tên đăng nhập đã tồn tại.");
            }
        });

        btnDel.addActionListener(e -> {
            String u = txtUser.getText();
            if(u.isEmpty()) return;
            if(JOptionPane.showConfirmDialog(this, "Xóa tài khoản " + u + "?", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                if(TaiKhoanDAO.deleteTaiKhoan(u)) refreshData();
            }
        });
        
        btnReset.addActionListener(e -> {
             String u = txtUser.getText();
             if(u.isEmpty()) return;
             if(TaiKhoanDAO.updatePassword(u, "123")) {
                 JOptionPane.showMessageDialog(this, "Đã reset mật khẩu về: 123");
                 refreshData();
             }
        });
    }

    public void refreshData() {
        tblAcc.setModel(TaiKhoanDAO.getDSTaiKhoan());
    }

    // Load danh sách NV vào ComboBox để Admin chọn dễ dàng
    private void loadCboNhanVien() {
        cboNhanVien.removeAllItems();
        DefaultTableModel model = NhanSuDAO.getNhanVienModel();
        for(int i=0; i<model.getRowCount(); i++) {
            String ma = model.getValueAt(i, 0).toString();
            String ten = model.getValueAt(i, 1).toString();
            cboNhanVien.addItem(ma + " - " + ten);
        }
    }

    private void setSelectedNhanVien(String maNV) {
        for(int i=0; i<cboNhanVien.getItemCount(); i++) {
            if(cboNhanVien.getItemAt(i).startsWith(maNV + " - ")) {
                cboNhanVien.setSelectedIndex(i);
                break;
            }
        }
    }
}