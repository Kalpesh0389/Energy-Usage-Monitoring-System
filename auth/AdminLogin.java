package auth;

import db.DatabaseConnection;
import main.MainApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;

import javax.imageio.ImageIO;

public class AdminLogin extends JFrame {
    BufferedImage backgroundImage;

    public AdminLogin() {
        setTitle("Admin Login - Electricity Billing System");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("icon/ad4.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Background Panel
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
        backgroundPanel.setBounds(0, 0, 600, 400);
        setContentPane(backgroundPanel);

        JLabel lblTitle = new JLabel("Admin Login", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(150, 30, 300, 40);
        backgroundPanel.add(lblTitle);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblUser.setForeground(Color.WHITE);
        lblUser.setBounds(140, 100, 100, 30);
        backgroundPanel.add(lblUser);

        JTextField userField = new JTextField();
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userField.setBounds(260, 100, 200, 30);
        backgroundPanel.add(userField);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblPass.setForeground(Color.WHITE);
        lblPass.setBounds(140, 150, 100, 30);
        backgroundPanel.add(lblPass);

        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setBounds(260, 150, 200, 30);
        backgroundPanel.add(passField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setBounds(230, 200, 100, 35);
        loginBtn.setBackground(Color.WHITE);             // Set white background
        loginBtn.setForeground(Color.BLACK);             // Set black text
        loginBtn.setFocusPainted(false);
        backgroundPanel.add(loginBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setBounds(340, 200, 100, 35);
        backBtn.setBackground(Color.WHITE);              // Set white background
        backBtn.setForeground(Color.BLACK);              // Set black text
        backBtn.setFocusPainted(false);
        backgroundPanel.add(backBtn);

        loginBtn.addActionListener((ActionEvent e) -> {
            try (Connection con = DatabaseConnection.connect()) {
                String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, userField.getText());
                pst.setString(2, new String(passField.getPassword()));
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Admin Login Successful");
                    new admin.AdminDashboard().setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database connection error", "Error", JOptionPane.ERROR_MESSAGE);
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
        new AdminLogin();
    }
}
