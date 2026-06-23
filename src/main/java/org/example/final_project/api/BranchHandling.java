package org.example.final_project.api;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchHandling {

    /**
     * Lấy danh sách toàn bộ chi nhánh ngân hàng.
     * API: /api/info/branches (GET - Không cần Token)
     *
     * @return List các chi nhánh, mỗi chi nhánh là 1 Map gồm: id, name, address
     */
    public static List<Map<String, String>> getBranches() {
        List<Map<String, String>> result = new ArrayList<>();

        try {
            // Truyền null hoặc chuỗi rỗng vào vị trí token vì API này public
            JsonNode resp = ApiClient.get("/api/info/branches", null);

            // Kiểm tra phản hồi từ server
            if (!ApiClient.isSuccess(resp)) {
                System.err.println("[WARN getBranches] Server trả về lỗi: " + resp);
                return result;
            }

            // LƯU Ý: Mảng dữ liệu nằm trong key "branches" chứ không phải "data"
            JsonNode branchesArray = resp.get("branches");

            if (branchesArray == null || !branchesArray.isArray()) {
                return result;
            }

            // Duyệt qua mảng và bóc tách dữ liệu
            branchesArray.forEach(item -> {
                Map<String, String> entry = new HashMap<>();
                entry.put("id",      textOrNull(item, "id"));
                entry.put("name",    textOrNull(item, "name"));
                entry.put("address", textOrNull(item, "address"));
                result.add(entry);
            });

        } catch (Exception e) {
            System.err.println("[ERROR getBranches] Lỗi khi gọi API: " + e.getMessage());
        }

        return result;
    }

    /**
     * Hàm hỗ trợ bóc tách JSON an toàn, chống lỗi NullPointerException
     */
    private static String textOrNull(JsonNode node, String field) {
        JsonNode f = node.get(field);
        return (f != null && !f.isNull()) ? f.asText() : null;
    }

    /**
     * Hàm main để chạy test thử code độc lập (Không cần bật UI)
     */
    public static void main(String[] args) {
        System.out.println("=== DANH SÁCH CHI NHÁNH NGÂN HÀNG ===");

        List<Map<String, String>> listBranches = getBranches();

        if (listBranches.isEmpty()) {
            System.out.println("Không lấy được dữ liệu chi nhánh hoặc danh sách trống.");
            return;
        }

        // In tiêu đề bảng
        System.out.printf("%-5s | %-25s | %s\n", "ID", "Tên chi nhánh", "Địa chỉ");
        System.out.println("-".repeat(80));

        // In dữ liệu
        listBranches.forEach(branch -> System.out.printf(
                "%-5s | %-25s | %s\n",
                branch.get("id"),
                branch.get("name"),
                branch.get("address")
        ));
    }
}