package customer;

import db.DatabaseConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;

public class DepositMoney extends JFrame {
    BufferedImage backgroundImage;

    public DepositMoney(String meterNo) {
        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("icon/d.jpeg")); // ✅ Change path if needed
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Deposit Money");
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
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        JLabel title = new JLabel("Deposit Money", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 30, 600, 30);
        bgPanel.add(title);

        JLabel label = new JLabel("Enter Amount to Deposit:", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(Color.WHITE);
        label.setBounds(0, 100, 600, 30);
        bgPanel.add(label);

        JTextField amountField = new JTextField();
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        amountField.setBounds(180, 150, 240, 35);
        bgPanel.add(amountField);

        JButton depositBtn = new JButton("Deposit");
        depositBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        depositBtn.setBackground(new Color(0, 123, 255));
        depositBtn.setForeground(Color.WHITE);
        depositBtn.setBounds(180, 210, 240, 40);
        bgPanel.add(depositBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setBounds(250, 270, 100, 35);
        bgPanel.add(backBtn);

        // 💰 Deposit Button Logic
        depositBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than 0.");
                    return;
                }

                try (Connection con = DatabaseConnection.connect()) {
                    String sql = "UPDATE customers SET balance = balance + ? WHERE meter_no = ?";
                    PreparedStatement pst = con.prepareStatement(sql);
                    pst.setDouble(1, amount);
                    pst.setString(2, meterNo);

                    int result = pst.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, "Amount Deposited Successfully!");
                        amountField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this, "Deposit Failed! Invalid Meter Number.");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // 🔙 Back Button Logic
        backBtn.addActionListener(e -> {
            new CustomerDashboard(meterNo);
            dispose();
        });

        setVisible(true);
    }
}
