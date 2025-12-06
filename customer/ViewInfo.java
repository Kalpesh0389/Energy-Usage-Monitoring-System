package customer;

import db.DatabaseConnection;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;

public class ViewInfo extends JFrame {
    BufferedImage backgroundImage;

    public ViewInfo(String meterNo) {
        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("icon/v1.jpg")); // ✅ Replace with your image path
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Customer Information");
        setSize(600, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create background panel
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

        // Top Header
        JLabel header = new JLabel("Customer Information", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false); // Transparent panel
        headerPanel.add(header, BorderLayout.CENTER);

        // Main form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); // Transparent
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels & Values
        String[] fieldNames = {"Name", "City", "State", "Email", "Address", "Meter No"};
        JLabel[] fieldLabels = new JLabel[fieldNames.length];
        JLabel[] fieldValues = new JLabel[fieldNames.length];

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 16);

        for (int i = 0; i < fieldNames.length; i++) {
            fieldLabels[i] = new JLabel(fieldNames[i] + ":");
            fieldLabels[i].setFont(labelFont);
            fieldLabels[i].setForeground(Color.WHITE);

            fieldValues[i] = new JLabel("");
            fieldValues[i].setFont(valueFont);
            fieldValues[i].setForeground(Color.WHITE);

            gbc.gridx = 0;
            gbc.gridy = i;
            formPanel.add(fieldLabels[i], gbc);

            gbc.gridx = 1;
            formPanel.add(fieldValues[i], gbc);
        }

        // Back Button
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backBtn.setBackground(new Color(220, 220, 220));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        bottomPanel.add(backBtn);

        // Add panels to bgPanel
        bgPanel.add(headerPanel, BorderLayout.NORTH);
        bgPanel.add(formPanel, BorderLayout.CENTER);
        bgPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Fetch and fill customer data
        try (Connection con = DatabaseConnection.connect()) {
            String query = "SELECT * FROM customers WHERE meter_no = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, meterNo);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                fieldValues[0].setText(rs.getString("name"));
                fieldValues[1].setText(rs.getString("city"));
                fieldValues[2].setText(rs.getString("state"));
                fieldValues[3].setText(rs.getString("email"));
                fieldValues[4].setText(rs.getString("address"));
                fieldValues[5].setText(rs.getString("meter_no"));
            } else {
                JOptionPane.showMessageDialog(this, "Customer not found!");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }

        setVisible(true);
    }
}
