package poly.quanlythuvien.ui;

import poly.quanlythuvien.dao.ThongKeDAO;
import poly.quanlythuvien.daoimpl.ThongKeDAOlmpl;
import poly.quanlythuvien.entity.ThongKe;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ThongKeBaoCao extends javax.swing.JDialog {
    private ThongKeDAO thongKeDAO;

    public ThongKeBaoCao(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        thongKeDAO = new ThongKeDAOlmpl();
        loadAllData();
    }

    public void loadAllData() {
        try {
            // Fill tblSachmuonnhieunhat
            List<ThongKe> listSachMuon = thongKeDAO.getMostBorrowedBooks();
            fillToTable(tblSachmuonnhieunhat, listSachMuon, "Sách mượn nhiều nhất");

            // Fill tblDocgiatratre
            List<ThongKe> listDocGiaTraTre = thongKeDAO.getLateReturns();
            System.out.println("Số bản ghi trả trễ: " + listDocGiaTraTre.size());
            fillToTable(tblDocgiatratre, listDocGiaTraTre, "Độc giả trả sách trễ");

            // Fill tblTinhhinhkhosach
            List<ThongKe> listKhoSach = thongKeDAO.getBookInventory();
            fillToTable(tblTinhhinhkhosach, listKhoSach, "Tình hình kho sách");

            // Fill tblhoatdongmuontra
            List<ThongKe> listHoatDong = thongKeDAO.getUserActivity();
            fillToTable(tblhoatdongmuontra, listHoatDong, "Hoạt động mượn/trả");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void fillToTable(JTable table, List<ThongKe> list, String tableName) {
    try {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ
        if (list == null || list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu cho bảng " + tableName, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (ThongKe tk : list) {
            if (table == tblDocgiatratre) {
                // Xử lý ngày tháng như String, định dạng nếu có giá trị
                String ngayTraDuKienStr = tk.getNgayTraDuKien() != null ? tk.getNgayTraDuKien() : "";
                String ngayTraThucTeStr = tk.getNgayTraThucTe() != null ? tk.getNgayTraThucTe() : "";
                try {
                    if (!ngayTraDuKienStr.isEmpty()) {
                        ngayTraDuKienStr = sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(ngayTraDuKienStr));
                    }
                    if (!ngayTraThucTeStr.isEmpty()) {
                        ngayTraThucTeStr = sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(ngayTraThucTeStr));
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi định dạng ngày cho bản ghi: " + tk.getMaDocGia() + ", sách: " + tk.getTenSach());
                    // Giữ nguyên giá trị String nếu định dạng thất bại
                }
                model.addRow(new Object[]{
                    tk.getStt(),
                    tk.getMaDocGia(),
                    tk.getHoTen(),
                    tk.getTenSach(),
                    ngayTraDuKienStr,
                    ngayTraThucTeStr,
                    tk.getSoNgayTre()
                });
            } else if (table == tblSachmuonnhieunhat) {
                model.addRow(new Object[]{
                    tk.getTenSach(),
                    tk.getMaSach(),
                    tk.getTacGia(),
                    tk.getSoLuotMuon()
                });
            } else if (table == tblTinhhinhkhosach) {
                model.addRow(new Object[]{
                    tk.getStt(),
                    tk.getMaSach(),
                    tk.getTenSach(),
                    tk.getTheLoai(),
                    tk.getTongSo(),
                    tk.getDangMuon(),
                    tk.getConLai()
                });
            } else if (table == tblhoatdongmuontra) {
                model.addRow(new Object[]{
                    tk.getStt(),
                    tk.getMaDocGia(),
                    tk.getHoTen(),
                    tk.getTongSoLuotMuon(),
                    tk.getTongSoSachTraTre()
                });
            }
        }
        table.revalidate();
        table.repaint(); // Làm mới giao diện bảng
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi điền dữ liệu vào bảng " + tableName + ": " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

    private void deleteLateReturn() {
        int selectedRow = tblDocgiatratre.getSelectedRow();
        if (selectedRow >= 0) {
            String maDocGia = (String) tblDocgiatratre.getValueAt(selectedRow, 1);
            String tenSach = (String) tblDocgiatratre.getValueAt(selectedRow, 3);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xóa bản ghi trả trễ cho độc giả " + maDocGia + " với sách " + tenSach + "?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    thongKeDAO.deleteLateReturn(maDocGia, tenSach);
                    loadAllData(); // Cập nhật lại toàn bộ bảng
                    JOptionPane.showMessageDialog(this, "Xóa bản ghi trả trễ thành công!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bản ghi để xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
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

        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSachmuonnhieunhat = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDocgiatratre = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTinhhinhkhosach = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblhoatdongmuontra = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Thống Kê & Báo Cáo");

        tblSachmuonnhieunhat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Tên sách", "Mã sách", "Tác giả", "Số lượt mượn"
            }
        ));
        jScrollPane1.setViewportView(tblSachmuonnhieunhat);

        jTabbedPane1.addTab("Sách mượn nhiều nhất", jScrollPane1);

        tblDocgiatratre.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã độc giả	", "Họ tên", "Tên sách", "Ngày trả dự kiến", "Ngày trả thực tế", "Số ngày trễ"
            }
        ));
        jScrollPane2.setViewportView(tblDocgiatratre);

        jTabbedPane1.addTab("Độc giả trả sách trễ", jScrollPane2);

        tblTinhhinhkhosach.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã sách", "Tên sách", "Thể loại", "Tổng số", "Đang mượn", "Còn lại"
            }
        ));
        jScrollPane3.setViewportView(tblTinhhinhkhosach);

        jTabbedPane1.addTab("Tình hình kho sách", jScrollPane3);

        tblhoatdongmuontra.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã độc giả", "Họ tên", "Tổng số lượt mượn", "Tổng số sách trả trễ"
            }
        ));
        jScrollPane4.setViewportView(tblhoatdongmuontra);

        jTabbedPane1.addTab("Hoạt động mượn/trả theo người dùng", jScrollPane4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 866, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(ThongKeBaoCao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ThongKeBaoCao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ThongKeBaoCao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ThongKeBaoCao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ThongKeBaoCao dialog = new ThongKeBaoCao(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tblDocgiatratre;
    private javax.swing.JTable tblSachmuonnhieunhat;
    private javax.swing.JTable tblTinhhinhkhosach;
    private javax.swing.JTable tblhoatdongmuontra;
    // End of variables declaration//GEN-END:variables
}
