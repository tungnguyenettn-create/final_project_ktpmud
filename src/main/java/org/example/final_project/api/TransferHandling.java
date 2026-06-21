package org.example.final_project.api;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TransferHandling — gọi REST API, parse JSON bằng Jackson.
 */
public class TransferHandling {

    // ------------------------------------------------------------------
    // 1. Danh sách ngân hàng liên kết được hỗ trợ
    // ------------------------------------------------------------------
    public static List<String> getSupportedBanks(String token) {
        List<String> banks = new ArrayList<>();
        try {
            JsonNode resp = ApiClient.get("/api/transfers/supported-banks", token);
            if (ApiClient.isSuccess(resp))
                resp.get("banks").forEach(n -> banks.add(n.asText()));
        } catch (Exception e) {
            System.err.println("[ERROR getSupportedBanks] " + e.getMessage());
        }
        return banks;
    }

    // ------------------------------------------------------------------
    // 2. Chuyển khoản nội bộ
    //    result_code: 1 = thành công, các mã khác = lỗi
    // ------------------------------------------------------------------
    public static int inBankTransfer(String token, String destAccountId,
                                     BigDecimal amount, String description) {
        try {
            Map<String, String> body = Map.of(
                    "dest_account", destAccountId,
                    "amount",       amount.toPlainString(),
                    "description",  description
            );
            JsonNode resp = ApiClient.post("/api/transfers/in_bank_transfer", body, token);
            return resp.has("result_code") ? resp.get("result_code").asInt() : -99;
        } catch (Exception e) {
            System.err.println("[ERROR inBankTransfer] " + e.getMessage());
            return -99;
        }
    }

    // ------------------------------------------------------------------
    // 3. Chuyển khoản liên ngân hàng
    // ------------------------------------------------------------------
    public static int outBankTransfer(String token, String destBankName,
                                      String destAccNumber, BigDecimal amount,
                                      String description) {
        try {
            Map<String, String> body = Map.of(
                    "desc_bank",   destBankName,
                    "desc_acc",    destAccNumber,
                    "amount",      amount.toPlainString(),
                    "description", description
            );
            JsonNode resp = ApiClient.post("/api/transfers/out_bank_transfer", body, token);
            return resp.has("result_code") ? resp.get("result_code").asInt() : -99;
        } catch (Exception e) {
            System.err.println("[ERROR outBankTransfer] " + e.getMessage());
            return -99;
        }
    }

    // ------------------------------------------------------------------
    // 4. Thanh toán hóa đơn
    // ------------------------------------------------------------------
    public static int payBill(String token, String providerName, BigDecimal amount) {
        try {
            Map<String, String> body = Map.of(
                    "provider_name", providerName,
                    "amount",        amount.toPlainString()
            );
            JsonNode resp = ApiClient.post("/api/transfers/pay-bill", body, token);
            return resp.has("result_code") ? resp.get("result_code").asInt() : -99;
        } catch (Exception e) {
            System.err.println("[ERROR payBill] " + e.getMessage());
            return -99;
        }
    }

    // ------------------------------------------------------------------
    // 5. Danh sách nhà cung cấp hóa đơn theo loại
    // ------------------------------------------------------------------
    public static List<String> getSupportedBillProviders(String token, String billType) {
        List<String> providers = new ArrayList<>();
        try {
            String encoded = URLEncoder.encode(billType, StandardCharsets.UTF_8);
            JsonNode resp = ApiClient.get("/api/transfers/supported-bill-providers?bill_type=" + encoded, token);
            if (ApiClient.isSuccess(resp))
                resp.get("providers").forEach(n -> providers.add(n.asText()));
        } catch (Exception e) {
            System.err.println("[ERROR getSupportedBillProviders] " + e.getMessage());
        }
        return providers;
    }

    // ------------------------------------------------------------------
    // Helper: dịch result_code → thông báo tiếng Việt
    // ------------------------------------------------------------------
    public static String getResultMessage(int code) {
        return switch (code) {
            case 1   -> "Giao dịch hoàn tất thành công!";
            case 0   -> "Thất bại: Tài khoản nguồn và đích không được trùng nhau.";
            case -1  -> "Thất bại: Số tiền giao dịch phải lớn hơn 0.";
            case 2   -> "Thất bại: Tài khoản nguồn không tồn tại hoặc đã bị đóng.";
            case 3   -> "Thất bại: Tài khoản đích không tồn tại hoặc đã bị đóng.";
            case 4   -> "Thất bại: Tài khoản không đủ số dư.";
            case 5   -> "Thất bại: Ngân hàng đích không được hỗ trợ.";
            case 6   -> "Thất bại: Nhà cung cấp hóa đơn không tồn tại.";
            case 7   -> "Thất bại: Nhân viên không tồn tại hoặc đã dừng hoạt động.";
            case -99 -> "Thất bại: Lỗi kết nối hệ thống.";
            default  -> "Thất bại: Mã lỗi không xác định (" + code + ").";
        };
    }

    // ------------------------------------------------------------------
    // main() — test
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        // Lấy token trước
        String[] tokenHolder = new String[1];
        AccountHandling.login("ACC-00014", "pin_hash", tokenHolder);
        String token = tokenHolder[0];

        if (token == null) {
            System.err.println("Không lấy được token, dừng test.");
            return;
        }

        System.out.println("Supported banks: " + getSupportedBanks(token));

        int r1 = inBankTransfer(token, "ACC-00015", new BigDecimal("500"), "Test chuyen tien");
        System.out.println("In-bank: " + r1 + " — " + getResultMessage(r1));

        int r2 = outBankTransfer(token, "External Bank Branch 1", "9999999999", new BigDecimal("200"), "Test");
        System.out.println("Out-bank: " + r2 + " — " + getResultMessage(r2));

        int r3 = payBill(token, "EVN Power Co 1", new BigDecimal("125"));
        System.out.println("Pay bill: " + r3 + " — " + getResultMessage(r3));

        System.out.println("Bill providers (Điện): " + getSupportedBillProviders(token, "Water"));
    }
}