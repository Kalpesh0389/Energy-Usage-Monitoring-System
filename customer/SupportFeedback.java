package customer;

import db.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SupportFeedback extends JFrame {
    public SupportFeedback(String meterNo) {
        setTitle("Support & Feedback");
        setSize(500, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel title = new JLabel("Contact Support / Raise a Ticket");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(80, 20, 400, 30);
        add(title);

        JLabel subjectLabel = new JLabel("Subject:");
        subjectLabel.setBounds(50, 70, 100, 25);
        add(subjectLabel);

        JTextField subjectField = new JTextField();
        subjectField.setBounds(130, 70, 300, 25);
        add(subjectField);

        JLabel messageLabel = new JLabel("Message:");
        messageLabel.setBounds(50, 110, 100, 25);
        add(messageLabel);

        JTextArea messageArea = new JTextArea();
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBounds(130, 110, 300, 150);
        add(scrollPane);

        // Create custom white rounded buttons
        RoundedButton submitBtn = new RoundedButton("Submit");
        submitBtn.setBounds(130, 280, 100, 35);
        add(submitBtn);

        RoundedButton backBtn = new RoundedButton("Back");
        backBtn.setBounds(260, 280, 100, 35);
        add(backBtn);

        submitBtn.addActionListener(e -> {
            String subject = subjectField.getText().trim();
            String msg = messageArea.getText().trim();

            if (subject.isEmpty() || msg.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            try (Connection con = DatabaseConnection.connect()) {
                String sql = "INSERT INTO support_tickets (meter_no, subject, message) VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, meterNo);
                ps.setString(2, subject);
                ps.setString(3, msg);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Ticket submitted successfully!");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    public static void main(String[] args) {
        new SupportFeedback("123");
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
