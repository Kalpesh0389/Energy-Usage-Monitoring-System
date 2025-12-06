package admin;

import db.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ViewSupportTickets extends JFrame {
    private final JFrame parent;

    public ViewSupportTickets(JFrame parent) {
        this.parent = parent;
        parent.setVisible(false);

        setTitle("Support Tickets");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Customer Support Tickets", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Ticket ID", "Meter No", "Subject", "Message", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        try (Connection con = DatabaseConnection.connect()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, meter_no, subject, message, created_at FROM support_tickets ORDER BY created_at DESC");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("meter_no"),
                        rs.getString("subject"),
                        rs.getString("message"),
                        rs.getTimestamp("created_at")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load tickets: " + e.getMessage());
        }

        JPanel bottomPanel = new JPanel();
        JButton backBtn = new JButton("Back");
        backBtn.setPreferredSize(new Dimension(100, 30));
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> {
            dispose();
            parent.setVisible(true);
        });

        setVisible(true);
    }
}
