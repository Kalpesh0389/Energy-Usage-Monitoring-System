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
import java.text.SimpleDateFormat;
import java.util.Date;

public class PayBill extends JFrame {
    BufferedImage backgroundImage;

    public PayBill(String meterNo) {
        try {
            backgroundImage = ImageIO.read(new File("icon/p.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Pay Electricity Bill");
        setSize(620, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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

        JLabel title = new JLabel("Pending Monthly Bills", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 30, 620, 30);
        bgPanel.add(title);

        JLabel info = new JLabel("Meter No: " + meterNo, SwingConstants.CENTER);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        info.setForeground(Color.WHITE);
        info.setBounds(0, 70, 620, 25);
        bgPanel.add(info);

        JTextArea billArea = new JTextArea();
        billArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        billArea.setEditable(false);
        billArea.setOpaque(true);
        billArea.setForeground(Color.WHITE);
        billArea.setBackground(new Color(0, 0, 0, 100));

        JScrollPane scrollPane = new JScrollPane(billArea);
        scrollPane.setBounds(50, 110, 500, 280);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        bgPanel.add(scrollPane);

        RoundedButton payBtn = new RoundedButton("Pay All Unpaid Bills");
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        payBtn.setBounds(180, 410, 240, 40);
        bgPanel.add(payBtn);

        RoundedButton backBtn = new RoundedButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setBounds(250, 470, 100, 35);
        bgPanel.add(backBtn);

        final double[] totalAmount = {0};
        final double[] totalLateFee = {0};
        boolean hasBills = false;

        try (Connection con = DatabaseConnection.connect()) {
            String sql = "SELECT * FROM bills WHERE meter_no=? AND status='Unpaid' ORDER BY billing_month";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, meterNo);
            ResultSet rs = pst.executeQuery();

            StringBuilder sb = new StringBuilder();
            Date today = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            while (rs.next()) {
                hasBills = true;
                int billId = rs.getInt("bill_id");
                int units = rs.getInt("units");
                double amount = rs.getDouble("amount");
                String dueDateStr = rs.getString("due_date");
                String billingMonth = rs.getString("billing_month");
                Date dueDate = sdf.parse(dueDateStr);

                boolean isOverdue = today.after(dueDate);
                double lateFee = isOverdue ? 50.0 : 0.0;

                sb.append("Bill ID      : ").append(billId).append("\n")
                        .append("Billing Month: ").append(billingMonth).append("\n")
                        .append("Units Used   : ").append(units).append("\n")
                        .append("Amount       : ₹").append(amount).append("\n")
                        .append("Due Date     : ").append(dueDateStr).append(isOverdue ? " (Overdue)\n" : "\n")
                        .append("Late Charges : ₹").append(lateFee).append("\n")
                        .append("Total Pay    : ₹").append(amount + lateFee).append("\n")
                        .append("--------------------------------------------\n");

                totalAmount[0] += amount;
                totalLateFee[0] += lateFee;
            }

            if (hasBills) {
                sb.append("\nGrand Total: ₹").append(totalAmount[0] + totalLateFee[0]);
                billArea.setText(sb.toString());
            } else {
                billArea.setText("No unpaid bills found.");
                payBtn.setEnabled(false);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading bills: " + ex.getMessage());
        }

        payBtn.addActionListener(e -> {
            try (Connection con = DatabaseConnection.connect()) {
                con.setAutoCommit(false);
                double totalPay = totalAmount[0] + totalLateFee[0];

                // Deduct balance
                String updateBalanceSQL = "UPDATE customers SET balance = balance - ? WHERE meter_no = ?";
                PreparedStatement balancePst = con.prepareStatement(updateBalanceSQL);
                balancePst.setDouble(1, totalPay);
                balancePst.setString(2, meterNo);
                balancePst.executeUpdate();

                // Update unpaid bills
                String updateBillSQL = "UPDATE bills SET status='Paid', paid_date=NOW() WHERE meter_no=? AND status='Unpaid'";
                PreparedStatement pst = con.prepareStatement(updateBillSQL);
                pst.setString(1, meterNo);
                int updated = pst.executeUpdate();

                if (updated > 0) {
                    // 1. Add Payment Successful Notification
                    String notifySQL = "INSERT INTO notifications (meter_no, message) VALUES (?, ?)";
                    PreparedStatement notifyPst = con.prepareStatement(notifySQL);
                    notifyPst.setString(1, meterNo);
                    notifyPst.setString(2, "Payment of ₹" + totalPay + " successful. Late Fee: ₹" + totalLateFee[0]);
                    notifyPst.executeUpdate();

                    // 2. Low Balance Warning Notification
                    String balSQL = "SELECT balance FROM customers WHERE meter_no=?";
                    PreparedStatement balPst = con.prepareStatement(balSQL);
                    balPst.setString(1, meterNo);
                    ResultSet rs = balPst.executeQuery();
                    if (rs.next() && rs.getDouble("balance") < 100) {
                        PreparedStatement lowBalPst = con.prepareStatement(notifySQL);
                        lowBalPst.setString(1, meterNo);
                        lowBalPst.setString(2, "Warning: Your balance is low. Please deposit funds.");
                        lowBalPst.executeUpdate();
                    }

                    con.commit();
                    JOptionPane.showMessageDialog(this, "Payment Successful!\nLate Fee: ₹" + totalLateFee[0] + "\nTotal Paid: ₹" + totalPay);
                    new CustomerDashboard(meterNo);
                    dispose();
                } else {
                    con.rollback();
                    JOptionPane.showMessageDialog(this, "No bills to pay.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Payment Error: " + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> {
            new CustomerDashboard(meterNo);
            dispose();
        });

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
