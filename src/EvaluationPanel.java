import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class EvaluationPanel extends JPanel {

    private JTable tblEval;
    private JTextField txtNam;

    public EvaluationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- TOP: CONTROL ---
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtNam = new JTextField(String.valueOf(LocalDate.now().getYear()), 5);
        JButton btnLoad = new JButton("🔄 Tổng Hợp Số Liệu");
        JButton btnSave = new JButton("💾 Chốt Xếp Loại Năm");
        
        btnLoad.setBackground(new Color(52, 152, 219)); btnLoad.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(230, 126, 34)); btnSave.setForeground(Color.WHITE);

        pnlTop.add(new JLabel("Năm học: "));
        pnlTop.add(txtNam);
        pnlTop.add(btnLoad);
        pnlTop.add(btnSave);
        
        // Chú thích tiêu chí
        JLabel lblNote = new JLabel("<html><i>(Tiêu chuẩn A: >270 tiết dạy & >1 điểm NCKH)</i></html>");
        lblNote.setForeground(Color.GRAY);
        pnlTop.add(Box.createHorizontalStrut(20));
        pnlTop.add(lblNote);

        add(pnlTop, BorderLayout.NORTH);

        // --- CENTER: TABLE ---
        tblEval = new JTable();
        tblEval.setRowHeight(30);
        tblEval.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(new JScrollPane(tblEval), BorderLayout.CENTER);

        // --- EVENT ---
        btnLoad.addActionListener(e -> refreshData()); // Gọi hàm refreshData
        
        btnSave.addActionListener(e -> {
            if(JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn lưu kết quả xếp loại này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                saveData();
            }
        });
        
        // Load lần đầu khi mở
        refreshData();
    }

    // --- ĐÂY LÀ HÀM QUAN TRỌNG ĐÃ ĐƯỢC SỬA ---
    public void refreshData() {
        try {
            int nam = Integer.parseInt(txtNam.getText());
            // Gọi DAO để lấy dữ liệu (Đảm bảo bạn đã có EvaluationDAO)
            tblEval.setModel(EvaluationDAO.calculateKPI(nam));
            
            // Tạo dropdown cho cột Xếp loại (Cột thứ 4)
            if (tblEval.getColumnCount() > 4) {
                tblEval.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(
                    new JComboBox<>(new String[]{"A - Xuất Sắc", "B - Hoàn Thành Tốt", "C - Hoàn Thành", "D - Không Hoàn Thành"})
                ));
            }
        } catch(Exception ex) {
            // ex.printStackTrace(); // Bật dòng này nếu muốn xem lỗi chi tiết trong console
        }
    }

    private void saveData() {
        try {
            int nam = Integer.parseInt(txtNam.getText());
            DefaultTableModel model = (DefaultTableModel) tblEval.getModel();
            int count = 0;
            
            for(int i=0; i<model.getRowCount(); i++) {
                String maNV = model.getValueAt(i, 0).toString();
                // Parse an toàn hơn để tránh lỗi dữ liệu
                int tongTiet = Integer.parseInt(model.getValueAt(i, 2).toString());
                double tongDiem = Double.parseDouble(model.getValueAt(i, 3).toString());
                String xepLoai = model.getValueAt(i, 4).toString();
                
                if(EvaluationDAO.saveEvaluation(maNV, nam, tongTiet, tongDiem, xepLoai)) {
                    count++;
                }
            }
            JOptionPane.showMessageDialog(this, "Đã lưu thành công " + count + " hồ sơ đánh giá!");
            refreshData(); // Refresh lại để cập nhật trạng thái "Đã chốt"
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu: " + e.getMessage());
        }
    }
}