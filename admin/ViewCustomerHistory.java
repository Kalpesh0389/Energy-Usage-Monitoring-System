package admin;

import db.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewCustomerHistory extends JFrame {
    private final JFrame parent;

    public ViewCustomerHistory(JFrame parent, String meterNo) {
        this.parent = parent;
        parent.setVisible(false);

        setTitle("Customer Bill History - Meter No: " + meterNo);
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Bill History for Meter No: " + meterNo, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Bill ID", "Units", "Amount (₹)", "Due Date", "Status", "Paid Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(26);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> {
            dispose();
            parent.setVisible(true);
        });

        try (Connection con = DatabaseConnection.connect()) {
            String sql = "SELECT bill_id, units, amount, due_date, status, paid_date FROM bills WHERE meter_no = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, meterNo);
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                Object[] row = {
                        rs.getInt("bill_id"),
                        rs.getInt("units"),
                        rs.getDouble("amount"),
                        rs.getDate("due_date"),
                        rs.getString("status"),
                        rs.getString("paid_date") == null ? "N/A" : rs.getDate("paid_date").toString()
                };
                model.addRow(row);
            }
            if (!hasData) {
                model.addRow(new Object[]{"-", "-", "-", "-", "-", "No billing history found"});
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bill history.");
        }

        setVisible(true);
    }
}
