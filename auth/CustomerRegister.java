package auth;

import db.DatabaseConnection;
import main.MainApp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;

public class CustomerRegister extends JFrame {
    BufferedImage backgroundImage;

    public CustomerRegister() {
        setTitle("Customer Registration");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("icon/c2.jpg")); // Replace with your image path
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

        JLabel lblTitle = new JLabel("New Customer Registration", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(100, 20, 400, 40);
        backgroundPanel.add(lblTitle);

        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(80, 80, 100, 30);
        lblName.setForeground(Color.WHITE);
        backgroundPanel.add(lblName);
        JTextField nameField = new JTextField();
        nameField.setBounds(200, 80, 280, 30);
        backgroundPanel.add(nameField);

        JLabel lblMeter = new JLabel("Meter No:");
        lblMeter.setBounds(80, 120, 100, 30);
        lblMeter.setForeground(Color.WHITE);
        backgroundPanel.add(lblMeter);
        JTextField meterField = new JTextField();
        meterField.setBounds(200, 120, 280, 30);
        backgroundPanel.add(meterField);

        JLabel lblState = new JLabel("State:");
        lblState.setBounds(80, 160, 100, 30);
        lblState.setForeground(Color.WHITE);
        backgroundPanel.add(lblState);
        String[] states = {
                "Maharashtra", "Gujarat", "Karnataka", "Tamil Nadu", "Kerala",
                "Delhi", "Rajasthan", "Uttar Pradesh", "Madhya Pradesh", "West Bengal"
        };
        JComboBox<String> stateBox = new JComboBox<>(states);
        stateBox.setBounds(200, 160, 280, 30);
        backgroundPanel.add(stateBox);

        JLabel lblCity = new JLabel("City:");
        lblCity.setBounds(80, 200, 100, 30);
        lblCity.setForeground(Color.WHITE);
        backgroundPanel.add(lblCity);
        JTextField cityField = new JTextField();
        cityField.setBounds(200, 200, 280, 30);
        backgroundPanel.add(cityField);

        JLabel lblAddress = new JLabel("Address:");
        lblAddress.setBounds(80, 240, 100, 30);
        lblAddress.setForeground(Color.WHITE);
        backgroundPanel.add(lblAddress);
        JTextField addressField = new JTextField();
        addressField.setBounds(200, 240, 280, 30);
        backgroundPanel.add(addressField);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(80, 280, 100, 30);
        lblEmail.setForeground(Color.WHITE);
        backgroundPanel.add(lblEmail);
        JTextField emailField = new JTextField();
        emailField.setBounds(200, 280, 280, 30);
        backgroundPanel.add(emailField);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(80, 320, 100, 30);
        lblPass.setForeground(Color.WHITE);
        backgroundPanel.add(lblPass);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(200, 320, 280, 30);
        backgroundPanel.add(passwordField);

        JLabel lblConfirm = new JLabel("Confirm Password:");
        lblConfirm.setBounds(80, 360, 120, 30);
        lblConfirm.setForeground(Color.WHITE);
        backgroundPanel.add(lblConfirm);
        JPasswordField confirmField = new JPasswordField();
        confirmField.setBounds(200, 360, 280, 30);
        backgroundPanel.add(confirmField);

        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setBounds(200, 410, 120, 35);
        registerBtn.setBackground(Color.WHITE);       // White background
        registerBtn.setForeground(Color.BLACK);       // Black text
        registerBtn.setFocusPainted(false);           // No focus border
        backgroundPanel.add(registerBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setBounds(360, 410, 120, 35);
        backBtn.setBackground(Color.WHITE);           // White background
        backBtn.setForeground(Color.BLACK);           // Black text
        backBtn.setFocusPainted(false);               // No focus border
        backgroundPanel.add(backBtn);

        registerBtn.addActionListener(e -> {
            String pass = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection con = DatabaseConnection.connect()) {
                String sql = "INSERT INTO customers (name, meter_no, state, city, address, email, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, nameField.getText());
                pst.setString(2, meterField.getText());
                pst.setString(3, stateBox.getSelectedItem().toString());
                pst.setString(4, cityField.getText());
                pst.setString(5, addressField.getText());
                pst.setString(6, emailField.getText());
                pst.setString(7, pass);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Registration Successful!");
                this.dispose();
                new CustomerLogin();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error during registration", "Error", JOptionPane.ERROR_MESSAGE);
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
        new CustomerRegister();
    }
}
