package penggajian;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Dashboard - Menu Utama");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Background biru
        getContentPane().setBackground(new Color(20, 25, 35));
        setLayout(new BorderLayout());
        
        JLabel lblTitle = new JLabel("SISTEM INFORMASI PENGGAJIAN KARYAWAN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);
        
        JPanel pnlMenu = new JPanel(new GridLayout(2, 2, 20, 20));
        pnlMenu.setOpaque(false);
        pnlMenu.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        
        JButton btnKaryawan = createMenuButton("Form Karyawan");
        JButton btnGolongan = createMenuButton("Form Golongan");
        JButton btnLembur = createMenuButton("Form Lembur");
        JButton btnPenggajian = createMenuButton("Form Penggajian");
        
        pnlMenu.add(btnKaryawan);
        pnlMenu.add(btnGolongan);
        pnlMenu.add(btnLembur);
        pnlMenu.add(btnPenggajian);
        
        add(pnlMenu, BorderLayout.CENTER);
        
        // Event Listeners
        btnKaryawan.addActionListener(e -> new FormKaryawan().setVisible(true));
        btnGolongan.addActionListener(e -> new FormGolongan().setVisible(true));
        btnLembur.addActionListener(e -> new FormLembur().setVisible(true));
        btnPenggajian.addActionListener(e -> new FormPenggajian().setVisible(true));
    }
    
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
