package poly.quanlythuvien.ui;

import poly.quanlythuvien.controller.QuanLyNVController;
import poly.quanlythuvien.dao.QuanLyNVDAO;
import poly.quanlythuvien.daoimpl.QuanLyNVDAOImpl;
import poly.quanlythuvien.entity.QuanLyNV;
import javax.swing.table.DefaultTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;

public class UC8 extends javax.swing.JDialog implements QuanLyNVController {
    private QuanLyNVDAO dao;

    public UC8(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        dao = new QuanLyNVDAOImpl();
        // Group radio buttons for Gender
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rdonam);
        genderGroup.add(rdonu);
        // Group radio buttons for Role
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(rdothuThu);
        roleGroup.add(rdoManager);
        roleGroup.add(rdoviewer);
        // Group radio buttons for Status
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(rdoActive);
        statusGroup.add(rdoPause);
        statusGroup.add(rdolock);
        loadTableData();
        // Add table row selection listener
        tblClient.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                int selectedRow = tblClient.getSelectedRow();
                if (selectedRow >= 0) {
                    String maNV = (String) tblClient.getValueAt(selectedRow, 0);
                    QuanLyNV nv = dao.findById(maNV);
                    if (nv != null) {
                        fillForm(nv);
                    }
                }
            }
        });
    }

    @Override
    public void loadTableData() {
        DefaultTableModel model = (DefaultTableModel) tblClient.getModel();
        model.setRowCount(0); // Clear old data
        List<QuanLyNV> list = dao.findAll();
        for (QuanLyNV nv : list) {
            model.addRow(new Object[]{
                nv.getMaNhanVien(),
                nv.getHoTen(),
                nv.isGioiTinh() ? "Nam" : "Nữ",
                new SimpleDateFormat("dd/MM/yyyy").format(nv.getNgaySinh()),
                nv.getSoDienThoai(),
                nv.getEmail(),
                nv.getChucVu(),
                nv.getTrangThai()
            });
        }
    }

    @Override
    public void createEmployee() {
        QuanLyNV nv = getFormData();
        if (nv != null) {
            try {
                // Kiểm tra mã nhân viên đã tồn tại
                if (dao.findById(nv.getMaNhanVien()) != null) {
                    JOptionPane.showMessageDialog(this, "Mã nhân viên đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dao.create(nv);
                loadTableData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Tạo mới nhân viên thành công!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ và đúng định dạng dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void updateEmployee() {
        QuanLyNV nv = getFormData();
        if (nv != null) {
            try {
                // Kiểm tra xem nhân viên có tồn tại không
                if (dao.findById(nv.getMaNhanVien()) == null) {
                    JOptionPane.showMessageDialog(this, "Nhân viên không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dao.update(nv);
                loadTableData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ và đúng định dạng dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void deleteEmployee() {
        String maNV = txtmaNhanvien.getText().trim();
        if (!maNV.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa nhân viên này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Kiểm tra xem nhân viên có tồn tại không
                    if (dao.findById(maNV) == null) {
                        JOptionPane.showMessageDialog(this, "Nhân viên không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    dao.deleteById(maNV);
                    loadTableData();
                    clearForm();
                    JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void clearForm() {
        txtmaNhanvien.setText("");
        txtName.setText("");
        txtDate.setText("");
        txtNumber.setText("");
        txtemail.setText("");
        rdonam.setSelected(true);
        rdothuThu.setSelected(true);
        rdoActive.setSelected(true);
    }

    @Override
    public void fillForm(QuanLyNV employee) {
        txtmaNhanvien.setText(employee.getMaNhanVien());
        txtName.setText(employee.getHoTen());
        txtDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(employee.getNgaySinh()));
        txtNumber.setText(employee.getSoDienThoai());
        txtemail.setText(employee.getEmail());
        rdonam.setSelected(employee.isGioiTinh());
        rdonu.setSelected(!employee.isGioiTinh());
        switch (employee.getChucVu()) {
            case "Quản lý":
                rdoManager.setSelected(true);
                break;
            case "Thủ thư":
                rdothuThu.setSelected(true);
                break;
            case "Người xem":
                rdoviewer.setSelected(true);
                break;
        }
        switch (employee.getTrangThai()) {
            case "Hoạt động":
                rdoActive.setSelected(true);
                break;
            case "Ngưng":
                rdoPause.setSelected(true);
                break;
            case "Khoá tài khoản":
                rdolock.setSelected(true);
                break;
        }
    }

    @Override
    public List<QuanLyNV> getAllEmployees() {
        return dao.findAll();
    }

    private QuanLyNV getFormData() {
        try {
            QuanLyNV nv = new QuanLyNV();
            String maNV = txtmaNhanvien.getText().trim();
            String hoTen = txtName.getText().trim();
            String ngaySinhStr = txtDate.getText().trim();
            String soDienThoai = txtNumber.getText().trim();
            String email = txtemail.getText().trim();

            // Kiểm tra dữ liệu đầu vào
            if (maNV.isEmpty() || hoTen.isEmpty() || ngaySinhStr.isEmpty() || soDienThoai.isEmpty() || email.isEmpty()) {
                return null;
            }
            // Kiểm tra định dạng email
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                return null;
            }
            // Kiểm tra định dạng số điện thoại (chỉ chứa số, 10-11 chữ số)
            if (!soDienThoai.matches("\\d{10,11}")) {
                return null;
            }

            nv.setMaNhanVien(maNV);
            nv.setHoTen(hoTen);
            nv.setGioiTinh(rdonam.isSelected());
            nv.setNgaySinh(new SimpleDateFormat("dd/MM/yyyy").parse(ngaySinhStr));
            nv.setSoDienThoai(soDienThoai);
            nv.setEmail(email);
            nv.setChucVu(rdoManager.isSelected() ? "Quản lý" : 
                        rdothuThu.isSelected() ? "Thủ thư" : "Người xem");
            nv.setTrangThai(rdoActive.isSelected() ? "Hoạt động" : 
                           rdoPause.isSelected() ? "Ngưng" : "Khoá tài khoản");
            return nv;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblClient = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtmaNhanvien = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtemail = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        rdonam = new javax.swing.JRadioButton();
        rdonu = new javax.swing.JRadioButton();
        rdoPause = new javax.swing.JRadioButton();
        rdoActive = new javax.swing.JRadioButton();
        jLabel13 = new javax.swing.JLabel();
        rdothuThu = new javax.swing.JRadioButton();
        rdoManager = new javax.swing.JRadioButton();
        jLabel14 = new javax.swing.JLabel();
        rdoviewer = new javax.swing.JRadioButton();
        rdolock = new javax.swing.JRadioButton();
        txtNumber = new javax.swing.JTextField();
        lbPhoto = new javax.swing.JLabel();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tblClient.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã nhân viên	", "Họ tên	", "Giới tính	", "Ngày sinh	", "Số điện thoại	", "Email", "Chức vụ", "Trạng thái	"
            }
        ));
        jScrollPane2.setViewportView(tblClient);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 853, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("DANH SÁCH", jPanel1);

        jLabel4.setText("Mã nhân viên:");

        jLabel5.setText("Giới tính:");

        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });

        jLabel6.setText("Họ tên:");

        txtmaNhanvien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtmaNhanvienActionPerformed(evt);
            }
        });

        jLabel7.setText("Ngày sinh:");

        jLabel8.setText("Email:");

        txtemail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtemailActionPerformed(evt);
            }
        });

        jLabel9.setText("Số điện thoại:");

        txtDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDateActionPerformed(evt);
            }
        });

        rdonam.setText("Nam");

        rdonu.setText("Nữ");

        rdoPause.setText("Ngưng");

        rdoActive.setText("Hoạt động");

        jLabel13.setText("Trạng thái:");

        rdothuThu.setText("Thủ thư");

        rdoManager.setText("Quản lý");
        rdoManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoManagerActionPerformed(evt);
            }
        });

        jLabel14.setText("Chức vụ:");

        rdoviewer.setText("Người xem");

        rdolock.setText("Khoá tài khoản");

        txtNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumberActionPerformed(evt);
            }
        });

        lbPhoto.setToolTipText("");
        lbPhoto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnCreate.setText("Tạo mới");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnUpdate.setText("Cập nhật");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setText("Xóa");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnClear.setText("Nhập mới");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(lbPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(95, 95, 95)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtmaNhanvien, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(77, 77, 77)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(77, 77, 77)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtemail, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(77, 77, 77)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(rdonam, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rdonu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdoManager))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoActive)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(rdothuThu)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rdoviewer))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(rdoPause)
                                .addGap(18, 18, 18)
                                .addComponent(rdolock)))))
                .addContainerGap(22, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(btnUpdate)
                        .addGap(53, 53, 53)
                        .addComponent(btnDelete)
                        .addGap(50, 50, 50)
                        .addComponent(btnClear))
                    .addComponent(btnCreate))
                .addGap(109, 109, 109))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtmaNhanvien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(rdonam)
                                .addComponent(rdonu)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdothuThu)
                            .addComponent(rdoManager)
                            .addComponent(rdoviewer))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdoActive)
                            .addComponent(rdoPause)
                            .addComponent(rdolock)))
                    .addComponent(lbPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate)
                    .addComponent(btnUpdate)
                    .addComponent(btnDelete)
                    .addComponent(btnClear))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("BIỂU MẪU", jPanel2);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Quản Lý Nhân Viên");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameActionPerformed

    private void txtmaNhanvienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtmaNhanvienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtmaNhanvienActionPerformed

    private void txtemailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtemailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtemailActionPerformed

    private void txtDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateActionPerformed

    private void rdoManagerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoManagerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdoManagerActionPerformed

    private void txtNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNumberActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
        createEmployee();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        updateEmployee();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteEmployee();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clearForm();
    }//GEN-LAST:event_btnClearActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UC8.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UC8.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UC8.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UC8.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UC8 dialog = new UC8(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lbPhoto;
    private javax.swing.JRadioButton rdoActive;
    private javax.swing.JRadioButton rdoManager;
    private javax.swing.JRadioButton rdoPause;
    private javax.swing.JRadioButton rdolock;
    private javax.swing.JRadioButton rdonam;
    private javax.swing.JRadioButton rdonu;
    private javax.swing.JRadioButton rdothuThu;
    private javax.swing.JRadioButton rdoviewer;
    private javax.swing.JTable tblClient;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNumber;
    private javax.swing.JTextField txtemail;
    private javax.swing.JTextField txtmaNhanvien;
    // End of variables declaration//GEN-END:variables
}
