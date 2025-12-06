package admin;

import auth.AdminLogin;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class AdminDashboard extends JFrame {
    private BufferedImage backgroundImage;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("icon/admin.jpg")); // Your background image path
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Background panel
        JPanel backgroundPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        JLabel title = new JLabel("Admin Dashboard", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.BLACK); // Title color
        title.setBounds(150, 30, 300, 30);
        backgroundPanel.add(title);

        int btnWidth = 200, btnHeight = 40;
        int x = 200, y = 90, gap = 50;

        // Create buttons with white style
        RoundedButton viewCustomersBtn = new RoundedButton("View All Customers");
        viewCustomersBtn.setBounds(x, y, btnWidth, btnHeight);
        backgroundPanel.add(viewCustomersBtn);

        RoundedButton generateBillBtn = new RoundedButton("Generate Bill");
        generateBillBtn.setBounds(x, y + gap, btnWidth, btnHeight);
        backgroundPanel.add(generateBillBtn);

        RoundedButton viewBillStatusBtn = new RoundedButton("View Bill Status");
        viewBillStatusBtn.setBounds(x, y + 2 * gap, btnWidth, btnHeight);
        backgroundPanel.add(viewBillStatusBtn);

        RoundedButton viewCustomerHistoryBtn = new RoundedButton("View Customer History");
        viewCustomerHistoryBtn.setBounds(x, y + 3 * gap, btnWidth, btnHeight);
        backgroundPanel.add(viewCustomerHistoryBtn);

        RoundedButton supportTicketsBtn = new RoundedButton("View Support Tickets");
        supportTicketsBtn.setBounds(x, y + 4 * gap, btnWidth, btnHeight);
        backgroundPanel.add(supportTicketsBtn);

        RoundedButton backBtn = new RoundedButton("Back");
        backBtn.setBounds(250, y + 5 * gap, 100, 35);
        backgroundPanel.add(backBtn);

        // Action Listeners
        viewCustomersBtn.addActionListener(e -> new ViewCustomers(this));
        generateBillBtn.addActionListener(e -> new GenerateBill(this));
        viewBillStatusBtn.addActionListener(e -> new ViewBillStatus(this));
        viewCustomerHistoryBtn.addActionListener(e -> {
            String meterNo = JOptionPane.showInputDialog(this, "Enter Meter Number to view history:");
            if (meterNo != null && !meterNo.trim().isEmpty()) {
                new ViewCustomerHistory(this, meterNo);
            }
        });
        supportTicketsBtn.addActionListener(e -> new ViewSupportTickets(this));
        backBtn.addActionListener(e -> {
            this.dispose();
            new AdminLogin();
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminDashboard::new);
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
