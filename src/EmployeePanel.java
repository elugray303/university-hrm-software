import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EmployeePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public EmployeePanel(JFrame parentFrame) {
        setLayout(new BorderLayout());
        
        // Toolbar
        JPanel pnlTools = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("➕ Thêm");
        JButton btnEdit = new JButton("✏️ Sửa");
        JButton btnDel = new JButton("❌ Xóa");
        
        // Màu sắc nút
        btnAdd.setBackground(new Color(46, 204, 113)); btnAdd.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(52, 152, 219)); btnEdit.setForeground(Color.WHITE);
        btnDel.setBackground(new Color(231, 76, 60)); btnDel.setForeground(Color.WHITE);
        
        pnlTools.add(btnAdd); pnlTools.add(btnEdit); pnlTools.add(btnDel);
        add(pnlTools, BorderLayout.NORTH);

        // Bảng
        table = new JTable();
        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Sự kiện
        btnAdd.addActionListener(e -> {
            EmployeeDialog d = new EmployeeDialog(parentFrame, null);
            d.setVisible(true);
            if(d.isSaved()) loadData();
        });

        btnEdit.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để sửa!");
                return;
            }
            // Lấy toàn bộ dữ liệu dòng chọn truyền sang Dialog
            int cols = table.getColumnCount();
            String[] data = new String[cols];
            for(int i=0; i<cols; i++) data[i] = table.getValueAt(r, i) == null ? "" : table.getValueAt(r, i).toString();
            
            EmployeeDialog d = new EmployeeDialog(parentFrame, data);
            d.setVisible(true);
            if(d.isSaved()) loadData();
        });

        btnDel.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r != -1 && JOptionPane.showConfirmDialog(this, "Xóa nhân viên này?") == JOptionPane.YES_OPTION) {
                if(NhanSuDAO.deleteNhanVien(table.getValueAt(r, 0).toString())) loadData();
            } else if (r == -1) {
                JOptionPane.showMessageDialog(this, "Chọn nhân viên cần xóa!");
            }
        });

        loadData();
    }

    private void loadData() {
        table.setModel(NhanSuDAO.getNhanVienModel());
        
        // Các cột: 0:Ma, 1:Ten, 2:NgaySinh, 3:Khoa, 4:CV, 5:TD, 6:LH, 7:TT, 8:HeSo, 9:Luong, 10:PC, 11:HinhAnh
        int lastCol = table.getColumnCount() - 1;
        table.getColumnModel().getColumn(lastCol).setMinWidth(0);
        table.getColumnModel().getColumn(lastCol).setMaxWidth(0);
        
        // Nếu muốn ẩn thêm cột Lương trên bảng chính để đỡ rối uncomment các dòng dưới:
        /*
        table.getColumnModel().getColumn(8).setMinWidth(0); table.getColumnModel().getColumn(8).setMaxWidth(0); // Hệ số
        table.getColumnModel().getColumn(9).setMinWidth(0); table.getColumnModel().getColumn(9).setMaxWidth(0); // Lương CB
        table.getColumnModel().getColumn(10).setMinWidth(0); table.getColumnModel().getColumn(10).setMaxWidth(0); // Phụ cấp
        */
    }
}