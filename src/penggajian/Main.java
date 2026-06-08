package penggajian;

public class Main {
    public static void main(String[] args) {
        // Gunakan tema Nimbus agar tampilan lebih modern
        try {
            // Setup custom modern dark theme colors
            javax.swing.UIManager.put("control", new java.awt.Color(45, 45, 48));
            javax.swing.UIManager.put("info", new java.awt.Color(45, 45, 48));
            javax.swing.UIManager.put("nimbusBase", new java.awt.Color(18, 30, 49));
            javax.swing.UIManager.put("nimbusAlertYellow", new java.awt.Color(248, 187, 0));
            javax.swing.UIManager.put("nimbusDisabledText", new java.awt.Color(128, 128, 128));
            javax.swing.UIManager.put("nimbusFocus", new java.awt.Color(115, 164, 209));
            javax.swing.UIManager.put("nimbusGreen", new java.awt.Color(176, 179, 50));
            javax.swing.UIManager.put("nimbusInfoBlue", new java.awt.Color(66, 139, 221));
            javax.swing.UIManager.put("nimbusLightBackground", new java.awt.Color(30, 30, 30));
            javax.swing.UIManager.put("nimbusOrange", new java.awt.Color(191, 98, 4));
            javax.swing.UIManager.put("nimbusRed", new java.awt.Color(169, 46, 34));
            javax.swing.UIManager.put("nimbusSelectedText", new java.awt.Color(255, 255, 255));
            javax.swing.UIManager.put("nimbusSelectionBackground", new java.awt.Color(50, 100, 150));
            javax.swing.UIManager.put("text", new java.awt.Color(230, 230, 230));
            
            // Customizing Tables
            javax.swing.UIManager.put("Table.background", new java.awt.Color(35, 35, 40));
            javax.swing.UIManager.put("Table.alternateRowColor", new java.awt.Color(45, 45, 50));
            javax.swing.UIManager.put("Table.foreground", new java.awt.Color(230, 230, 230));
            javax.swing.UIManager.put("Table.selectionBackground", new java.awt.Color(60, 120, 180));
            javax.swing.UIManager.put("Table.selectionForeground", new java.awt.Color(255, 255, 255));
            javax.swing.UIManager.put("TableHeader.background", new java.awt.Color(25, 25, 30));
            javax.swing.UIManager.put("TableHeader.foreground", new java.awt.Color(230, 230, 230));

            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        java.awt.EventQueue.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
