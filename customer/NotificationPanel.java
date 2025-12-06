package customer;

import db.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;

public class NotificationPanel extends JFrame {
    public NotificationPanel(String meterNo) {
        setTitle("Notifications");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Your Notifications", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JTextArea notificationArea = new JTextArea();
        notificationArea.setEditable(false);
        notificationArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        ArrayList<String> notifications = fetchNotifications(meterNo);
        if (notifications.isEmpty()) {
            notificationArea.setText("No notifications available.");
        } else {
            for (String note : notifications) {
                notificationArea.append("• " + note + "\n\n");
            }
        }

        JScrollPane scrollPane = new JScrollPane(notificationArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Back Button Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        RoundedButton backButton = new RoundedButton("Back");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bottomPanel.add(backButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> dispose());

        add(panel);
        setVisible(true);
    }

    private ArrayList<String> fetchNotifications(String meterNo) {
        ArrayList<String> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.connect()) {
            String sql = "SELECT message FROM notifications WHERE meter_no = ? ORDER BY timestamp DESC LIMIT 10";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, meterNo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("message"));
            }
        } catch (Exception e) {
            list.add("Failed to fetch notifications.");
        }
        return list;
    }

    public static void main(String[] args) {
        new NotificationPanel("123");
    }

    // Custom white rounded button with hover effect
    class RoundedButton extends JButton {
        private final Color normalBg = Color.WHITE;
        private final Color hoverBg = new Color(220, 220, 220);
        private boolean hovered = false;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.BLACK);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(hovered ? hoverBg : normalBg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

            FontMetrics fm = g2.getFontMetrics();
            int stringWidth = fm.stringWidth(getText());
            int stringHeight = fm.getAscent();

            g2.setColor(getForeground());
            g2.drawString(getText(), (getWidth() - stringWidth) / 2, (getHeight() + stringHeight) / 2 - 3);

            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            g2.dispose();
        }
    }
}
