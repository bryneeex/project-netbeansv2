package penggajian;

import java.sql.*;

public class DatabaseHelper {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB   = "db_fardhan";
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection conn = null;

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB
                        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta&zeroDateTimeBehavior=convertToNull";
                conn = DriverManager.getConnection(url, USER, PASS);
            }
        } catch (ClassNotFoundException e) {
            // Coba driver lama
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB + "?useSSL=false";
                conn = DriverManager.getConnection(url, USER, PASS);
            } catch (Exception ex) {
                showError("Driver MySQL tidak ditemukan!\nPastikan mysql-connector-java.jar sudah ditambahkan ke Libraries.\n\n" + ex.getMessage());
            }
        } catch (SQLException e) {
            showError("Gagal terhubung ke database!\nPastikan MySQL server berjalan.\n\n" + e.getMessage());
        }
        return conn;
    }

    public static boolean cekLogin(String username, String password) {
        try {
            Connection c = getConnection();
            if (c == null) return false;
            String sql = "SELECT * FROM tb_user WHERE username = ? AND password = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            showError("Error login: " + e.getMessage());
            return false;
        }
    }

    public static ResultSet executeQuery(String sql, Object... params) {
        try {
            Connection c = getConnection();
            if (c == null) return null;
            PreparedStatement ps = c.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeQuery();
        } catch (SQLException e) {
            showError("Gagal mengambil data!\n" + e.getMessage());
            return null;
        }
    }

    public static boolean executeUpdate(String sql, Object... params) {
        try {
            Connection c = getConnection();
            if (c == null) return false;
            PreparedStatement ps = c.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            showError("Gagal menyimpan data!\n" + e.getMessage());
            return false;
        }
    }

    private static void showError(String msg) {
        javax.swing.JOptionPane.showMessageDialog(null, msg, "Database Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}
