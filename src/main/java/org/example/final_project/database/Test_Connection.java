package org.example.final_project.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Test_Connection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/BANK";
        String user = "postgres";
        String password = "ubuntu8s9reat";

        try (Connection conn = DriverManager.getConnection(url, user, password);
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT version();")) {
            if (conn != null) {
                System.out.println("Successfully connected to the database!");
                if (resultSet.next()) {
                    System.out.println("Database Version: "+ resultSet.getString(1));
                }
            } else {
                System.out.println("Failed to make connection");
            }
        }
        catch (Exception e) {
            System.err.println("Connection failed!");
            e.printStackTrace();
        }
    }
}
