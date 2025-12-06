package customer;

import db.DatabaseConnection;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;

public class UpdateInfo extends JFrame {
    BufferedImage backgroundImage;

    public UpdateInfo(String meterNo) {
        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("icon/i.jpg")); // Replace with your actual path
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Update Info");
        setSize(600, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Background panel
        JPanel bgPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        bgPanel.setLayout(new BorderLayout());
        setContentPane(bgPanel);

        // Header
        JLabel header = new JLabel("Update Customer Info", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.add(header);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        Font labelFont = new Font("Arial", Font.BOLD, 15);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(Color.WHITE);
        JTextField nameField = new JTextField();
        nameField.setFont(fieldFont);

        JLabel cityLabel = new JLabel("City:");
        cityLabel.setFont(labelFont);
        cityLabel.setForeground(Color.WHITE);
        JTextField cityField = new JTextField();
        cityField.setFont(fieldFont);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(Color.WHITE);
        JTextField emailField = new JTextField();
        emailField.setFont(fieldFont);

        JLabel passLabel = new JLabel("New Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(Color.WHITE);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(cityLabel);
        formPanel.add(cityField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passLabel);
        formPanel.add(passwordField);

        // Load current customer data
        try (Connection con = DatabaseConnection.connect()) {
            String sql = "SELECT name, city, email FROM customers WHERE meter_no=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, meterNo);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                cityField.setText(rs.getString("city"));
                emailField.setText(rs.getString("email"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }

        // Use custom white rounded buttons
        RoundedButton updateBtn = new RoundedButton("Update");
        RoundedButton backBtn = new RoundedButton("Back");
        updateBtn.setFont(new Font("Arial", Font.PLAIN, 15));
        backBtn.setFont(new Font("Arial", Font.PLAIN, 15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(updateBtn);
        buttonPanel.add(backBtn);

        // Update button logic
        updateBtn.addActionListener(e -> {
            try (Connection con = DatabaseConnection.connect()) {
                String sql = "UPDATE customers SET name=?, city=?, email=?" +
                        (passwordField.getPassword().length > 0 ? ", password=?" : "") +
                        " WHERE meter_no=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, nameField.getText());
                pst.setString(2, cityField.getText());
                pst.setString(3, emailField.getText());

                int index = 4;
                if (passwordField.getPassword().length > 0) {
                    pst.setString(4, new String(passwordField.getPassword()));
                    index = 5;
                }

                pst.setString(index, meterNo);
                int updated = pst.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Info Updated Successfully!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Update Failed.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> dispose());

        // Add to main background panel
        bgPanel.add(headerPanel, BorderLayout.NORTH);
        bgPanel.add(formPanel, BorderLayout.CENTER);
        bgPanel.add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
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
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
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
