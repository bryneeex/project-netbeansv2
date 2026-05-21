package penggajian;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * DatePickerField - Komponen input tanggal dengan kalender popup.
 * Klik field atau tombol kalender untuk memilih tanggal.
 * Format tampilan: dd-MM-yyyy
 */
public class DatePickerField extends JPanel {

    private JTextField txtDisplay;
    private JButton btnCalendar;
    private JDialog popupDialog;
    private Calendar selectedCal;
    private Calendar viewCal;

    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final Color CLR_HEADER   = new Color(41, 128, 185);
    private static final Color CLR_TODAY    = new Color(231, 76, 60);
    private static final Color CLR_SELECTED = new Color(46, 204, 113);
    private static final Color CLR_WEEKEND  = new Color(230, 126, 34);
    private static final Color CLR_BG       = new Color(236, 240, 241);
    private static final Color CLR_DAY_BG   = Color.WHITE;

    private static final String[] MONTH_NAMES = {
        "Januari","Februari","Maret","April","Mei","Juni",
        "Juli","Agustus","September","Oktober","November","Desember"
    };
    private static final String[] DAY_NAMES = {"Min","Sen","Sel","Rab","Kam","Jum","Sab"};

    public DatePickerField() {
        this(null);
    }

    public DatePickerField(java.util.Date initialDate) {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        selectedCal = Calendar.getInstance();
        if (initialDate != null) {
            selectedCal.setTime(initialDate);
        } else {
            selectedCal = null;
        }
        viewCal = Calendar.getInstance();

        // -- Text field --
        txtDisplay = new JTextField();
        txtDisplay.setEditable(false);
        txtDisplay.setBackground(Color.WHITE);
        txtDisplay.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        txtDisplay.setBorder(new CompoundBorder(
            new LineBorder(new Color(180, 180, 180), 1, true),
            new EmptyBorder(4, 8, 4, 4)
        ));

        if (selectedCal != null) {
            txtDisplay.setText(formatDate(selectedCal));
        } else {
            txtDisplay.setText("");
            txtDisplay.setForeground(new Color(150, 150, 150));
        }

        // -- Calendar button --
        btnCalendar = new JButton("📅");
        btnCalendar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btnCalendar.setFocusPainted(false);
        btnCalendar.setBorder(new CompoundBorder(
            new LineBorder(new Color(180, 180, 180), 1, true),
            new EmptyBorder(4, 6, 4, 6)
        ));
        btnCalendar.setBackground(CLR_HEADER);
        btnCalendar.setForeground(Color.WHITE);
        btnCalendar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCalendar.setToolTipText("Klik untuk memilih tanggal");

        add(txtDisplay, BorderLayout.CENTER);
        add(btnCalendar, BorderLayout.EAST);

        // -- Action listeners --
        ActionListener openCalendar = e -> showCalendarPopup();
        btnCalendar.addActionListener(openCalendar);
        txtDisplay.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { showCalendarPopup(); }
        });
    }

    // -------------------------------------------------------
    //  Public API
    // -------------------------------------------------------

    /** Ambil tanggal yang dipilih (null jika belum dipilih) */
    public java.util.Date getDate() {
        if (selectedCal == null) return null;
        return selectedCal.getTime();
    }

    /** Set tanggal secara programatik */
    public void setDate(java.util.Date date) {
        if (date == null) {
            selectedCal = null;
            txtDisplay.setText("");
            txtDisplay.setForeground(new Color(150, 150, 150));
        } else {
            selectedCal = Calendar.getInstance();
            selectedCal.setTime(date);
            txtDisplay.setText(formatDate(selectedCal));
            txtDisplay.setForeground(Color.BLACK);
        }
        if (viewCal == null) viewCal = Calendar.getInstance();
        if (selectedCal != null) viewCal.setTime(selectedCal.getTime());
    }

    /** Ambil tanggal dalam format dd-MM-yyyy (kosong jika belum dipilih) */
    public String getDateText() {
        if (selectedCal == null) return "";
        return formatDate(selectedCal);
    }

    /** Ambil tanggal dalam format yyyy-MM-dd untuk database (kosong jika belum dipilih) */
    public String getDateMySQL() {
        if (selectedCal == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(selectedCal.getTime());
    }

    /** Set tanggal dari String format dd-MM-yyyy atau yyyy-MM-dd */
    public void setDateFromString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            setDate(null);
            return;
        }
        try {
            // Coba format dd-MM-yyyy
            java.util.Date d = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr.trim());
            setDate(d);
            return;
        } catch (Exception ignored) {}
        try {
            // Coba format yyyy-MM-dd
            java.util.Date d = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr.trim());
            setDate(d);
        } catch (Exception ignored) {}
    }

    // -------------------------------------------------------
    //  Calendar Popup
    // -------------------------------------------------------

    private void showCalendarPopup() {
        // Buat dialog popup
        Window owner = SwingUtilities.getWindowAncestor(this);
        popupDialog = new JDialog(owner, ModalityType.MODELESS);
        popupDialog.setUndecorated(true);
        popupDialog.setBackground(new Color(0, 0, 0, 0));

        JPanel calPanel = buildCalendarPanel();
        calPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(41, 128, 185), 2, true),
            new EmptyBorder(0, 0, 0, 0)
        ));
        popupDialog.add(calPanel);
        popupDialog.pack();

        // Posisikan di bawah field
        Point loc = getLocationOnScreen();
        int popupY = loc.y + getHeight() + 2;
        int popupX = loc.x;

        // Jangan keluar layar kanan
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (popupX + popupDialog.getWidth() > screen.width) {
            popupX = screen.width - popupDialog.getWidth() - 5;
        }
        // Jangan keluar layar bawah
        if (popupY + popupDialog.getHeight() > screen.height) {
            popupY = loc.y - popupDialog.getHeight() - 2;
        }

        popupDialog.setLocation(popupX, popupY);
        popupDialog.setVisible(true);

        // Tutup saat klik di luar
        popupDialog.addWindowFocusListener(new WindowFocusListener() {
            public void windowLostFocus(WindowEvent e) { closePopup(); }
            public void windowGainedFocus(WindowEvent e) {}
        });
    }

    private void closePopup() {
        if (popupDialog != null && popupDialog.isVisible()) {
            popupDialog.dispose();
            popupDialog = null;
        }
    }

    private JPanel buildCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(CLR_BG);

        // ---- Header: navigasi bulan & tahun ----
        JPanel header = new JPanel(new BorderLayout(5, 0));
        header.setBackground(CLR_HEADER);
        header.setBorder(new EmptyBorder(8, 10, 8, 10));

        JButton btnPrevYear  = makeNavBtn("≪");
        JButton btnPrevMonth = makeNavBtn("‹");
        JButton btnNextMonth = makeNavBtn("›");
        JButton btnNextYear  = makeNavBtn("≫");

        JLabel lblMonthYear = new JLabel("", SwingConstants.CENTER);
        lblMonthYear.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblMonthYear.setForeground(Color.WHITE);

        JPanel navLeft  = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        navLeft.setOpaque(false);
        navLeft.add(btnPrevYear);
        navLeft.add(btnPrevMonth);

        JPanel navRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        navRight.setOpaque(false);
        navRight.add(btnNextMonth);
        navRight.add(btnNextYear);

        header.add(navLeft,      BorderLayout.WEST);
        header.add(lblMonthYear, BorderLayout.CENTER);
        header.add(navRight,     BorderLayout.EAST);

        // ---- Hari-hari ----
        JPanel daysWrapper = new JPanel(new BorderLayout());
        daysWrapper.setBackground(CLR_BG);
        daysWrapper.setBorder(new EmptyBorder(5, 8, 8, 8));

        // Header nama hari
        JPanel dayNamesPanel = new JPanel(new GridLayout(1, 7, 4, 0));
        dayNamesPanel.setOpaque(false);
        for (int i = 0; i < DAY_NAMES.length; i++) {
            JLabel lbl = new JLabel(DAY_NAMES[i], SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            lbl.setForeground(i == 0 || i == 6 ? CLR_WEEKEND : new Color(80, 80, 80));
            dayNamesPanel.add(lbl);
        }

        // Grid hari
        JPanel daysGrid = new JPanel(new GridLayout(6, 7, 4, 4));
        daysGrid.setOpaque(false);

        Runnable[] refreshRef = {null};
        Runnable refresh = () -> {
            lblMonthYear.setText(MONTH_NAMES[viewCal.get(Calendar.MONTH)]
                                  + "  " + viewCal.get(Calendar.YEAR));
            daysGrid.removeAll();

            Calendar tmp = (Calendar) viewCal.clone();
            tmp.set(Calendar.DAY_OF_MONTH, 1);
            int firstDow = tmp.get(Calendar.DAY_OF_WEEK) - 1; // 0=Sun
            int daysInMonth = tmp.getActualMaximum(Calendar.DAY_OF_MONTH);

            Calendar today = Calendar.getInstance();

            // Kosongkan slot sebelum hari pertama
            for (int i = 0; i < firstDow; i++) {
                daysGrid.add(new JLabel(""));
            }

            for (int d = 1; d <= daysInMonth; d++) {
                final int day = d;
                int dow = (firstDow + d - 1) % 7; // 0=Sun, 6=Sat

                JButton btn = new JButton(String.valueOf(d));
                btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setPreferredSize(new Dimension(34, 30));
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                boolean isToday = (d == today.get(Calendar.DAY_OF_MONTH)
                    && viewCal.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                    && viewCal.get(Calendar.YEAR) == today.get(Calendar.YEAR));

                boolean isSelected = selectedCal != null
                    && d == selectedCal.get(Calendar.DAY_OF_MONTH)
                    && viewCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH)
                    && viewCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR);

                if (isSelected) {
                    btn.setBackground(CLR_SELECTED);
                    btn.setForeground(Color.WHITE);
                    btn.setFont(btn.getFont().deriveFont(Font.BOLD));
                    btn.setBorder(new LineBorder(CLR_SELECTED.darker(), 1, true));
                } else if (isToday) {
                    btn.setBackground(CLR_TODAY);
                    btn.setForeground(Color.WHITE);
                    btn.setFont(btn.getFont().deriveFont(Font.BOLD));
                    btn.setBorder(new LineBorder(CLR_TODAY.darker(), 1, true));
                } else {
                    btn.setBackground(CLR_DAY_BG);
                    btn.setForeground(dow == 0 || dow == 6 ? CLR_WEEKEND : Color.DARK_GRAY);
                    btn.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
                }

                btn.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        if (!btn.getBackground().equals(CLR_SELECTED)) {
                            btn.setBackground(new Color(174, 214, 241));
                        }
                    }
                    public void mouseExited(MouseEvent e) {
                        if (!btn.getBackground().equals(CLR_SELECTED)) {
                            btn.setBackground(isToday ? CLR_TODAY : CLR_DAY_BG);
                        }
                    }
                });

                btn.addActionListener(e -> {
                    selectedCal = (Calendar) viewCal.clone();
                    selectedCal.set(Calendar.DAY_OF_MONTH, day);
                    txtDisplay.setText(formatDate(selectedCal));
                    txtDisplay.setForeground(Color.BLACK);
                    closePopup();
                });

                daysGrid.add(btn);
            }

            // Isi sisa slot
            int total = firstDow + daysInMonth;
            int remainder = total % 7 == 0 ? 0 : 7 - (total % 7);
            for (int i = 0; i < remainder; i++) daysGrid.add(new JLabel(""));

            daysGrid.revalidate();
            daysGrid.repaint();
        };
        refreshRef[0] = refresh;

        btnPrevMonth.addActionListener(e -> { viewCal.add(Calendar.MONTH, -1); refresh.run(); });
        btnNextMonth.addActionListener(e -> { viewCal.add(Calendar.MONTH,  1); refresh.run(); });
        btnPrevYear.addActionListener(e ->  { viewCal.add(Calendar.YEAR,  -1); refresh.run(); });
        btnNextYear.addActionListener(e ->  { viewCal.add(Calendar.YEAR,   1); refresh.run(); });

        refresh.run();

        // ---- Footer: tombol "Hari Ini" ----
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        footer.setBackground(CLR_BG);

        JButton btnToday = new JButton("Hari Ini");
        btnToday.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnToday.setBackground(CLR_HEADER);
        btnToday.setForeground(Color.WHITE);
        btnToday.setFocusPainted(false);
        btnToday.setBorderPainted(false);
        btnToday.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnToday.addActionListener(e -> {
            selectedCal = Calendar.getInstance();
            viewCal = (Calendar) selectedCal.clone();
            txtDisplay.setText(formatDate(selectedCal));
            txtDisplay.setForeground(Color.BLACK);
            refresh.run();
            closePopup();
        });

        JButton btnClear = new JButton("Hapus");
        btnClear.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnClear.setBackground(new Color(189, 195, 199));
        btnClear.setForeground(new Color(44, 62, 80));
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> {
            selectedCal = null;
            txtDisplay.setText("");
            txtDisplay.setForeground(new Color(150, 150, 150));
            closePopup();
        });

        footer.add(btnToday);
        footer.add(btnClear);

        daysWrapper.add(dayNamesPanel, BorderLayout.NORTH);
        daysWrapper.add(daysGrid, BorderLayout.CENTER);

        panel.add(header,      BorderLayout.NORTH);
        panel.add(daysWrapper, BorderLayout.CENTER);
        panel.add(footer,      BorderLayout.SOUTH);

        return panel;
    }

    private JButton makeNavBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(CLR_HEADER);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(new Color(174, 214, 241)); }
            public void mouseExited(MouseEvent e)  { btn.setForeground(Color.WHITE); }
        });
        return btn;
    }

    private String formatDate(Calendar cal) {
        return new SimpleDateFormat(DATE_FORMAT).format(cal.getTime());
    }
}
