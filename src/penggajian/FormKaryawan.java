package penggajian;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.toedter.calendar.JDateChooser;

public class FormKaryawan extends JFrame {
    private JTextField txtIdKaryawan, txtNama, txtTempat;
    private JComboBox<String> cbIdGolongan;
    private JRadioButton rbLaki, rbPerempuan, rbMenikah, rbBelum;
    private JTextArea txtAlamat;
    private JTable table;
    private DefaultTableModel tableModel;
    private JDateChooser dateLahir;

    public FormKaryawan() {
        setTitle("Data Karyawan");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(52, 152, 219));
        
        setLayout(new BorderLayout(10, 10));
        
        // Panel Form
        JPanel pnlForm = new JPanel(new GridLayout(8, 2, 5, 5));
        pnlForm.setOpaque(false);
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        pnlForm.add(createLabel("ID Karyawan:"));
        txtIdKaryawan = new JTextField(); pnlForm.add(txtIdKaryawan);
        
        pnlForm.add(createLabel("Nama:"));
        txtNama = new JTextField(); pnlForm.add(txtNama);
        
        pnlForm.add(createLabel("ID Golongan:"));
        cbIdGolongan = new JComboBox<>(new String[]{"G001", "G002", "G003", "G004"});
        pnlForm.add(cbIdGolongan);
        
        pnlForm.add(createLabel("Jenis Kelamin:"));
        JPanel pnlJK = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlJK.setOpaque(false);
        rbLaki = new JRadioButton("Laki-laki"); rbLaki.setOpaque(false); rbLaki.setForeground(Color.WHITE);
        rbPerempuan = new JRadioButton("Perempuan"); rbPerempuan.setOpaque(false); rbPerempuan.setForeground(Color.WHITE);
        ButtonGroup bgJK = new ButtonGroup(); bgJK.add(rbLaki); bgJK.add(rbPerempuan);
        pnlJK.add(rbLaki); pnlJK.add(rbPerempuan);
        pnlForm.add(pnlJK);
        
        pnlForm.add(createLabel("Tempat Lahir:"));
        txtTempat = new JTextField(); pnlForm.add(txtTempat);
        
        pnlForm.add(createLabel("Tanggal Lahir:"));
        dateLahir = new JDateChooser();
        dateLahir.setDateFormatString("dd-MM-yyyy");
        pnlForm.add(dateLahir);
        
        pnlForm.add(createLabel("Status:"));
        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlStatus.setOpaque(false);
        rbMenikah = new JRadioButton("Menikah"); rbMenikah.setOpaque(false); rbMenikah.setForeground(Color.WHITE);
        rbBelum = new JRadioButton("Belum Menikah"); rbBelum.setOpaque(false); rbBelum.setForeground(Color.WHITE);
        ButtonGroup bgStatus = new ButtonGroup(); bgStatus.add(rbMenikah); bgStatus.add(rbBelum);
        pnlStatus.add(rbMenikah); pnlStatus.add(rbBelum);
        pnlForm.add(pnlStatus);
        
        pnlForm.add(createLabel("Alamat:"));
        txtAlamat = new JTextArea(3, 20);
        pnlForm.add(new JScrollPane(txtAlamat));
        
        // Panel Buttons
        JPanel pnlButtons = new JPanel();
        pnlButtons.setOpaque(false);
        JButton btnSave = new JButton("Save");
        JButton btnReset = new JButton("Reset");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnExit = new JButton("Exit");
        
        pnlButtons.add(btnSave); pnlButtons.add(btnReset);
        pnlButtons.add(btnUpdate); pnlButtons.add(btnDelete); pnlButtons.add(btnExit);
        
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(pnlForm, BorderLayout.CENTER);
        pnlTop.add(pnlButtons, BorderLayout.SOUTH);
        
        add(pnlTop, BorderLayout.NORTH);
        
        // Panel Table
        String[] cols = {"ID", "Nama", "Golongan", "L/P", "Tempat", "Tgl Lahir", "Status", "Alamat"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        btnExit.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> simpanData());
        btnUpdate.addActionListener(e -> updateData());
        btnDelete.addActionListener(e -> deleteData());
        btnReset.addActionListener(e -> resetForm());
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelKlik();
            }
        });
        
        loadData();
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        try {
            java.sql.ResultSet rs = DatabaseHelper.executeQuery("SELECT * FROM tb_karyawan");
            while (rs != null && rs.next()) {
                String tglMySQL = rs.getString("tanggal_lahir");
                String tglDisplay = tglMySQL;
                // Format YYYY-MM-DD kembali ke DD-MM-YYYY untuk display
                try {
                    java.text.SimpleDateFormat formatDB = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    java.text.SimpleDateFormat formatDisplay = new java.text.SimpleDateFormat("dd-MM-yyyy");
                    java.util.Date date = formatDB.parse(tglMySQL);
                    tglDisplay = formatDisplay.format(date);
                } catch (Exception ignored) {}
                
                tableModel.addRow(new Object[]{
                    rs.getString("id_karyawan"), rs.getString("nama"), rs.getString("id_golongan"),
                    rs.getString("jenis_kelamin"), rs.getString("tempat_lahir"), tglDisplay,
                    rs.getString("status"), rs.getString("alamat")
                });
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void tabelKlik() {
        int row = table.getSelectedRow();
        if(row >= 0) {
            txtIdKaryawan.setText(tableModel.getValueAt(row, 0).toString());
            txtNama.setText(tableModel.getValueAt(row, 1).toString());
            cbIdGolongan.setSelectedItem(tableModel.getValueAt(row, 2).toString());
            
            String jk = tableModel.getValueAt(row, 3).toString().toLowerCase();
            if(jk.contains("laki")) rbLaki.setSelected(true);
            else if(jk.contains("perempuan")) rbPerempuan.setSelected(true);
            else { rbLaki.setSelected(false); rbPerempuan.setSelected(false); }
            
            txtTempat.setText(tableModel.getValueAt(row, 4).toString());
            try {
                java.util.Date date = new java.text.SimpleDateFormat("dd-MM-yyyy").parse(tableModel.getValueAt(row, 5).toString());
                dateLahir.setDate(date);
            } catch (Exception ignored) {
                dateLahir.setDate(null);
            }
            
            String stat = tableModel.getValueAt(row, 6).toString().toLowerCase();
            if(stat.contains("menikah") && !stat.contains("belum")) rbMenikah.setSelected(true);
            else if(stat.contains("belum") || stat.contains("single")) rbBelum.setSelected(true);
            else { rbMenikah.setSelected(false); rbBelum.setSelected(false); }
            
            txtAlamat.setText(tableModel.getValueAt(row, 7).toString());
        }
    }
    
    private void resetForm() {
        txtIdKaryawan.setText("");
        txtNama.setText("");
        cbIdGolongan.setSelectedIndex(0);
        rbLaki.setSelected(false); rbPerempuan.setSelected(false);
        txtTempat.setText("");
        dateLahir.setDate(null);
        rbMenikah.setSelected(false); rbBelum.setSelected(false);
        txtAlamat.setText("");
    }
    
    private void simpanData() {
        String id = txtIdKaryawan.getText();
        String nama = txtNama.getText();
        String idGol = cbIdGolongan.getSelectedItem().toString();
        String jk = rbLaki.isSelected() ? "Laki-laki" : (rbPerempuan.isSelected() ? "Perempuan" : "");
        String tempat = txtTempat.getText();
        java.util.Date selectedDate = dateLahir.getDate();
        String status = rbMenikah.isSelected() ? "Menikah" : (rbBelum.isSelected() ? "Belum Menikah" : "");
        String alamat = txtAlamat.getText();

        if (id.isEmpty() || nama.isEmpty() || jk.isEmpty() || status.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harap lengkapi semua data wajib!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Harap pilih Tanggal Lahir terlebih dahulu!", "Error Tanggal", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String tglMySQL = new java.text.SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

        String sql = "INSERT INTO tb_karyawan (id_karyawan, nama, id_golongan, jenis_kelamin, tempat_lahir, tanggal_lahir, status, alamat) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        if (DatabaseHelper.executeUpdate(sql, id, nama, idGol, jk, tempat, tglMySQL, status, alamat)) {
            JOptionPane.showMessageDialog(this, "Data Karyawan berhasil disimpan!");
            loadData();
            resetForm();
        }
    }
    
    private void updateData() {
        String id = txtIdKaryawan.getText();
        String nama = txtNama.getText();
        String idGol = cbIdGolongan.getSelectedItem().toString();
        String jk = rbLaki.isSelected() ? "Laki-laki" : (rbPerempuan.isSelected() ? "Perempuan" : "");
        String tempat = txtTempat.getText();
        java.util.Date selectedDate = dateLahir.getDate();
        String status = rbMenikah.isSelected() ? "Menikah" : (rbBelum.isSelected() ? "Belum Menikah" : "");
        String alamat = txtAlamat.getText();

        if (id.isEmpty() || nama.isEmpty() || jk.isEmpty() || status.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harap pilih data dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Harap pilih Tanggal Lahir terlebih dahulu!", "Error Tanggal", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String tglMySQL = new java.text.SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

        String sql = "UPDATE tb_karyawan SET nama=?, id_golongan=?, jenis_kelamin=?, tempat_lahir=?, tanggal_lahir=?, status=?, alamat=? WHERE id_karyawan=?";
        if (DatabaseHelper.executeUpdate(sql, nama, idGol, jk, tempat, tglMySQL, status, alamat, id)) {
            JOptionPane.showMessageDialog(this, "Data Karyawan berhasil diupdate!");
            loadData();
            resetForm();
        }
    }
    
    private void deleteData() {
        String id = txtIdKaryawan.getText();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus dari tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int konfirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data Karyawan " + id + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM tb_karyawan WHERE id_karyawan=?";
            if (DatabaseHelper.executeUpdate(sql, id)) {
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                loadData();
                resetForm();
            }
        }
    }
    
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        return lbl;
    }
}
