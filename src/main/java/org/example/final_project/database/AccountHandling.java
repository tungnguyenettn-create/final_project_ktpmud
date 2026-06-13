package org.example.final_project.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class AccountHandling {

    // 1. Fixed camelCase naming convention
    public static Integer login(String account, String password) {
        // 2. Fixed naming discrepancy: "password" -> "account_password"
        String query = "SELECT account_password FROM account WHERE account_id = ? AND account_status = 'open'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, account);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return 0; // Wrong account (Account does not exist)
                }

                String correctedPassword = rs.getString("account_password");

                if (!correctedPassword.equals(password)) {
                    return 1; // Wrong password
                }

                return 2; // Success! Both account and password match
            }

        } catch (Exception e) {
            System.err.println("Login system error: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public static BigDecimal getBalance(String account) {
        String query = "SELECT balance FROM account WHERE account_id = ? AND account_status='open'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, account);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal balance = rs.getBigDecimal("balance");
                    return (balance != null) ? balance : BigDecimal.ZERO;
                }
            }
            return BigDecimal.ZERO;

        } catch (SQLException e) {
            System.err.println("Database error while fetching balance for account: " + account);
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    public static List<String> getAccountFromUser(String identityCard) {
        String query1 = "SELECT customer_id FROM customer WHERE identity_card = ?";
        String query2 = "SELECT account_id FROM account WHERE customer_id = ? AND account_status = 'open'";

        List<String> accountList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(query1)) {

            stmt1.setString(1, identityCard);

            try (ResultSet rs1 = stmt1.executeQuery()) {
                if (!rs1.next()) {
                    // Return the empty list if no customer is found
                    return accountList;
                }
                int customerId = rs1.getInt("customer_id");

                try (PreparedStatement stmt2 = conn.prepareStatement(query2)) {
                    stmt2.setInt(1, customerId);

                    try (ResultSet rs2 = stmt2.executeQuery()) {
                        while (rs2.next()) {
                            accountList.add(rs2.getString("account_id"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return accountList;
    }

    public static Map<String, Object> getUserFromAccount(String accountId) {
        // Step 1: Query to get customer_id from the account table
        String query1 = "SELECT customer_id FROM account WHERE account_status = 'open' AND account_id = ?";
        // Step 2: Query to get all user details from the customer table
        String query2 = "SELECT customer_id, branch_id, full_name, identity_card, nationality, dob, city, address " +
                "FROM customer WHERE customer_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(query1)) {

            stmt1.setString(1, accountId);

            try (ResultSet rs1 = stmt1.executeQuery()) {
                // If the account doesn't exist, return null early
                if (!rs1.next()) {
                    System.out.println("No account found with ID: " + accountId);
                    return null;
                }

                int customerId = rs1.getInt("customer_id");

                // Execute the second query using the retrieved customerId
                try (PreparedStatement stmt2 = conn.prepareStatement(query2)) {
                    stmt2.setInt(1, customerId);

                    try (ResultSet rs2 = stmt2.executeQuery()) {
                        if (rs2.next()) {
                            // Map the data to a structured Map matching your schema types
                            Map<String, Object> customerData = new HashMap<>();
                            customerData.put("customer_id", rs2.getInt("customer_id"));
                            customerData.put("branch_id", rs2.getInt("branch_id"));
                            customerData.put("full_name", rs2.getString("full_name"));
                            customerData.put("identity_card", rs2.getString("identity_card"));
                            customerData.put("nationality", rs2.getString("nationality"));
                            customerData.put("dob", rs2.getDate("dob"));
                            customerData.put("city", rs2.getString("city"));
                            customerData.put("address", rs2.getString("address"));

                            return customerData;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error while fetching user from account: " + accountId);
            e.printStackTrace();
        }

        return null; // Return null if customer record wasn't found or an error occurred
    }
    // Refactored to return a Map because returning a closed ResultSet causes errors.
    // Also corrected lowercase 'Null' to 'null'
    public static Map<String, Object> getMetadata(String account) {
        String query = "SELECT balance, open_date FROM account WHERE account_id = ? AND account_status='open'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, account);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> metadataMap = new HashMap<>();
                    metadataMap.put("balance", rs.getBigDecimal("balance"));
                    metadataMap.put("open_date", rs.getDate("open_date"));
                    return metadataMap;
                }
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Database error while fetching metadata for account: " + account);
            e.printStackTrace();
            return null;
        }
    }

    public static Integer updatePassword(String account, String new_password) {
        // SQL query to update the password for an active account
        String query = "UPDATE account SET account_password = ? WHERE account_id = ? AND account_status = 'open'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Bind parameters safely to prevent SQL injection
            stmt.setString(1, new_password);
            stmt.setString(2, account);

            // executeUpdate returns the number of rows affected
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return 1;  // 1 -> Finished successfully
            } else {
                return 0;  // 0 -> No existing (or open) account found
            }

        } catch (SQLException e) {
            System.err.println("Database error while updating password for account: " + account);
            e.printStackTrace();
            return -1;     // -1 -> Some error occurred
        }
    }
    // 5. Implemented main function for testing your exact dataset
    public static void main(String[] args) {
        System.out.println("=== RUNNING ACCOUNT HANDLING DB TESTS ===\n");

        // Test 1: Login functionality
        System.out.println("--- Test 1: Login ---");
        int loginResult1 = login("0012060351", "thai"); // Should be 2 (Success)
        int loginResult2 = login("0012060351", "wrong_pass"); // Should be 1 (Wrong password)
        int loginResult3 = login("9999999999", "thai"); // Should be 0 (No account)

        System.out.println("Valid Login Result (Expect 2): " + loginResult1);
        System.out.println("Invalid Password Result (Expect 1): " + loginResult2);
        System.out.println("Invalid Account Result (Expect 0): " + loginResult3);
        System.out.println();

        // Test 2: Get Balance
        System.out.println("--- Test 2: Get Balance ---");
        BigDecimal balance1 = getBalance("0012060350"); // Should be 500000.000
        BigDecimal balance2 = getBalance("0012060330"); // Should be 350000.000

        System.out.println("Balance for 0012060350 (Expect 500000.00): " + balance1);
        System.out.println("Balance for 0012060330 (Expect 350000.00): " + balance2);
        System.out.println();

        // Test 3: Get Metadata
        System.out.println("--- Test 3: Get Metadata ---");
        Map<String, Object> meta = getMetadata("0012060351");
        if (meta != null) {
            System.out.println("Account 0012060351 Metadata:");
            System.out.println(" > Balance: " + meta.get("balance"));
            System.out.println(" > Open Date: " + meta.get("open_date"));
        } else {
            System.out.println("Metadata not found.");
        }
        System.out.println();

        // Test 4: Get Accounts from User Identity Card
        // (Assuming customer_id 2 has identity_card "ID-THAI-123" inside your 'customer' table)
        System.out.println("--- Test 4: Get Accounts from User ---");
        String[] accountList = new String[5];
        // Note: Replace "ID-THAI-123" with an actual identity card value from your database
        List<String> accountList1 = getAccountFromUser("ID-THAI-123");
        Integer totalAccounts = accountList1.size();
        System.out.println("Total accounts found for user: " + totalAccounts);
        for (int i = 0; i < totalAccounts; i++) {
            System.out.println(" > Account " + (i + 1) + ": " + accountList[i]);

        }
        System.out.println("--- Test: Get User From Account ---");

        // Testing with one of your existing account IDs
        Map<String, Object> customer = getUserFromAccount("0012060351");

        if (customer != null) {
            System.out.println("Customer found for account 0012060350:");
            System.out.println(" > ID: " + customer.get("customer_id"));
            System.out.println(" > Name: " + customer.get("full_name"));
            System.out.println(" > Identity Card: " + customer.get("identity_card"));
            System.out.println(" > DOB: " + customer.get("dob"));
            System.out.println(" > City: " + customer.get("city"));
        } else {
            System.out.println("No customer data found for this account.");
        }
    }
}