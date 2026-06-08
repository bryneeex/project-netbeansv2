package penggajian;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FormGolongan extends JFrame {
    private JTextField txtIdGolongan, txtNamaGolongan, txtGajiPokok, txtTunjanganIstri;
    private JTextField txtJumlahAnak, txtTunjanganAnak, txtTransport, txtUangMakan;
    private JTable table;
    private DefaultTableModel tableModel;

    public FormGolongan() {
        setTitle("Data Golongan");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(30, 35, 45));
        
        setLayout(new BorderLayout(10, 10));
        
        JPanel pnlForm = new JPanel(new GridLayout(8, 2, 5, 5));
        pnlForm.setOpaque(false);
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        pnlForm.add(createLabel("ID Golongan:"));
        txtIdGolongan = new JTextField(); pnlForm.add(txtIdGolongan);
        
        pnlForm.add(createLabel("Nama Golongan:"));
        txtNamaGolongan = new JTextField(); pnlForm.add(txtNamaGolongan);
        
        pnlForm.add(createLabel("Gaji Pokok:"));
        txtGajiPokok = new JTextField(); pnlForm.add(txtGajiPokok);
        
        pnlForm.add(createLabel("Tunjangan Istri:"));
        txtTunjanganIstri = new JTextField(); pnlForm.add(txtTunjanganIstri);
        
        pnlForm.add(createLabel("Jumlah Anak:"));
        txtJumlahAnak = new JTextField(); pnlForm.add(txtJumlahAnak);
        
        pnlForm.add(createLabel("Tunjangan Anak:"));
        txtTunjanganAnak = new JTextField(); pnlForm.add(txtTunjanganAnak);
        
        pnlForm.add(createLabel("Transport:"));
        txtTransport = new JTextField(); pnlForm.add(txtTransport);
        
        pnlForm.add(createLabel("Uang Makan:"));
        txtUangMakan = new JTextField(); pnlForm.add(txtUangMakan);
        
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
        
        String[] cols = {"ID", "Nama", "Gaji Pokok", "Tj. Istri", "Jml Anak", "Tj. Anak", "Transport", "Makan"};
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
            java.sql.ResultSet rs = DatabaseHelper.executeQuery("SELECT * FROM tb_golongan");
            while (rs != null && rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("id_golongan"), rs.getString("nama_golongan"), 
                    rs.getDouble("gaji_pokok"), rs.getDouble("tunjangan_istri"), 
                    rs.getInt("jumlah_anak"), rs.getDouble("tunjangan_anak"), 
                    rs.getDouble("transport"), rs.getDouble("uang_makan")
                });
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void tabelKlik() {
        int row = table.getSelectedRow();
        if(row >= 0) {
            txtIdGolongan.setText(tableModel.getValueAt(row, 0).toString());
            txtNamaGolongan.setText(tableModel.getValueAt(row, 1).toString());
            txtGajiPokok.setText(tableModel.getValueAt(row, 2).toString());
            txtTunjanganIstri.setText(tableModel.getValueAt(row, 3).toString());
            txtJumlahAnak.setText(tableModel.getValueAt(row, 4).toString());
            txtTunjanganAnak.setText(tableModel.getValueAt(row, 5).toString());
            txtTransport.setText(tableModel.getValueAt(row, 6).toString());
            txtUangMakan.setText(tableModel.getValueAt(row, 7).toString());
        }
    }
    
    private void resetForm() {
        txtIdGolongan.setText("");
        txtNamaGolongan.setText("");
        txtGajiPokok.setText("");
        txtTunjanganIstri.setText("");
        txtJumlahAnak.setText("");
        txtTunjanganAnak.setText("");
        txtTransport.setText("");
        txtUangMakan.setText("");
    }
    
    private void simpanData() {
        try {
            String id = txtIdGolongan.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID Golongan tidak boleh kosong!");
                return;
            }
            
            String nama = txtNamaGolongan.getText();
            double gapok = Double.parseDouble(txtGajiPokok.getText());
            double tjIstri = Double.parseDouble(txtTunjanganIstri.getText());
            int anak = Integer.parseInt(txtJumlahAnak.getText());
            double tjAnak = Double.parseDouble(txtTunjanganAnak.getText());
            double transport = Double.parseDouble(txtTransport.getText());
            double makan = Double.parseDouble(txtUangMakan.getText());
            
            if (anak == 0 && tjAnak > 0) {
                JOptionPane.showMessageDialog(this, "Tunjangan anak harus 0 jika tidak memiliki anak!", "Error Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String sql = "INSERT INTO tb_golongan VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            if (DatabaseHelper.executeUpdate(sql, id, nama, gapok, tjIstri, anak, tjAnak, transport, makan)) {
                JOptionPane.showMessageDialog(this, "Data Golongan berhasil disimpan!");
                loadData();
                resetForm();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Pastikan field numerik diisi dengan angka saja!", "Error Validasi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateData() {
        try {
            String id = txtIdGolongan.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Harap pilih data dari tabel terlebih dahulu!");
                return;
            }
            String nama = txtNamaGolongan.getText();
            double gapok = Double.parseDouble(txtGajiPokok.getText());
            double tjIstri = Double.parseDouble(txtTunjanganIstri.getText());
            int anak = Integer.parseInt(txtJumlahAnak.getText());
            double tjAnak = Double.parseDouble(txtTunjanganAnak.getText());
            double transport = Double.parseDouble(txtTransport.getText());
            double makan = Double.parseDouble(txtUangMakan.getText());
            
            if (anak == 0 && tjAnak > 0) {
                JOptionPane.showMessageDialog(this, "Tunjangan anak harus 0 jika tidak memiliki anak!", "Error Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String sql = "UPDATE tb_golongan SET nama_golongan=?, gaji_pokok=?, tunjangan_istri=?, jumlah_anak=?, tunjangan_anak=?, transport=?, uang_makan=? WHERE id_golongan=?";
            if (DatabaseHelper.executeUpdate(sql, nama, gapok, tjIstri, anak, tjAnak, transport, makan, id)) {
                JOptionPane.showMessageDialog(this, "Data Golongan berhasil diupdate!");
                loadData();
                resetForm();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Pastikan field numerik diisi dengan angka saja!", "Error Validasi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteData() {
        String id = txtIdGolongan.getText();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus dari tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int konfirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data Golongan " + id + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM tb_golongan WHERE id_golongan=?";
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
