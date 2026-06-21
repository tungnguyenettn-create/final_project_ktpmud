package org.example.final_project.api;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryHandling {

    /**
     * Lay lich su bien dong so du.
     *
     * @param token  JWT token cua nguoi dung
     * @param type   "daily" (7 ngay qua) hoac "monthly" (theo thang)
     * @return       List cac giao dich, moi giao dich la 1 Map gom:
     * date, inflow, outflow, transaction_count
     */
    public static List<Map<String, String>> getHistory(String token, String type) {
        List<Map<String, String>> result = new ArrayList<>();
        try {
            JsonNode resp = ApiClient.get("/api/customer/history?type=" + type, token);

            if (!ApiClient.isSuccess(resp)) {
                System.err.println("[WARN getHistory] Server tra ve loi: " + resp);
                return result;
            }
            System.out.println(resp.toString());
            JsonNode data = resp.get("data");
            System.out.println(data.toString());
            if (data == null || !data.isArray()) return result;

            data.forEach(item -> {
                Map<String, String> entry = new HashMap<>();
                entry.put("date",              textOrNull(item, "period_label"));
                entry.put("inflow",            textOrNull(item, "total_inflow"));
                entry.put("outflow",           textOrNull(item, "total_outflow"));
                entry.put("transaction_count", textOrNull(item, "transaction_count"));
                result.add(entry);
            });

        } catch (Exception e) {
            System.err.println("[ERROR getHistory] " + e.getMessage());
        }
        return result;
    }

    public static List<Map<String, String>> getDailyHistory(String token) {
        return getHistory(token, "daily");
    }

    public static List<Map<String, String>> getMonthlyHistory(String token) {
        return getHistory(token, "monthly");
    }

    /**
     * Lay toan bo lich su giao dich chi tiet.
     * API: /api/customer/history_all
     *
     * @param token JWT token cua nguoi dung
     * @return      List cac giao dich chi tiet, moi giao dich gom cac truong:
     * transaction_id, transaction_time, transaction_type, amount_change, related_party, status, description
     */
    public static List<Map<String, String>> getHistoryAll(String token) {
        List<Map<String, String>> result = new ArrayList<>();
        try {
            // Goi API history_all
            JsonNode resp = ApiClient.get("/api/customer/history_all", token);

            if (!ApiClient.isSuccess(resp)) {
                System.err.println("[WARN getHistoryAll] Server tra ve loi: " + resp);
                return result;
            }

            JsonNode data = resp.get("data");
            if (data == null || !data.isArray()) return result;

            // Duyet qua mang cac giao dich va add vao map kết quả
            data.forEach(item -> {
                Map<String, String> entry = new HashMap<>();
                entry.put("transaction_id",   textOrNull(item, "transaction_id"));
                entry.put("transaction_time", textOrNull(item, "transaction_time"));
                entry.put("transaction_type", textOrNull(item, "transaction_type"));
                entry.put("amount_change",    textOrNull(item, "amount_change"));
                entry.put("related_party",    textOrNull(item, "related_party"));
                entry.put("status",           textOrNull(item, "status"));
                entry.put("description",      textOrNull(item, "description"));
                result.add(entry);
            });

        } catch (Exception e) {
            System.err.println("[ERROR getHistoryAll] " + e.getMessage());
        }
        return result;
    }

    private static String textOrNull(JsonNode node, String field) {
        JsonNode f = node.get(field);
        return (f != null && !f.isNull()) ? f.asText() : null;
    }

    public static void main(String[] args) {
        String[] tokenHolder = new String[1];
        AccountHandling.login("ACC-00014", "pin_hash", tokenHolder);
        String token = tokenHolder[0];

        if (token == null) {
            System.err.println("Khong lay duoc token.");
            return;
        }

        System.out.println("=== LICH SU 7 NGAY QUA ===");
        List<Map<String, String>> daily = getDailyHistory(token);
        daily.forEach(tx -> System.out.println(
                tx.get("date") + " | " + tx.get("inflow") + " | "
                        + tx.get("outflow") + " | " + tx.get("transaction_count")
        ));

        System.out.println("\n=== LICH SU THEO THANG ===");
        List<Map<String, String>> monthly = getMonthlyHistory(token);
        monthly.forEach(tx -> System.out.println(
                tx.get("date") + " | " + tx.get("inflow") + " | "
                        + tx.get("outflow") + " | " + tx.get("transaction_count")
        ));

        System.out.println("\n=== TOAN BO LICH SU GIAO DICH CHI TIET ===");
        List<Map<String, String>> historyAll = getHistoryAll(token);

        // In ra tieu de cac cot cho de nhin
        System.out.printf("%-6s | %-19s | %-25s | %-12s | %-25s | %-8s | %s\n",
                "ID", "Time", "Type", "Amount", "Related Party", "Status", "Description");
        System.out.println("-".repeat(120));

        // Duyet va format string de in ket qua ra console theo dang bang
        historyAll.forEach(tx -> System.out.printf(
                "%-6s | %-19s | %-25s | %-12s | %-25s | %-8s | %s\n",
                tx.get("transaction_id"),
                tx.get("transaction_time"),
                tx.get("transaction_type"),
                tx.get("amount_change"),
                tx.get("related_party"),
                tx.get("status"),
                tx.get("description")
        ));
    }
}