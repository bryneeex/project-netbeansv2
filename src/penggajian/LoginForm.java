package penggajian;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;

    public LoginForm() {
        setTitle("Login - Aplikasi Penggajian");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Background biru sesuai PRD
        getContentPane().setBackground(new Color(30, 35, 45));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel lblTitle = new JLabel("LOGIN SISTEM");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblUser = new JLabel("Username:");
        lblUser.setForeground(Color.WHITE);
        txtUsername = new JTextField(15);
        
        JLabel lblPass = new JLabel("Password:");
        lblPass.setForeground(Color.WHITE);
        txtPassword = new JPasswordField(15);
        
        // Sesuai PRD, tombol harus menggunakan image icon (di sini disiapkan tempatnya)
        // btnLogin.setIcon(new ImageIcon("path/to/icon.png"));
        btnLogin = new JButton("Login");
        btnExit = new JButton("Exit");
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1;
        add(lblUser, gbc);
        gbc.gridx = 1;
        add(txtUsername, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        add(lblPass, gbc);
        gbc.gridx = 1;
        add(txtPassword, gbc);
        
        JPanel panelBtn = new JPanel();
        panelBtn.setOpaque(false);
        panelBtn.add(btnLogin);
        panelBtn.add(btnExit);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(panelBtn, gbc);
        
        // Events
        btnLogin.addActionListener(this::prosesLogin);
        btnExit.addActionListener(e -> System.exit(0));
    }

    private void prosesLogin(ActionEvent e) {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());
        
        if (DatabaseHelper.cekLogin(user, pass)) {
            JOptionPane.showMessageDialog(this, "Login Berhasil!");
            new Dashboard().setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
