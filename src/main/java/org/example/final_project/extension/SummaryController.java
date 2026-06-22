package org.example.final_project.extension;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import org.example.final_project.NavigationManager;
import org.example.final_project.api.HistoryHandling;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummaryController {

    @FXML
    private BarChart<String, Number> daysChart;

    @FXML
    private BarChart<String, Number> monthsChart;

    @FXML
    private Label navLogo;

    // 2. The method triggered by clicking the Logo (or the Home button)
    @FXML
    private void handleNavHome() {
        switchTo("/fxml/MainPage.fxml", "Main Page", "/css/MainPage.css");
    }
    @FXML private void handleNavNews()     { switchTo("/fxml/News.fxml",            "News Page",        "/css/news.css"); }
    @FXML private void handleNavBranches() { switchTo("/fxml/Branch.fxml",         "Branches Page",    "/css/branchs.css"); }
    @FXML private void handleNavProfile()      { switchTo("/fxml/UserInformation.fxml", "Profile Page",  "/css/user_profile.css"); }
    // 3. The helper method that actually performs the transition
    private void switchTo(String fxml, String title, String css) {
        // Get the current window using the navLogo
        Stage stage = (Stage) navLogo.getScene().getWindow();
        NavigationManager.setPrimaryStage(stage);

        // Tell the NavigationManager to switch the scene
        NavigationManager.switchScene(fxml, title, "/css/shared.css", css);
    }

    @FXML
    public void initialize() {
        AppSession session = AppSession.getInstance();

        if (session.isAuthenticated()) {
            String token = session.getAuthToken();

            // Vẽ biểu đồ 7 ngày và 12 tháng
            setupDaysChart(token);
            setupMonthsChart(token);
        } else {
            System.err.println("[WARN SummaryController] User chưa đăng nhập.");
        }
    }

    /**
     * Logic xử lý và nạp dữ liệu cho biểu đồ 7 ngày gần nhất
     */
    private void setupDaysChart(String token) {
        // 1. Lấy dữ liệu từ API
        List<Map<String, String>> dailyData = HistoryHandling.getDailyHistory(token);

        // Chuyển List từ API thành Map để tra cứu nhanh theo định dạng "yyyy-MM-dd"
        Map<String, Map<String, String>> apiDataMap = new HashMap<>();
        if (dailyData != null) {
            for (Map<String, String> tx : dailyData) {
                String dateKey = tx.get("date"); // "period_label" bên API đã được map thành "date"
                if (dateKey != null) {
                    apiDataMap.put(dateKey, tx);
                }
            }
        }

        // 2. Tạo 2 nhánh dữ liệu (Series) cho Income và Expenses
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income (Thu)");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses (Chi)");

        // 3. Tạo danh sách nhãn cho đúng 7 ngày gần đây (tính ngược từ hôm nay)
        LocalDate today = LocalDate.now();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 6; i >= 0; i--) {
            String targetDayStr = today.minusDays(i).format(dayFormatter);

            double inflow = 0.0;
            double outflow = 0.0;

            // Nếu API có chứa dữ liệu của ngày này thì lấy ra, ngược lại giữ nguyên 0.0
            if (apiDataMap.containsKey(targetDayStr)) {
                Map<String, String> data = apiDataMap.get(targetDayStr);
                inflow = parseDoubleSafe(data.get("inflow"));
                outflow = parseDoubleSafe(data.get("outflow"));
            }

            // Đưa dữ liệu vào Series (Dùng Math.abs để đảm bảo cột Chi tiêu luôn dương khi hiển thị đồ thị cột)
            incomeSeries.getData().add(new XYChart.Data<>(targetDayStr, inflow));
            expenseSeries.getData().add(new XYChart.Data<>(targetDayStr, Math.abs(outflow)));
        }

        // 4. Đổ dữ liệu vào biểu đồ
        daysChart.getData().clear();
        daysChart.getData().addAll(incomeSeries, expenseSeries);
    }

    /**
     * Logic xử lý và nạp dữ liệu cho biểu đồ 12 tháng gần nhất
     */
    private void setupMonthsChart(String token) {
        // 1. Lấy dữ liệu từ API
        List<Map<String, String>> monthlyData = HistoryHandling.getMonthlyHistory(token);

        // Chuyển List thành Map tra cứu nhanh theo định dạng "yyyy-MM"
        Map<String, Map<String, String>> apiDataMap = new HashMap<>();
        if (monthlyData != null) {
            for (Map<String, String> tx : monthlyData) {
                String monthKey = tx.get("date"); // Ví dụ: "2026-06"
                if (monthKey != null) {
                    apiDataMap.put(monthKey, tx);
                }
            }
        }

        // 2. Tạo 2 nhánh dữ liệu (Series)
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income (Thu)");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses (Chi)");

        // 3. Tạo danh sách nhãn cho đúng 12 tháng gần đây
        LocalDate today = LocalDate.now();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = 11; i >= 0; i--) {
            String targetMonthStr = today.minusMonths(i).format(monthFormatter);

            double inflow = 0.0;
            double outflow = 0.0;

            // Nếu ngày này có giao dịch trong API Map
            if (apiDataMap.containsKey(targetMonthStr)) {
                Map<String, String> data = apiDataMap.get(targetMonthStr);
                inflow = parseDoubleSafe(data.get("inflow"));
                outflow = parseDoubleSafe(data.get("outflow"));
            }

            // Đưa dữ liệu vào Series
            incomeSeries.getData().add(new XYChart.Data<>(targetMonthStr, inflow));
            expenseSeries.getData().add(new XYChart.Data<>(targetMonthStr, Math.abs(outflow)));
        }

        // 4. Đổ dữ liệu vào biểu đồ tháng
        monthsChart.getData().clear();
        monthsChart.getData().addAll(incomeSeries, expenseSeries);
    }

    /**
     * Hàm bổ trợ chuyển đổi chuỗi String sang Double an toàn tránh lỗi đúp (NumberFormatException)
     */
    private double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) {
            return 0.0;
        }
        try {
            // Thay thế mọi ký tự thừa hoặc định dạng lạ nếu có
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            System.err.println("[WARN SummaryController] Lỗi parse số: " + value);
            return 0.0;
        }
    }
}