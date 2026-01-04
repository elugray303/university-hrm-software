import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class SalaryPanel extends JPanel {

    // --- COMPONENTS ---
    private JTable tblNhanVien;
    private DefaultTableModel modelNV;
    
    // Form Inputs
    private JTextField txtMa, txtTen, txtLoaiHinh;
    private JTextField txtLuongCB, txtHeSo, txtPhuCap;
    private JTextField txtSoTiet, txtDonGia;
    private JLabel lblTongLuong;
    
    // Panels
    private JPanel pnlTeaching; 
    
    // Control Time
    private JComboBox<Integer> cboThang;
    private JTextField txtNam;

    // Biến lưu tạm
    private double curLuongCung = 0;
    private double curThuLao = 0;
    private double curThucLinh = 0;

    public SalaryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 1. TOOLBAR ---
        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlToolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        
        cboThang = new JComboBox<>();
        for(int i=1; i<=12; i++) cboThang.addItem(i);
        cboThang.setSelectedItem(LocalDate.now().getMonthValue());
        
        txtNam = new JTextField(String.valueOf(LocalDate.now().getYear()), 4);
        
        JButton btnReload = new JButton("🔄 Tải lại danh sách");
        JButton btnChotSo = new JButton("💾 Chốt Cả Tháng");
        JButton btnExcel = new JButton("📊 Xuất Excel");
        
        btnChotSo.setBackground(new Color(230, 126, 34)); btnChotSo.setForeground(Color.WHITE);
        btnExcel.setBackground(new Color(39, 174, 96)); btnExcel.setForeground(Color.WHITE);

        pnlToolbar.add(new JLabel("Kỳ lương: Tháng ")); pnlToolbar.add(cboThang);
        pnlToolbar.add(new JLabel(" Năm ")); pnlToolbar.add(txtNam);
        pnlToolbar.add(Box.createHorizontalStrut(20));
        pnlToolbar.add(btnReload);
        pnlToolbar.add(btnChotSo);
        pnlToolbar.add(btnExcel);

        add(pnlToolbar, BorderLayout.NORTH);

        // --- 2. SPLIT PANE ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5); 
        
        // A. NỬA TRÊN: DANH SÁCH ---
        JPanel pnlList = new JPanel(new BorderLayout());
        pnlList.setBorder(BorderFactory.createTitledBorder("1. DANH SÁCH NHÂN SỰ & THỰC LĨNH"));
        
        tblNhanVien = new JTable();
        tblNhanVien.setRowHeight(25);
        tblNhanVien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pnlList.add(new JScrollPane(tblNhanVien), BorderLayout.CENTER);
        
        // B. NỬA DƯỚI: FORM CHI TIẾT ---
        JPanel pnlDetail = new JPanel(new BorderLayout(10, 10));
        pnlDetail.setBorder(BorderFactory.createTitledBorder("2. TÍNH LƯƠNG & LƯU TRỮ"));
        
        // Form Layout
        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 15); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Init Fields
        txtMa = new JTextField(10); txtMa.setEditable(false); txtMa.setBackground(new Color(240,240,240));
        txtTen = new JTextField(15); txtTen.setEditable(false); txtTen.setBackground(new Color(240,240,240));
        txtLoaiHinh = new JTextField(15); txtLoaiHinh.setEditable(false); txtLoaiHinh.setBackground(new Color(240,240,240));
        
        txtLuongCB = new JTextField("0", 10);
        txtHeSo = new JTextField("1.0", 5);
        txtPhuCap = new JTextField("0", 10);
        txtSoTiet = new JTextField("0", 5);
        txtDonGia = new JTextField("50000", 10);

        // Layout Components
        addSectionTitle(pnlForm, "Thông Tin Nhân Viên", 0, gbc);
        addLabel(pnlForm, "Mã NV:", 1, 0, gbc); addField(pnlForm, txtMa, 1, 1, gbc);
        addLabel(pnlForm, "Họ Tên:", 1, 2, gbc); addField(pnlForm, txtTen, 1, 3, gbc);
        addLabel(pnlForm, "Loại Hình:", 2, 0, gbc); addField(pnlForm, txtLoaiHinh, 2, 1, gbc);

        addSectionTitle(pnlForm, "Lương Cố Định", 3, gbc);
        addLabel(pnlForm, "Lương CB:", 4, 0, gbc); addField(pnlForm, txtLuongCB, 4, 1, gbc);
        addLabel(pnlForm, "Hệ Số:", 4, 2, gbc); addField(pnlForm, txtHeSo, 4, 3, gbc);
        addLabel(pnlForm, "Thưởng/PC:", 5, 0, gbc); addField(pnlForm, txtPhuCap, 5, 1, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4; gbc.insets = new Insets(15, 5, 5, 5);
        pnlForm.add(new JLabel("<html><b>Thù Lao Giảng Dạy</b></html>"), gbc);
        
        pnlTeaching = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlTeaching.setOpaque(false);
        pnlTeaching.add(new JLabel("Số Tiết Dạy: ")); pnlTeaching.add(txtSoTiet);
        pnlTeaching.add(Box.createHorizontalStrut(20));
        pnlTeaching.add(new JLabel("Đơn Giá/Tiết: ")); pnlTeaching.add(txtDonGia);
        
        gbc.gridy = 7;
        pnlForm.add(pnlTeaching, gbc);
        
        // Footer Actions
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlAction.setBackground(new Color(240, 240, 240));
        pnlAction.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        
        JButton btnTinh = new JButton("🧮 TÍNH LƯƠNG");
        JButton btnLuuKQ = new JButton("💾 LƯU KẾT QUẢ"); 
        
        btnTinh.setFont(new Font("Arial", Font.BOLD, 14));
        btnTinh.setBackground(new Color(46, 204, 113)); btnTinh.setForeground(Color.WHITE);
        
        btnLuuKQ.setFont(new Font("Arial", Font.BOLD, 14));
        btnLuuKQ.setBackground(new Color(52, 152, 219)); btnLuuKQ.setForeground(Color.WHITE);
        btnLuuKQ.setEnabled(false);
        
        lblTongLuong = new JLabel("Tổng Thực Lĩnh: 0 VNĐ");
        lblTongLuong.setFont(new Font("Arial", Font.BOLD, 20));
        lblTongLuong.setForeground(Color.RED);
        
        pnlAction.add(btnTinh);
        pnlAction.add(btnLuuKQ);
        pnlAction.add(lblTongLuong);

        pnlDetail.add(pnlForm, BorderLayout.CENTER);
        pnlDetail.add(pnlAction, BorderLayout.SOUTH);

        splitPane.setTopComponent(pnlList);
        splitPane.setBottomComponent(pnlDetail);
        add(splitPane, BorderLayout.CENTER);

        // --- LOGIC & EVENTS ---
        
        loadTable();

        ActionListener timeChangeListener = e -> loadTable();
        cboThang.addActionListener(timeChangeListener);
        txtNam.addActionListener(timeChangeListener);

        tblNhanVien.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblNhanVien.getSelectedRow();
                if(row != -1) {
                    loadEmployeeData(row);
                    btnLuuKQ.setEnabled(false);
                }
            }
        });
        
        btnTinh.addActionListener(e -> {
            calculateSalary();
            btnLuuKQ.setEnabled(true);
        });
        
        // --- CẬP NHẬT: Xử lý nút LƯU KẾT QUẢ ---
        btnLuuKQ.addActionListener(e -> {
            String maNV = txtMa.getText();
            if(maNV.isEmpty()) return;

            int thang = (int) cboThang.getSelectedItem();
            int nam = getNam();
            
            // Biến lưu giá trị từ ô nhập
            int tongTiet = 0;
            double valLuongCB = 0;
            double valHeSo = 1;
            double valPhuCap = 0;
            
            try { 
                // Lấy dữ liệu từ ô nhập (đã xử lý dấu phẩy)
                tongTiet = Integer.parseInt(txtSoTiet.getText()); 
                valLuongCB = Double.parseDouble(txtLuongCB.getText().replace(",", "").replace(".", ""));
                valHeSo = Double.parseDouble(txtHeSo.getText().replace(",", "").replace(".", ""));
                valPhuCap = Double.parseDouble(txtPhuCap.getText().replace(",", "").replace(".", ""));
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng kiểm tra lại định dạng số!");
                return;
            }
            
            // Gọi hàm DAO mới với đầy đủ tham số
            boolean result = LuongDAO.saveSingleSalary(
                maNV, thang, nam, 
                valLuongCB, valHeSo, valPhuCap, // Truyền thêm 3 tham số này
                curLuongCung, tongTiet, curThuLao, curThucLinh
            );

            if(result) {
                JOptionPane.showMessageDialog(this, "Đã lưu thành công chi tiết lương!");
                loadTable(); 
                selectRowByMaNV(maNV);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu!");
            }
        });

        btnReload.addActionListener(e -> loadTable());
        
        btnExcel.addActionListener(e -> {
            if (tblNhanVien.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Danh sách trống, không có dữ liệu để xuất!");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn vị trí lưu bảng lương");
            
            // Gợi ý tên file: Bang_Luong_Thang_12_2025.xlsx
            int thang = (int) cboThang.getSelectedItem();
            int nam = getNam();
            String defaultFileName = "Bang_Luong_Thang_" + thang + "_" + nam + ".xlsx";
            fileChooser.setSelectedFile(new File(defaultFileName));

            // Chỉ cho chọn file Excel
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx");
            fileChooser.setFileFilter(filter);

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                // Gọi hàm từ class ExcelExporter
                ExcelExporter.exportToExcel(tblNhanVien, fileToSave, "Luong_Thang_" + thang);
            }
        });
    }

    // --- HELPERS ---

    private int getNam() {
        try {
            return Integer.parseInt(txtNam.getText());
        } catch (NumberFormatException e) {
            return LocalDate.now().getYear();
        }
    }

    private void loadTable() {
        int thang = (int) cboThang.getSelectedItem();
        int nam = getNam();
        
        modelNV = LuongDAO.getBangLuong(thang, nam);
        tblNhanVien.setModel(modelNV);
        
        if(tblNhanVien.getColumnCount() > 0) {
            tblNhanVien.getColumnModel().getColumn(1).setPreferredWidth(150);
            tblNhanVien.getColumnModel().getColumn(6).setPreferredWidth(100);
        }
    }

    private void loadEmployeeData(int row) {
        try {
            String maNV = getValue(row, 0);
            int thang = (int) cboThang.getSelectedItem();
            int nam = getNam();

            Object[] data = LuongDAO.getChiTietLuong(maNV, thang, nam);
            
            if(data != null) {
                txtMa.setText(data[0].toString());
                txtTen.setText(data[1].toString());
                txtLoaiHinh.setText(data[2].toString());
                txtHeSo.setText(String.valueOf(data[3]));
                
                DecimalFormat df = new DecimalFormat("###");
                txtLuongCB.setText(df.format(data[4]));
                txtPhuCap.setText(df.format(data[5]));
                
                int soTiet = (int) data[6];
                txtSoTiet.setText(String.valueOf(soTiet));
                
                double savedThucLinh = (double) data[7];
                if(savedThucLinh > 0) {
                     lblTongLuong.setText("Đã lưu: " + new DecimalFormat("#,###").format(savedThucLinh) + " VNĐ");
                } else {
                     lblTongLuong.setText("Chưa tính lương");
                }
            }

            String loaiHinh = txtLoaiHinh.getText().toLowerCase();
            boolean isCoHuu = loaiHinh.contains("cơ hữu") || loaiHinh.contains("biên chế");
            setTeachingVisible(!isCoHuu);

        } catch (Exception e) { e.printStackTrace(); }
    }
    
    private void selectRowByMaNV(String maNV) {
        for(int i=0; i<tblNhanVien.getRowCount(); i++) {
            if(tblNhanVien.getValueAt(i, 0).equals(maNV)) {
                tblNhanVien.setRowSelectionInterval(i, i);
                break;
            }
        }
    }
    
    private void setTeachingVisible(boolean visible) {
        txtSoTiet.setEnabled(visible);
        txtDonGia.setEnabled(visible);
        if (!visible) {
            txtSoTiet.setText("0"); 
            txtSoTiet.setBackground(new Color(240,240,240));
            txtDonGia.setBackground(new Color(240,240,240));
        } else {
            txtSoTiet.setBackground(Color.WHITE);
            txtDonGia.setBackground(Color.WHITE);
        }
    }

    private void calculateSalary() {
        try {
            double heSo = Double.parseDouble(txtHeSo.getText());
            double luongCB = Double.parseDouble(txtLuongCB.getText().replace(",", "").replace(".", ""));
            double phuCap = Double.parseDouble(txtPhuCap.getText().replace(",", "").replace(".", ""));
            
            curLuongCung = (heSo * luongCB) + phuCap;
            curThuLao = 0;

            if (txtSoTiet.isEnabled()) {
                int soTiet = Integer.parseInt(txtSoTiet.getText());
                double donGia = Double.parseDouble(txtDonGia.getText().replace(",", "").replace(".", ""));
                curThuLao = (soTiet * donGia);
            }
            
            curThucLinh = curLuongCung + curThuLao;

            DecimalFormat df = new DecimalFormat("#,###");
            lblTongLuong.setText("Tổng Thực Lĩnh: " + df.format(curThucLinh) + " VNĐ");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số (không chứa chữ cái)!");
        }
    }

    private String getValue(int row, int col) {
        Object val = tblNhanVien.getValueAt(row, col);
        return val == null ? "" : val.toString();
    }
    
    private void addLabel(JPanel p, String text, int row, int col, GridBagConstraints gbc) {
        gbc.gridx = col; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        p.add(new JLabel(text), gbc);
    }
    private void addField(JPanel p, JTextField field, int row, int col, GridBagConstraints gbc) {
        gbc.gridx = col; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        p.add(field, gbc);
    }
    private void addSectionTitle(JPanel p, String text, int row, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        JLabel lbl = new JLabel("<html><b>" + text + "</b></html>");
        lbl.setForeground(new Color(52, 152, 219));
        lbl.setBorder(new EmptyBorder(10, 0, 5, 0));
        p.add(lbl, gbc);
    }
}