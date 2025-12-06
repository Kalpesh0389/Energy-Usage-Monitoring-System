// src/db/DatabaseConnection.java
package db;
import java.sql.*;

public class DatabaseConnection {
    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/DB_EBS", "root", "PHW#84#jeor"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
