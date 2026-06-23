package org.example.final_project.api;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountHandling {

    public static int login(String account, String password) {
        return login(account, password, null);
    }

    public static int login(String account, String password, String[] tokenHolder) {
        try {
            Map<String, String> body = Map.of("username", account, "password", password);
            JsonNode resp = ApiClient.post("/api/customer/login", body, null);

            if (ApiClient.isSuccess(resp)) {
                if (tokenHolder != null)
                    tokenHolder[0] = resp.get("token").asText();
                return 2;   // ✅ Login success
            }

            String msg = resp.has("message") ? resp.get("message").asText() : "";

            // ✅ Detect duplicate login (Flask returns 403 with this message)
            if (msg.contains("đăng nhập ở nơi khác") || msg.contains("Vui lòng đăng xuất")) {
                return 3;   // ✅ New return code: already logged in elsewhere
            }

            return msg.contains("Sai") ? 1 : 0;

        } catch (Exception e) {
            System.err.println("[ERROR login] " + e.getMessage());
            return -1;
        }
    }

    public static BigDecimal getBalance(String token) {
        try {
            JsonNode resp = ApiClient.get("/api/customer/balance", token);
            if (ApiClient.isSuccess(resp))
                return resp.get("balance").decimalValue();
        } catch (Exception e) {
            System.err.println("[ERROR getBalance] " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    public static List<String> getAccountFromUser(String identityCard) {
        List<String> result = new ArrayList<>();
        try {
            String body = ApiClient.JSON.writeValueAsString(
                    Map.of("identiy-card", identityCard)
            );
            JsonNode resp = ApiClient.getWithBody("/api/customer/forget-account", body, null);

            if (resp.isArray()) {
                // Server tra ve array thang: ["ACC-00014", "ACC-00015", ...]
                resp.forEach(node -> result.add(node.asText()));
            } else if (ApiClient.isSuccess(resp) && resp.has("account_id")) {
                // Fallback neu server doi thanh object: {"status":"success","account_id":"..."}
                result.add(resp.get("account_id").asText());
            }

        } catch (Exception e) {
            System.err.println("[ERROR getAccountFromUser] " + e.getMessage());
        }
        return result;
    }

    public static Map<String, Object> getUserFromAccount(String token) {
        try {
            JsonNode resp = ApiClient.get("/api/customer/my-info", token);
            if (ApiClient.isSuccess(resp)) {
                JsonNode data = resp.get("data");
                Map<String, Object> map = new HashMap<>();
                map.put("customer_id",   textOrNull(data, "customer_id"));
                map.put("full_name",     textOrNull(data, "full_name"));
                map.put("identity_card", textOrNull(data, "identity_card"));
                map.put("nationality",   textOrNull(data, "nationality"));
                map.put("dob",           textOrNull(data, "dob"));
                map.put("city",          textOrNull(data, "city"));
                map.put("address",       textOrNull(data, "address"));
                map.put("phone",         textOrNull(data, "phone"));
                map.put("branch_id",     data.has("branch_id") ? data.get("branch_id").asInt() : null);
                return map;
            }
        } catch (Exception e) {
            System.err.println("[ERROR getUserFromAccount] " + e.getMessage());
        }
        return null;
    }

    public static int updatePassword(String token, String newPassword) {
        try {

            Map<String, String> body = Map.of("new_password", newPassword);
            JsonNode resp = ApiClient.put("/api/customer/change-password", body, token);
            return ApiClient.isSuccess(resp) ? 1 : 0;
        } catch (Exception e) {
            System.err.println("[ERROR updatePassword] " + e.getMessage());
            return -1;
        }
    }

    public static Map<String, Object> getMetadata(String token) {

        Map<String, Object> meta = new HashMap<>();
        try {
            JsonNode resp = ApiClient.get("/api/customer/acc-info", token);
            System.out.println(resp);
            if (ApiClient.isSuccess(resp)) {
                JsonNode data = resp.get("data");
                meta.put("balance", textOrNull(data, "balance"));
                meta.put("open_date", textOrNull(data, "open_date"));
            }
        }
        catch (Exception  e){
            System.err.println(e.getMessage());
        }
        System.out.println(meta.toString());
        return meta;
    }

    private static String textOrNull(JsonNode node, String field) {
        JsonNode f = node.get(field);
        return (f != null && !f.isNull()) ? f.asText() : null;
    }

    public static void main(String[] args) {
        System.out.println("=== ACCOUNT HANDLING - API MODE ===\n");

        String[] tokenHolder = new String[1];
        int result = login("ACC-00014", "pin_hash", tokenHolder);

        System.out.println("Login result (expect 2): " + result);
        System.out.println("Token: " + tokenHolder[0]);

        Map<String, Object> meta = getMetadata(tokenHolder[0]);
        System.out.println(meta.toString());
        if (tokenHolder[0] != null) {
            System.out.println("Balance: " + getBalance(tokenHolder[0]));
            System.out.println("User info: " + getUserFromAccount(tokenHolder[0]));
        }

        System.out.println("Accounts by CCCD 'CUST-ID-14': " + getAccountFromUser("CUST-ID-14").getFirst());
    }
}