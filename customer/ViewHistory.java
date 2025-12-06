package customer;

import db.DatabaseConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;

public class ViewHistory extends JFrame {
    BufferedImage backgroundImage;

    public ViewHistory(String meterNo) {
        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("icon/h.jpg")); // ✅ Update path if needed
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Bill Payment History");
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

        JLabel title = new JLabel("Paid Bill History", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 20, 600, 30);
        bgPanel.add(title);

        // Text area with transparent scroll pane
        JTextArea historyArea = new JTextArea();
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        historyArea.setEditable(false);
        historyArea.setOpaque(false);
        historyArea.setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBounds(50, 70, 500, 370);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        // Semi-transparent panel behind the scroll
        JPanel textPanel = new JPanel();
        textPanel.setBounds(50, 70, 500, 370);
        textPanel.setLayout(new BorderLayout());
        textPanel.setBackground(new Color(255, 255, 255, 200)); // White with transparency
        textPanel.setOpaque(true);
        textPanel.add(scrollPane);
        bgPanel.add(textPanel);

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backBtn.setBounds(250, 460, 100, 35);
        bgPanel.add(backBtn);

        // Load paid bills
        try (Connection con = DatabaseConnection.connect()) {
            String sql = "SELECT * FROM bills WHERE meter_no=? AND status='Paid' ORDER BY paid_date DESC";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, meterNo);
            ResultSet rs = pst.executeQuery();

            StringBuilder sb = new StringBuilder();
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                sb.append("Bill ID       : ").append(rs.getInt("bill_id")).append("\n")
                        .append("Billing Month : ").append(rs.getString("billing_month")).append("\n")
                        .append("Units Used    : ").append(rs.getInt("units")).append("\n")
                        .append("Amount Paid   : ₹").append(rs.getDouble("amount")).append("\n")
                        .append("Paid Date     : ").append(rs.getString("paid_date")).append("\n")
                        .append("------------------------------------------\n");
            }

            historyArea.setText(hasData ? sb.toString() : "No paid bills found.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading history: " + ex.getMessage());
        }

        // Back button action
        backBtn.addActionListener(e -> {
            new CustomerDashboard(meterNo);
            dispose();
        });

        setVisible(true);
    }
}
