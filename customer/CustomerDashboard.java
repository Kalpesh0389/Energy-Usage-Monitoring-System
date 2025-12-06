package customer;

import db.DatabaseConnection;
import main.MainApp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;

public class CustomerDashboard extends JFrame {
    String meterNo;
    JLabel balanceLabel;
    BufferedImage backgroundImage;

    public CustomerDashboard(String meterNo) {
        this.meterNo = meterNo;

        setTitle("Customer Dashboard");
        setSize(600, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        try {
            backgroundImage = ImageIO.read(new File("icon/cust_dash.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel bgPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        String customerName = fetchCustomerName(meterNo);
        double balance = fetchCustomerBalance(meterNo);

        JLabel title = new JLabel("Customer Dashboard", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 20, 600, 30);
        bgPanel.add(title);

        JLabel nameLabel = new JLabel("Welcome, " + customerName, JLabel.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(0, 60, 600, 25);
        bgPanel.add(nameLabel);

        JLabel meterLabel = new JLabel("Meter No: " + meterNo, JLabel.CENTER);
        meterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        meterLabel.setForeground(Color.WHITE);
        meterLabel.setBounds(0, 90, 600, 20);
        bgPanel.add(meterLabel);

        balanceLabel = new JLabel("Balance: ₹" + String.format("%.2f", balance), JLabel.CENTER);
        balanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setBounds(0, 115, 600, 20);
        bgPanel.add(balanceLabel);

        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 16);
        int btnWidth = 220;
        int btnHeight = 40;
        int leftX = 60;
        int rightX = 320;
        int startY = 160;
        int gap = 50;

        // Create buttons using custom RoundedButton class
        RoundedButton viewInfo = new RoundedButton("View Info");
        viewInfo.setFont(buttonFont);
        viewInfo.setBounds(leftX, startY, btnWidth, btnHeight);
        bgPanel.add(viewInfo);

        RoundedButton updateInfo = new RoundedButton("Update Info");
        updateInfo.setFont(buttonFont);
        updateInfo.setBounds(leftX, startY + gap, btnWidth, btnHeight);
        bgPanel.add(updateInfo);

        RoundedButton payBill = new RoundedButton("Pay Bill");
        payBill.setFont(buttonFont);
        payBill.setBounds(leftX, startY + 2 * gap, btnWidth, btnHeight);
        bgPanel.add(payBill);

        RoundedButton depositMoney = new RoundedButton("Deposit Money");
        depositMoney.setFont(buttonFont);
        depositMoney.setBounds(rightX, startY, btnWidth, btnHeight);
        bgPanel.add(depositMoney);

        RoundedButton notificationsBtn = new RoundedButton("Notifications");
        notificationsBtn.setFont(buttonFont);
        notificationsBtn.setBounds(rightX, startY + gap, btnWidth, btnHeight);
        bgPanel.add(notificationsBtn);

        RoundedButton supportBtn = new RoundedButton("Support / Feedback");
        supportBtn.setFont(buttonFont);
        supportBtn.setBounds(rightX, startY + 2 * gap, btnWidth, btnHeight);
        bgPanel.add(supportBtn);

        RoundedButton viewHistory = new RoundedButton("Bill History");
        viewHistory.setFont(buttonFont);
        viewHistory.setBounds((600 - btnWidth) / 2, startY + 3 * gap + 20, btnWidth, btnHeight);
        bgPanel.add(viewHistory);

        RoundedButton logout = new RoundedButton("Logout");
        logout.setFont(buttonFont);
        logout.setBounds((600 - btnWidth) / 2, startY + 4 * gap + 30, btnWidth, btnHeight);
        bgPanel.add(logout);

        // Actions
        viewInfo.addActionListener(e -> new ViewInfo(meterNo));
        updateInfo.addActionListener(e -> new UpdateInfo(meterNo));
        payBill.addActionListener(e -> new PayBill(meterNo));
        viewHistory.addActionListener(e -> new ViewHistory(meterNo));
        depositMoney.addActionListener(e -> {
            dispose();
            new DepositMoney(meterNo);
        });
        notificationsBtn.addActionListener(e -> new NotificationPanel(meterNo));
        supportBtn.addActionListener(e -> new SupportFeedback(meterNo));
        logout.addActionListener(e -> {
            new MainApp();
            this.dispose();
        });

        setVisible(true);
    }

    private String fetchCustomerName(String meterNo) {
        String name = "Customer";
        try (Connection con = DatabaseConnection.connect()) {
            String sql = "SELECT name FROM customers WHERE meter_no = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, meterNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching customer name: " + e.getMessage());
        }
        return name;
    }

    private double fetchCustomerBalance(String meterNo) {
        double balance = 0.0;
        try (Connection con = DatabaseConnection.connect()) {
            String sql = "SELECT balance FROM customers WHERE meter_no = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, meterNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                balance = rs.getDouble("balance");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching balance: " + e.getMessage());
        }
        return balance;
    }

    // Custom RoundedButton class with hover effect
    class RoundedButton extends JButton {
        private Color normalBg = Color.WHITE;
        private Color hoverBg = new Color(220, 220, 220); // light gray for hover
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

            if (hovered) {
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
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        new CustomerDashboard("123");
    }
}
