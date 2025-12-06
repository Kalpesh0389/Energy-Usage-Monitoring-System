package admin;

import db.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ViewBillStatus extends JFrame {
    private final JFrame parent;

    public ViewBillStatus(JFrame parent) {
        this.parent = parent;
        parent.setVisible(false);

        setTitle("View Bill Status");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("All Bill Status", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Bill ID", "Meter No", "Units", "Amount (₹)", "Status", "Due Date", "Paid Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        buttonPanel.add(backBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> {
            dispose();
            parent.setVisible(true);
        });

        try (Connection con = DatabaseConnection.connect()) {
            String sql = "SELECT bill_id, meter_no, units, amount, status, due_date, paid_date FROM bills ORDER BY bill_id DESC";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("bill_id"),
                        rs.getString("meter_no"),
                        rs.getInt("units"),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        rs.getDate("due_date"),
                        rs.getString("paid_date") == null ? "N/A" : rs.getDate("paid_date").toString()
                };
                model.addRow(row);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch bill status from database.");
        }

        setVisible(true);
    }
}
