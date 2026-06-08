package penggajian;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.toedter.calendar.JDateChooser;

public class FormPenggajian extends JFrame {
    private JTextField txtIdGaji, txtNamaKaryawan, txtGolongan;
    private JTextField txtJumlahGaji, txtJumlahLembur, txtPotongan, txtTotalGaji;
    private JComboBox<String> cbIdKaryawan;
    private JTable table;
    private DefaultTableModel tableModel;
    private JDateChooser dateGaji;
    private boolean isUpdating = false;

    public FormPenggajian() {
        setTitle("Daftar Gaji Karyawan");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(30, 35, 45));
        
        setLayout(new BorderLayout(10, 10));
        
        JPanel pnlForm = new JPanel(new GridLayout(9, 2, 5, 5));
        pnlForm.setOpaque(false);
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        pnlForm.add(createLabel("ID Gaji:"));
        txtIdGaji = new JTextField(); pnlForm.add(txtIdGaji);
        
        pnlForm.add(createLabel("Tanggal Gaji:"));
        dateGaji = new JDateChooser();
        dateGaji.setDateFormatString("dd-MM-yyyy");
        pnlForm.add(dateGaji);
        
        pnlForm.add(createLabel("ID Karyawan:"));
        cbIdKaryawan = new JComboBox<>(new String[]{"K001", "K002"}); pnlForm.add(cbIdKaryawan);
        
        pnlForm.add(createLabel("Nama Karyawan:"));
        txtNamaKaryawan = new JTextField(); txtNamaKaryawan.setEditable(false); pnlForm.add(txtNamaKaryawan);
        
        pnlForm.add(createLabel("Golongan:"));
        txtGolongan = new JTextField(); txtGolongan.setEditable(false); pnlForm.add(txtGolongan);
        
        pnlForm.add(createLabel("Jumlah Gaji:"));
        txtJumlahGaji = new JTextField(); txtJumlahGaji.setEditable(false); pnlForm.add(txtJumlahGaji);
        
        pnlForm.add(createLabel("Jumlah Lembur (Rp):"));
        txtJumlahLembur = new JTextField(); txtJumlahLembur.setEditable(false); pnlForm.add(txtJumlahLembur);
        
        pnlForm.add(createLabel("Potongan:"));
        txtPotongan = new JTextField("0"); pnlForm.add(txtPotongan);
        
        pnlForm.add(createLabel("Total Gaji Bersih:"));
        txtTotalGaji = new JTextField(); txtTotalGaji.setEditable(false); pnlForm.add(txtTotalGaji);
        
        JPanel pnlButtons = new JPanel();
        pnlButtons.setOpaque(false);
        JButton btnHitung = new JButton("Hitung Total");
        JButton btnSave = new JButton("Save");
        JButton btnReset = new JButton("Reset");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnExit = new JButton("Exit");
        
        pnlButtons.add(btnHitung);
        pnlButtons.add(btnSave); pnlButtons.add(btnReset);
        pnlButtons.add(btnUpdate); pnlButtons.add(btnDelete); pnlButtons.add(btnExit);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(pnlForm, BorderLayout.CENTER);
        pnlTop.add(pnlButtons, BorderLayout.SOUTH);
        
        add(pnlTop, BorderLayout.NORTH);
        
        String[] cols = {"ID Gaji", "Tgl", "ID Kary", "Nama", "Golongan", "Gaji", "Lembur", "Potongan", "Total"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        btnExit.addActionListener(e -> dispose());
        btnHitung.addActionListener(e -> hitungGaji());
        btnSave.addActionListener(e -> simpanData());
        btnUpdate.addActionListener(e -> updateData());
        btnDelete.addActionListener(e -> deleteData());
        btnReset.addActionListener(e -> resetForm());
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelKlik();
            }
        });
        cbIdKaryawan.addActionListener(e -> {
            if (!isUpdating) {
                updateKaryawanDetails();
            }
        });

        loadKaryawan();
        loadData();
    }
    
    private void updateKaryawanDetails() {
        if (cbIdKaryawan.getSelectedItem() == null) return;
        String idKaryawan = cbIdKaryawan.getSelectedItem().toString();
        try {
            // 1. Fetch employee details and Golongan details
            String sql = "SELECT k.nama, k.status, g.nama_golongan, g.gaji_pokok, g.tunjangan_istri, g.tunjangan_anak, g.transport, g.uang_makan " +
                         "FROM tb_karyawan k JOIN tb_golongan g ON k.id_golongan = g.id_golongan WHERE k.id_karyawan = ?";
            java.sql.ResultSet rs = DatabaseHelper.executeQuery(sql, idKaryawan);
            if (rs != null && rs.next()) {
                txtNamaKaryawan.setText(rs.getString("nama"));
                txtGolongan.setText(rs.getString("nama_golongan"));
                
                double gapok = rs.getDouble("gaji_pokok");
                double tunjIstri = rs.getString("status").equalsIgnoreCase("Menikah") ? rs.getDouble("tunjangan_istri") : 0;
                double tunjAnak = rs.getDouble("tunjangan_anak");
                double transport = rs.getDouble("transport");
                double makan = rs.getDouble("uang_makan");
                
                double jumlahGaji = gapok + tunjIstri + tunjAnak + transport + makan;
                txtJumlahGaji.setText(String.valueOf(jumlahGaji));
            }
            
            // 2. Fetch total lembur hours and calculate money (assume Rp 30,000 per hour)
            String sqlLembur = "SELECT SUM(jumlah_jam) as total_jam FROM tb_lembur WHERE id_karyawan = ?";
            java.sql.ResultSet rsLembur = DatabaseHelper.executeQuery(sqlLembur, idKaryawan);
            if (rsLembur != null && rsLembur.next()) {
                int totalJam = rsLembur.getInt("total_jam");
                double lemburMoney = totalJam * 30000.0;
                txtJumlahLembur.setText(String.valueOf(lemburMoney));
            } else {
                txtJumlahLembur.setText("0.0");
            }
            
            hitungGaji();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadKaryawan() {
        isUpdating = true;
        cbIdKaryawan.removeAllItems();
        try {
            java.sql.ResultSet rs = DatabaseHelper.executeQuery("SELECT id_karyawan FROM tb_karyawan");
            while (rs != null && rs.next()) {
                cbIdKaryawan.addItem(rs.getString("id_karyawan"));
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        isUpdating = false;
        updateKaryawanDetails();
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        try {
            java.sql.ResultSet rs = DatabaseHelper.executeQuery("SELECT * FROM tb_penggajian");
            while (rs != null && rs.next()) {
                String tglMySQL = rs.getString("tanggal_gaji"); // corrected to matches db schema tb_penggajian!
                String tglDisplay = tglMySQL;
                try {
                    java.util.Date date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tglMySQL);
                    tglDisplay = new java.text.SimpleDateFormat("dd-MM-yyyy").format(date);
                } catch (Exception ignored) {}
                
                tableModel.addRow(new Object[]{
                    rs.getString("id_gaji"), tglDisplay, rs.getString("id_karyawan"), 
                    rs.getString("nama_karyawan"), rs.getString("golongan"), 
                    rs.getDouble("jumlah_gaji"), rs.getDouble("jumlah_lembur"), 
                    rs.getDouble("potongan"), rs.getDouble("total_gaji")
                });
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void tabelKlik() {
        int row = table.getSelectedRow();
        if(row >= 0) {
            txtIdGaji.setText(tableModel.getValueAt(row, 0).toString());
            try {
                java.util.Date date = new java.text.SimpleDateFormat("dd-MM-yyyy").parse(tableModel.getValueAt(row, 1).toString());
                dateGaji.setDate(date);
            } catch (Exception ignored) {
                dateGaji.setDate(null);
            }
            
            isUpdating = true;
            cbIdKaryawan.setSelectedItem(tableModel.getValueAt(row, 2).toString());
            isUpdating = false;
            
            txtNamaKaryawan.setText(tableModel.getValueAt(row, 3).toString());
            txtGolongan.setText(tableModel.getValueAt(row, 4).toString());
            txtJumlahGaji.setText(tableModel.getValueAt(row, 5).toString());
            txtJumlahLembur.setText(tableModel.getValueAt(row, 6).toString());
            txtPotongan.setText(tableModel.getValueAt(row, 7).toString());
            txtTotalGaji.setText(tableModel.getValueAt(row, 8).toString());
        }
    }
    
    private void resetForm() {
        txtIdGaji.setText("");
        dateGaji.setDate(null);
        isUpdating = true;
        if(cbIdKaryawan.getItemCount() > 0) cbIdKaryawan.setSelectedIndex(0);
        isUpdating = false;
        txtNamaKaryawan.setText("");
        txtGolongan.setText("");
        txtJumlahGaji.setText("");
        txtJumlahLembur.setText("");
        txtPotongan.setText("0");
        txtTotalGaji.setText("");
    }
    
    private void simpanData() {
        try {
            String id = txtIdGaji.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID Gaji tidak boleh kosong!");
                return;
            }
            if (cbIdKaryawan.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Data Karyawan kosong!");
                return;
            }
            java.util.Date selectedDate = dateGaji.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Harap pilih Tanggal Gaji terlebih dahulu!", "Error Tanggal", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String tglMySQL = new java.text.SimpleDateFormat("yyyy-MM-dd").format(selectedDate);
            String idKaryawan = cbIdKaryawan.getSelectedItem().toString();
            String nama = txtNamaKaryawan.getText();
            String golongan = txtGolongan.getText();
            
            if (txtTotalGaji.getText().isEmpty()) {
                hitungGaji();
            }
            
            double gapok = Double.parseDouble(txtJumlahGaji.getText().isEmpty() ? "0" : txtJumlahGaji.getText());
            double lembur = Double.parseDouble(txtJumlahLembur.getText().isEmpty() ? "0" : txtJumlahLembur.getText());
            double potongan = Double.parseDouble(txtPotongan.getText().isEmpty() ? "0" : txtPotongan.getText());
            double total = Double.parseDouble(txtTotalGaji.getText());
            
            String sql = "INSERT INTO tb_penggajian VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            if (DatabaseHelper.executeUpdate(sql, id, tglMySQL, idKaryawan, nama, golongan, gapok, lembur, potongan, total)) {
                JOptionPane.showMessageDialog(this, "Data Penggajian berhasil disimpan!");
                loadData();
                resetForm();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Pastikan format angka benar sebelum menyimpan!", "Error Validasi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateData() {
        try {
            String id = txtIdGaji.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Harap pilih data dari tabel terlebih dahulu!");
                return;
            }
            java.util.Date selectedDate = dateGaji.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Harap pilih Tanggal Gaji terlebih dahulu!", "Error Tanggal", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String tglMySQL = new java.text.SimpleDateFormat("yyyy-MM-dd").format(selectedDate);
            String idKaryawan = cbIdKaryawan.getSelectedItem().toString();
            String nama = txtNamaKaryawan.getText();
            String golongan = txtGolongan.getText();
            
            if (txtTotalGaji.getText().isEmpty()) hitungGaji();
            
            double gapok = Double.parseDouble(txtJumlahGaji.getText().isEmpty() ? "0" : txtJumlahGaji.getText());
            double lembur = Double.parseDouble(txtJumlahLembur.getText().isEmpty() ? "0" : txtJumlahLembur.getText());
            double potongan = Double.parseDouble(txtPotongan.getText().isEmpty() ? "0" : txtPotongan.getText());
            double total = Double.parseDouble(txtTotalGaji.getText());
            
            String sql = "UPDATE tb_penggajian SET tanggal=?, id_karyawan=?, nama_karyawan=?, golongan=?, gaji_pokok=?, lembur=?, potongan=?, total_gaji=? WHERE id_penggajian=?";
            if (DatabaseHelper.executeUpdate(sql, tglMySQL, idKaryawan, nama, golongan, gapok, lembur, potongan, total, id)) {
                JOptionPane.showMessageDialog(this, "Data Penggajian berhasil diupdate!");
                loadData();
                resetForm();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format angka salah!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteData() {
        String id = txtIdGaji.getText();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus dari tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int konfirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data Penggajian " + id + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM tb_penggajian WHERE id_penggajian=?";
            if (DatabaseHelper.executeUpdate(sql, id)) {
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                loadData();
                resetForm();
            }
        }
    }
    
    private void hitungGaji() {
        try {
            double gaji = txtJumlahGaji.getText().isEmpty() ? 0 : Double.parseDouble(txtJumlahGaji.getText());
            double lembur = txtJumlahLembur.getText().isEmpty() ? 0 : Double.parseDouble(txtJumlahLembur.getText());
            double potongan = txtPotongan.getText().isEmpty() ? 0 : Double.parseDouble(txtPotongan.getText());
            
            double total = gaji + lembur - potongan;
            txtTotalGaji.setText(String.valueOf(total));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format angka salah!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        return lbl;
    }
}
