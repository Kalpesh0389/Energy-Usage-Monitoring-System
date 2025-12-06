package utils;

import db.DatabaseConnection;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationScheduler {

    public static void checkAndSendNotifications() {
        try (Connection con = DatabaseConnection.connect()) {
            // 1. Bill Due Reminder
            String dueReminderSQL = "SELECT meter_no, due_date FROM bills WHERE paid = 0";
            PreparedStatement ps1 = con.prepareStatement(dueReminderSQL);
            ResultSet rs1 = ps1.executeQuery();

            Date today = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            while (rs1.next()) {
                String meter = rs1.getString("meter_no");
                Date dueDate = rs1.getDate("due_date");

                long daysLeft = (dueDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24);
                if (daysLeft <= 3 && daysLeft >= 0) {
                    insertNotification(con, meter, "⚠️ Your bill is due in " + daysLeft + " day(s). Please pay on time.");
                }
            }

            // 2. Low Balance Warning
            String balanceSQL = "SELECT meter_no, balance FROM customers";
            PreparedStatement ps2 = con.prepareStatement(balanceSQL);
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
                String meter = rs2.getString("meter_no");
                double balance = rs2.getDouble("balance");

                if (balance < 100) {
                    insertNotification(con, meter, "⚠️ Low balance warning! Your current balance is ₹" + balance);
                }
            }

        } catch (Exception e) {
            System.out.println("Error in NotificationScheduler: " + e.getMessage());
        }
    }

    private static void insertNotification(Connection con, String meterNo, String message) {
        try {
            String checkSQL = "SELECT COUNT(*) FROM notifications WHERE meter_no=? AND message=? AND DATE(timestamp)=CURDATE()";
            PreparedStatement checkStmt = con.prepareStatement(checkSQL);
            checkStmt.setString(1, meterNo);
            checkStmt.setString(2, message);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSQL = "INSERT INTO notifications (meter_no, message) VALUES (?, ?)";
                PreparedStatement ps = con.prepareStatement(insertSQL);
                ps.setString(1, meterNo);
                ps.setString(2, message);
                ps.executeUpdate();
            }
        } catch (Exception ex) {
            System.out.println("Insert notification failed: " + ex.getMessage());
        }
    }
}
