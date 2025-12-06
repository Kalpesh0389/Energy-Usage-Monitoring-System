package auth;

import db.DatabaseConnection;
import customer.CustomerDashboard;
import main.MainApp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;

public class CustomerLogin extends JFrame {
    private JTextField meterField;
    private JPasswordField passwordField;
    private BufferedImage backgroundImage;

    public CustomerLogin() {
        setTitle("Customer Login - Electricity Billing System");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("icon/cust_log.jpg")); // Change path/image as needed
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Custom panel with background
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

        JLabel title = new JLabel("Customer Login", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBounds(100, 30, 400, 40);
        backgroundPanel.add(title);

        JLabel lblMeter = new JLabel("Meter No:");
        lblMeter.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMeter.setForeground(Color.WHITE);
        lblMeter.setBounds(130, 100, 100, 30);
        backgroundPanel.add(lblMeter);

        meterField = new JTextField();
        meterField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        meterField.setBounds(240, 100, 220, 30);
        backgroundPanel.add(meterField);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblPass.setForeground(Color.WHITE);
        lblPass.setBounds(130, 150, 100, 30);
        backgroundPanel.add(lblPass);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBounds(240, 150, 220, 30);
        backgroundPanel.add(passwordField);

        // Login Button
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setBounds(240, 200, 100, 35);
        loginBtn.setBackground(Color.WHITE);           // White background
        loginBtn.setForeground(Color.BLACK);           // Black text
        loginBtn.setFocusPainted(false);               // No focus border
        backgroundPanel.add(loginBtn);

        // Back Button
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setBounds(360, 200, 100, 35);
        backBtn.setBackground(Color.WHITE);            // White background
        backBtn.setForeground(Color.BLACK);            // Black text
        backBtn.setFocusPainted(false);                // No focus border
        backgroundPanel.add(backBtn);

        loginBtn.addActionListener(e -> {
            try (Connection con = DatabaseConnection.connect()) {
                String sql = "SELECT * FROM customers WHERE meter_no = ? AND password = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, meterField.getText());
                pst.setString(2, new String(passwordField.getPassword()));
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login successful");
                    new CustomerDashboard(meterField.getText()).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid meter number or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> {
            this.dispose();
            new MainApp();
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}
        new CustomerLogin();
    }
}
