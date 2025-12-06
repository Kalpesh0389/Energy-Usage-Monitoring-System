package admin;

import db.DatabaseConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenerateBill extends JFrame {
    private final JFrame parent;
    BufferedImage backgroundImage;

    public GenerateBill(JFrame parent) {
        this.parent = parent;
        parent.setVisible(false);

        try {
            backgroundImage = ImageIO.read(new File("icon/gen_bill.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Generate Bill");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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

        JLabel title = new JLabel("Generate Customer Bill", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setBounds(200, 20, 300, 40);
        backgroundPanel.add(title);

        JLabel meterLabel = new JLabel("Meter No:");
        meterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        meterLabel.setForeground(Color.WHITE);
        meterLabel.setBounds(150, 80, 120, 30);
        backgroundPanel.add(meterLabel);

        JTextField meterField = new JTextField();
        meterField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        meterField.setBounds(280, 80, 220, 30);
        backgroundPanel.add(meterField);

        JLabel unitsLabel = new JLabel("Units Consumed:");
        unitsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        unitsLabel.setForeground(Color.WHITE);
        unitsLabel.setBounds(150, 130, 130, 30);
        backgroundPanel.add(unitsLabel);

        JTextField unitsField = new JTextField();
        unitsField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        unitsField.setBounds(280, 130, 220, 30);
        backgroundPanel.add(unitsField);

        JLabel monthLabel = new JLabel("Billing Month:");
        monthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        monthLabel.setForeground(Color.WHITE);
        monthLabel.setBounds(150, 180, 120, 30);
        backgroundPanel.add(monthLabel);

        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        JComboBox<String> monthCombo = new JComboBox<>(months);
        monthCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        monthCombo.setBounds(280, 180, 220, 30);
        backgroundPanel.add(monthCombo);

        JLabel dueLabel = new JLabel("Due Date:");
        dueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dueLabel.setForeground(Color.WHITE);
        dueLabel.setBounds(150, 230, 120, 30);
        backgroundPanel.add(dueLabel);

        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner dueSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dueSpinner, "yyyy-MM-dd");
        dueSpinner.setEditor(dateEditor);
        dueSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dueSpinner.setBounds(280, 230, 220, 30);
        backgroundPanel.add(dueSpinner);

        JButton calculateBtn = new JButton("Calculate Bill");
        calculateBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        calculateBtn.setBounds(130, 290, 140, 35);
        backgroundPanel.add(calculateBtn);

        JButton generateBtn = new JButton("Generate");
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        generateBtn.setBounds(290, 290, 120, 35);
        backgroundPanel.add(generateBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backBtn.setBounds(430, 290, 100, 35);
        backgroundPanel.add(backBtn);

        calculateBtn.addActionListener(e -> {
            try {
                int units = Integer.parseInt(unitsField.getText().trim());
                double amount = units * 7.5;
                JOptionPane.showMessageDialog(this,
                        "Estimated Bill Amount for " + monthCombo.getSelectedItem() + ": ₹" + amount,
                        "Calculated Amount", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid units.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        generateBtn.addActionListener(e -> {
            try (Connection con = DatabaseConnection.connect()) {
                int units = Integer.parseInt(unitsField.getText().trim());
                double amount = units * 7.5;
                String dueDate = new SimpleDateFormat("yyyy-MM-dd").format((Date) dueSpinner.getValue());
                String billingMonth = (String) monthCombo.getSelectedItem();

                String sql = "INSERT INTO bills (meter_no, units, amount, status, due_date, billing_month) VALUES (?, ?, ?, 'Unpaid', ?, ?)";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, meterField.getText().trim());
                pst.setInt(2, units);
                pst.setDouble(3, amount);
                pst.setString(4, dueDate);
                pst.setString(5, billingMonth);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Bill Generated for " + billingMonth + " Successfully!");

                meterField.setText("");
                unitsField.setText("");
                monthCombo.setSelectedIndex(0);
                dueSpinner.setValue(new Date());

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid number of units.", "Input Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to generate bill.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            parent.setVisible(true);
        });

        setVisible(true);
    }
}
