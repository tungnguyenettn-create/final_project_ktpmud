package org.example.final_project.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransferHandling {

    /**
     * Lấy danh sách ngân hàng hỗ trợ (Hàm có sẵn của bạn)
     */
    public static List<String> get_supported_bank() {
        List<String> supportedBanks = new ArrayList<>();
        String query = "SELECT out_bank_branch FROM out_bank_supported;";
        supportedBanks.add("36 BANK");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String bankBranch = rs.getString("out_bank_branch");
                supportedBanks.add(bankBranch);
            }

        } catch (Exception e) {
            System.err.println("Something went wrong while fetching supported banks:");
            e.printStackTrace();
        }
        return supportedBanks;
    }

    /**
     * 1. Chuyển khoản nội bộ (In-bank Transfer)
     * Trả về mã số kết quả: 1 = Thành công, các số còn lại là lỗi cụ thể.
     */
    public static int inBankTransfer(String sourceAccountId, String destAccountId, BigDecimal amount, String description) {
        String query = "SELECT fn_in_bank_transfer(?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, sourceAccountId);
            stmt.setString(2, destAccountId);
            stmt.setBigDecimal(3, amount);
            stmt.setString(4, description);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing inBankTransfer:");
            e.printStackTrace();
        }
        return -99; // Lỗi kết nối hệ thống
    }

    /**
     * 2. Chuyển khoản liên ngân hàng (Out-bank Transfer)
     */
    public static int outBankTransfer(String sourceAccount, String destBankId, BigDecimal amount, String description) {
        String query = "SELECT fn_out_bank_transaction(?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, sourceAccount);
            stmt.setString(2, destBankId);
            stmt.setBigDecimal(3, amount);
            stmt.setString(4, description);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing outBankTransfer:");
            e.printStackTrace();
        }
        return -99;
    }

    /**
     * 3. Thanh toán hóa đơn (Pay Bill)
     */
    public static int payBill(String sourceAccount, int billProviderId, BigDecimal amount) {
        String query = "SELECT fn_pay_bill(?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, sourceAccount);
            stmt.setInt(2, billProviderId);
            stmt.setBigDecimal(3, amount);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing payBill:");
            e.printStackTrace();
        }
        return -99;
    }

    /**
     * 4. Rút tiền mặt tại quầy (Withdraw Cash)
     */
    public static int withdrawCash(String accountId, int employeeId, BigDecimal amount, String description) {
        String query = "SELECT fn_withdraw_cash(?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, accountId);
            stmt.setInt(2, employeeId);
            stmt.setBigDecimal(3, amount);
            stmt.setString(4, description);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing withdrawCash:");
            e.printStackTrace();
        }
        return -99;
    }

    /**
     * 5. Nộp tiền mặt tại quầy (Deposit Cash)
     */
    public static int depositCash(String accountId, int employeeId, BigDecimal amount, String description) {
        String query = "SELECT fn_deposit_cash(?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, accountId);
            stmt.setInt(2, employeeId);
            stmt.setBigDecimal(3, amount);
            stmt.setString(4, description);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing depositCash:");
            e.printStackTrace();
        }
        return -99;
    }

    public static String getBankIdFromBankName(String bankName) {
        String query = "SELECT out_bank_id FROM out_bank_supported WHERE out_bank_branch = ?";
        String bankId = null; // Default to null if no match is found

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Bind the parameter to the '?' placeholder
            stmt.setString(1, bankName);

            try (ResultSet rs = stmt.executeQuery()) {
                // Since we expect at most one result, 'if' is used instead of 'while'
                if (rs.next()) {
                    // Use getObject or check for null if the ID can be null in the DB,
                    // but getInt wrapped in Integer works for standard IDs.
                    bankId = rs.getString("out_bank_id");

                    // Handle case where the database value itself is SQL NULL
                    if (rs.wasNull()) {
                        bankId = null;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Something went wrong while fetching bank ID for: " + bankName);
            e.printStackTrace();
        }

        return bankId;
    }
    /**
     * Hàm helper hỗ trợ dịch mã kết quả từ Database thành câu thông báo dễ hiểu
     */
    public static String getResultMessage(int code) {
        return switch (code) {
            case 1 -> "Giao dịch hoàn tất thành công!";
            case 0 -> "Thất bại: Tài khoản nguồn và đích không được trùng nhau.";
            case -1 -> "Thất bại: Số tiền giao dịch phải lớn hơn 0.";
            case 2 -> "Thất bại: Tài khoản nguồn không tồn tại hoặc đã bị đóng.";
            case 3 -> "Thất bại: Tài khoản đích không tồn tại hoặc đã bị đóng.";
            case 4 -> "Thất bại: Tài khoản không đủ số dư.";
            case 5 -> "Thất bại: Ngân hàng đích liên kết không được hỗ trợ.";
            case 6 -> "Thất bại: Nhà cung cấp dịch vụ hóa đơn không tồn tại.";
            case 7 -> "Thất bại: Nhân viên thực hiện không tồn tại hoặc đã dừng hoạt động.";
            case -99 -> "Thất bại: Lỗi kết nối hệ thống dữ liệu đột xuất.";
            default -> "Thất bại: Mã lỗi không xác định (" + code + ").";
        };
    }

    public static void main(String[] args) {
        System.out.println("--- TESTING BANKING TRANSACTIONS ---");

        // 1. Test thử tính năng lấy danh sách ngân hàng
        List<String> banks = get_supported_bank();
        System.out.println("Supported banks size: " + banks.size());

        // 2. Test thử một giao dịch Chuyển khoản nội bộ (In-bank Transfer)
        String source = "ACC-00017";
        String destination = "ACC-00020";
        BigDecimal amountToTransfer = new BigDecimal("5000.000"); // 500k VND
        String note = "Chuyen tien tieu tet 2026";

        System.out.println("\nExecuting In-bank Transfer...");
        int result = inBankTransfer(source, destination, amountToTransfer, note);

        // In ra dòng thông báo kết quả tương ứng
        System.out.println("Result Code: " + result);
        System.out.println("Message: " + getResultMessage(result));
    }
}