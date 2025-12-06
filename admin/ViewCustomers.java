package admin;

import db.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewCustomers extends JFrame {
    private final JFrame parentRef; // reference to AdminDashboard

    public ViewCustomers(JFrame parent) {
        this.parentRef = parent;
        parent.setVisible(false); // hide AdminDashboard

        setTitle("View All Customers");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel titleLabel = new JLabel("All Registered Customers", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Table and ScrollPane
        String[] columns = {"Meter No", "Name", "City", "Email"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Back Button Panel
        JPanel bottomPanel = new JPanel();
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load Data from Database
        try (Connection con = DatabaseConnection.connect()) {
            String sql = "SELECT meter_no, name, city, email FROM customers ORDER BY meter_no";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Object[] row = {
                        rs.getString("meter_no"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getString("email")
                };
                tableModel.addRow(row);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load customer data.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Back Button Action
        backBtn.addActionListener(e -> {
            dispose();              // close current window
            parentRef.setVisible(true);  // show AdminDashboard again
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame dummyParent = new JFrame(); // used only for testing this class
            dummyParent.setVisible(false);
            new ViewCustomers(dummyParent);
        });
    }
}
