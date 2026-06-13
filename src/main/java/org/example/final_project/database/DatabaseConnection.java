package org.example.final_project.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static final String url = "jdbc:postgresql://localhost:5432/BANK";
    private static final String user = "postgres";
    private static final String password = "ubuntu8s9reat";

    public static Connection getConnection(){
        try {
            // Standard assignment (No try-with-resources here!)
            Connection conn = DriverManager.getConnection(url, user, password);
            if (conn != null) {
                return conn;
            } else {
                System.err.println("Connection is null, Please check again");
            }
        } catch (Exception e) {
            System.err.println("Failed to establish database connection:");
            e.printStackTrace();
        }
        return null;
    }
}