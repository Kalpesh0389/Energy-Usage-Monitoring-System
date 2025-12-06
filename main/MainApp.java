package main;
import auth.CustomerLogin;
import auth.CustomerRegister;
import auth.AdminLogin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class MainApp extends JFrame {
    BufferedImage backgroundImage;

    public MainApp() {
        setTitle("Electricity Billing System");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        try {
            backgroundImage = ImageIO.read(new File("icon/main.jpeg")); // Make sure the image path is valid
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Background Panel with Image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, 800, 500);
        add(backgroundPanel);

        // Professional Title with subtle shadow
        JLabel title = new JLabel("Energy Usage Monitoring System", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0, 0, 0, 120)); // subtle shadow
                g2.setFont(getFont());
                g2.drawString(getText(), 2, getHeight() - 6); // shadow offset
                g2.dispose();
                super.paintComponent(g);
            }
        };
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(255, 240, 210)); // Soft creamy white
        title.setBounds(100, 30, 600, 60);
        backgroundPanel.add(title);

        // Bottom Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 15));
        buttonPanel.setBounds(0, 380, 800, 80);
        backgroundPanel.add(buttonPanel);

        JButton customerLoginBtn = createStyledButton("Customer Login");
        JButton registerBtn = createStyledButton("New Customer");
        JButton adminLoginBtn = createStyledButton("Admin Login");

        buttonPanel.add(customerLoginBtn);
        buttonPanel.add(registerBtn);
        buttonPanel.add(adminLoginBtn);

        // Actions
        customerLoginBtn.addActionListener((ActionEvent e) -> new CustomerLogin());
        registerBtn.addActionListener((ActionEvent e) -> new CustomerRegister());
        adminLoginBtn.addActionListener((ActionEvent e) -> new AdminLogin());

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        return new RoundedButton(text);
    }

    // Custom Rounded Button Class
    class RoundedButton extends JButton {
        private final Color normalBg = new Color(255, 140, 60);  // light orange
        private final Color hoverBg = new Color(210, 100, 30);   // darker orange
        private boolean hover = false;

        public RoundedButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.PLAIN, 16));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (hover) {
                g2.setColor(hoverBg);
            } else {
                g2.setColor(normalBg);
            }
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
            g2.setColor(new Color(210, 100, 30));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", new Color(255, 140, 60));
            UIManager.put("nimbusBlueGrey", new Color(255, 180, 100));
            UIManager.put("control", new Color(255, 230, 200));
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(MainApp::new);
    }
}
