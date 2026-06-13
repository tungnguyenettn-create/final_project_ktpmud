package org.example.final_project.database;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class UserHandling {
    public static boolean identityCardExists(String identityCard) {
        String query = "SELECT 1 FROM customer WHERE identity_card = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, identityCard);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();   // true if found, false otherwise
            }

        } catch (Exception e) {
            System.err.println("Identity card check error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
