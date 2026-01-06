/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author hoang
 */




import java.sql.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



public class qlGVFrame extends javax.swing.JFrame {
    
    DefaultTableModel tableModel;


    /**
     * Creates new form qlGVFrame
     */
    private static final String HINT_NGAYSINH = "yyyy-MM-dd (vd: 1980-08-22)";
    
   private void setupNgaySinhHint() {
    setHint(txtNgaySinh, HINT_NGAYSINH);
    }

    private void setHint(javax.swing.JTextField tf, String hint) {
        tf.setText(hint);
        tf.setForeground(java.awt.Color.GRAY);

        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (tf.getText().equals(hint)) {
                    tf.setText("");
                    tf.setForeground(java.awt.Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (tf.getText().trim().isEmpty()) {
                    tf.setText(hint);
                    tf.setForeground(java.awt.Color.GRAY);
                }
            }
        });
    }

        


    public qlGVFrame() {
        initComponents();
        
        setupNgaySinhHint();

        tableModel = (DefaultTableModel) tblQLGiaoVien.getModel();
        
        // load full DB
        refreshTableAll();
        
                // click row -> fill form
        tblQLGiaoVien.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) return;
            int viewRow = tblQLGiaoVien.getSelectedRow();
            if (viewRow >= 0) {
                int modelRow = tblQLGiaoVien.convertRowIndexToModel(viewRow);
                fillFormFromTableRow(modelRow);
            }
        });

       
    }
    
        // ====== TABLE ======
    private void refreshTableAll() {
        try {
            List<GiaoVien> list = GiaoVienModify.findAll();
            fillTable(list);
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void fillTable(List<GiaoVien> list) {
        tableModel.setRowCount(0);
        for (GiaoVien gv : list) {
            tableModel.addRow(new Object[]{
                gv.getMaGV(),
                gv.getHoTenGV(),
                gv.getKhoa(),
                gv.getQueQuanGV(),
                gv.getNgaySinhGV(),
                gv.getSdtGV(),
                gv.getEmailGV(),
                gv.getMaTK()
            });
        }
    }

    private void selectRowByMaGV(int maGV) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object v = tableModel.getValueAt(i, 0);
            if (v != null && String.valueOf(v).equals(String.valueOf(maGV))) {
                int viewRow = tblQLGiaoVien.convertRowIndexToView(i);
                tblQLGiaoVien.setRowSelectionInterval(viewRow, viewRow);
                tblQLGiaoVien.scrollRectToVisible(tblQLGiaoVien.getCellRect(viewRow, 0, true));
                return;
            }
        }
    }

    // ====== FORM ======
    private GiaoVien readForm(boolean includeMaGV) {
        String hoTen = txtHoTenGV.getText().trim();
        String khoa = txtKhoa.getText().trim();

        if (hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên giáo viên không được để trống.");
            txtHoTenGV.requestFocus();
            return null;
        }
        if (khoa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Khoa không được để trống.");
            txtKhoa.requestFocus();
            return null;
        }

        String queQuan = txtQueQuan.getText().trim();
        String ngaySinhStr = txtNgaySinh.getText().trim(); // yyyy-MM-dd hoặc rỗng
        if (ngaySinhStr.equals(HINT_NGAYSINH)) ngaySinhStr = "";
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String maTK = txtMaTK.getText().trim();

        Date ngaySinh = GiaoVienModify.parseDateOrNull(ngaySinhStr);
        if (!ngaySinhStr.isEmpty() && ngaySinh == null) {
            JOptionPane.showMessageDialog(this, "Ngày sinh phải đúng định dạng yyyy-MM-dd (ví dụ 2005-08-22).");
            txtNgaySinh.requestFocus();
            return null;
        }

        GiaoVien gv = new GiaoVien();
        if (includeMaGV) {
            String s = txtMaGV.getText().trim();
            if (s.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã GV không được để trống khi sửa/xóa.");
                txtMaGV.requestFocus();
                return null;
            }
            try {
                gv.setMaGV(Integer.parseInt(s));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mã GV phải là số.");
                txtMaGV.requestFocus();
                return null;
            }
        }

        gv.setHoTenGV(hoTen);
        gv.setKhoa(khoa);
        gv.setQueQuanGV(queQuan.isEmpty() ? null : queQuan);
        gv.setNgaySinhGV(ngaySinh); // giữ ngày sinh (có thể null)
        gv.setSdtGV(sdt.isEmpty() ? null : sdt);
        gv.setEmailGV(email.isEmpty() ? null : email);
        gv.setMaTK(maTK.isEmpty() ? null : maTK);

        return gv;
    }

    private void clearForm() {
        txtMaGV.setText("");
        txtHoTenGV.setText("");
        txtKhoa.setText("");
        txtQueQuan.setText("");
        txtNgaySinh.setText(HINT_NGAYSINH);
        txtNgaySinh.setForeground(java.awt.Color.GRAY);

        txtSDT.setText("");
        txtEmail.setText("");
        txtMaTK.setText("");
        tblQLGiaoVien.clearSelection();
    }

    private void fillFormFromTableRow(int row) {
        txtMaGV.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtHoTenGV.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        txtKhoa.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtQueQuan.setText(valueOrEmpty(tableModel.getValueAt(row, 3)));
        Object ns = tableModel.getValueAt(row, 4);
        if (ns == null || String.valueOf(ns).trim().isEmpty()) {
            txtNgaySinh.setText(HINT_NGAYSINH);
            txtNgaySinh.setForeground(java.awt.Color.GRAY);
        } else {
            txtNgaySinh.setText(String.valueOf(ns));
            txtNgaySinh.setForeground(java.awt.Color.BLACK);
        }

        txtSDT.setText(valueOrEmpty(tableModel.getValueAt(row, 5)));
        txtEmail.setText(valueOrEmpty(tableModel.getValueAt(row, 6)));
        txtMaTK.setText(valueOrEmpty(tableModel.getValueAt(row, 7)));
    }

    private String valueOrEmpty(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private String askKeyword() {
        return JOptionPane.showInputDialog(this, "Nhập từ khóa (maGV / yyyy-MM-dd / họ tên / khoa / ...):");
    }

    private void showError(RuntimeException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }


    


    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSlider1 = new javax.swing.JSlider();
        jPanel1 = new javax.swing.JPanel();
        lblQUANLYGIANGVIEN = new javax.swing.JLabel();
        lblMaGV = new javax.swing.JLabel();
        lblHoTenGV = new javax.swing.JLabel();
        lblKhoa = new javax.swing.JLabel();
        lblQueQuan = new javax.swing.JLabel();
        lblSDT = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblMaTK = new javax.swing.JLabel();
        txtMaGV = new javax.swing.JTextField();
        txtHoTenGV = new javax.swing.JTextField();
        txtKhoa = new javax.swing.JTextField();
        txtQueQuan = new javax.swing.JTextField();
        txtNgaySinh = new javax.swing.JTextField();
        txtSDT = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblQLGiaoVien = new javax.swing.JTable();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnTim = new javax.swing.JButton();
        lblNgaySinh = new javax.swing.JLabel();
        txtMaTK = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblQUANLYGIANGVIEN.setBackground(new java.awt.Color(204, 204, 204));
        lblQUANLYGIANGVIEN.setFont(new java.awt.Font("Segoe UI Light", 0, 48)); // NOI18N
        lblQUANLYGIANGVIEN.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQUANLYGIANGVIEN.setText("QUẢN LÝ GIÁO VIÊN");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblQUANLYGIANGVIEN, javax.swing.GroupLayout.PREFERRED_SIZE, 535, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 33, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblQUANLYGIANGVIEN, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                .addContainerGap())
        );

        lblMaGV.setText("Mã giáo viên");

        lblHoTenGV.setText("Họ tên giáo viên");

        lblKhoa.setText("Khoa");

        lblQueQuan.setText("Quê quán ");

        lblSDT.setText("Số điện thoại");

        lblEmail.setText("Email");

        lblMaTK.setText("Mã tài khoản");

        txtMaGV.addActionListener(this::txtMaGVActionPerformed);

        txtHoTenGV.addActionListener(this::txtHoTenGVActionPerformed);

        txtKhoa.addActionListener(this::txtKhoaActionPerformed);

        txtQueQuan.addActionListener(this::txtQueQuanActionPerformed);

        txtNgaySinh.addActionListener(this::txtNgaySinhActionPerformed);

        txtSDT.addActionListener(this::txtSDTActionPerformed);

        txtEmail.addActionListener(this::txtEmailActionPerformed);

        tblQLGiaoVien.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã GV", "Họ tên GV", "Khoa", "Quê quán", "Ngày sinh", "SĐT", "Email", "Mã TK"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblQLGiaoVien);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 648, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(243, Short.MAX_VALUE))
        );

        btnThem.setText("Thêm");
        btnThem.addActionListener(this::btnThemActionPerformed);

        btnSua.setText("Sửa");
        btnSua.addActionListener(this::btnSuaActionPerformed);

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(this::btnXoaActionPerformed);

        btnTim.setText("Tìm");
        btnTim.addActionListener(this::btnTimActionPerformed);

        lblNgaySinh.setText("Ngày sinh");

        txtMaTK.addActionListener(this::txtMaTKActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblMaGV)
                                    .addComponent(lblHoTenGV)
                                    .addComponent(lblKhoa)
                                    .addComponent(lblQueQuan)
                                    .addComponent(lblSDT)
                                    .addComponent(lblEmail)
                                    .addComponent(lblMaTK)
                                    .addComponent(lblNgaySinh))
                                .addGap(34, 34, 34))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnThem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(btnSua)
                                .addGap(78, 78, 78)
                                .addComponent(btnXoa)
                                .addGap(74, 74, 74)
                                .addComponent(btnTim))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtMaGV)
                                .addComponent(txtHoTenGV)
                                .addComponent(txtKhoa)
                                .addComponent(txtQueQuan)
                                .addComponent(txtNgaySinh)
                                .addComponent(txtSDT)
                                .addComponent(txtEmail)
                                .addComponent(txtMaTK, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)))
                        .addGap(32, 32, 32)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMaGV)
                            .addComponent(txtMaGV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblHoTenGV)
                            .addComponent(txtHoTenGV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblKhoa)
                            .addComponent(txtKhoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblQueQuan)
                            .addComponent(txtQueQuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNgaySinh))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSDT))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEmail))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMaTK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMaTK))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnSua))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnXoa)
                                .addComponent(btnTim))))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(645, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
  
    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
         try {
            GiaoVien gv = readForm(false);
            if (gv == null) return;

            int newId = GiaoVienModify.insert(gv);

            refreshTableAll();
            clearForm();

            if (newId > 0) selectRowByMaGV(newId);

            JOptionPane.showMessageDialog(this, "Đã thêm giáo viên.");
        } catch (RuntimeException ex) {
            showError(ex);
        }

    }//GEN-LAST:event_btnThemActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        try {
            GiaoVien gv = readForm(true);
            if (gv == null) return;

            boolean ok = GiaoVienModify.update(gv);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy mã GV để cập nhật.");
                return;
            }

            refreshTableAll();
            selectRowByMaGV(gv.getMaGV());
            JOptionPane.showMessageDialog(this, "Đã cập nhật.");
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        String s = txtMaGV.getText().trim();
        if (s.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng trong bảng (hoặc nhập Mã GV) để xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xóa giáo viên mã " + s + " ?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int ma = Integer.parseInt(s);
            boolean ok = GiaoVienModify.delete(ma);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy mã GV để xóa.");
                return;
            }

            refreshTableAll();
            clearForm();
            JOptionPane.showMessageDialog(this, "Đã xóa.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã GV phải là số.");
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }//GEN-LAST:event_btnXoaActionPerformed


    
    private void txtMaGVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaGVActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaGVActionPerformed

    private void txtHoTenGVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHoTenGVActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHoTenGVActionPerformed

    private void txtKhoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKhoaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKhoaActionPerformed

    private void txtQueQuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQueQuanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQueQuanActionPerformed

    private void txtNgaySinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNgaySinhActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNgaySinhActionPerformed

    private void txtSDTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSDTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSDTActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtMaTKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaTKActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaTKActionPerformed

    private void btnTimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimActionPerformed
         try {
            String keyword = askKeyword();
            if (keyword == null) return;

            keyword = keyword.trim();
            if (keyword.isEmpty()) {
                refreshTableAll();
                return;
            }

            List<GiaoVien> list = GiaoVienModify.find(keyword);
            fillTable(list);

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả.");
            }
        } catch (RuntimeException ex) {
            showError(ex);
        }

    }//GEN-LAST:event_btnTimActionPerformed

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
        } catch (Exception ex) {
            ex.printStackTrace();
           }

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new qlGVFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnTim;
    private javax.swing.JButton btnXoa;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblHoTenGV;
    private javax.swing.JLabel lblKhoa;
    private javax.swing.JLabel lblMaGV;
    private javax.swing.JLabel lblMaTK;
    private javax.swing.JLabel lblNgaySinh;
    private javax.swing.JLabel lblQUANLYGIANGVIEN;
    private javax.swing.JLabel lblQueQuan;
    private javax.swing.JLabel lblSDT;
    private javax.swing.JTable tblQLGiaoVien;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtHoTenGV;
    private javax.swing.JTextField txtKhoa;
    private javax.swing.JTextField txtMaGV;
    private javax.swing.JTextField txtMaTK;
    private javax.swing.JTextField txtNgaySinh;
    private javax.swing.JTextField txtQueQuan;
    private javax.swing.JTextField txtSDT;
    // End of variables declaration//GEN-END:variables

   
}
