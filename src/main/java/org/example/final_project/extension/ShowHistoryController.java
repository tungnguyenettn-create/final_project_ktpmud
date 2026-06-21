package org.example.final_project.extension;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import org.example.final_project.AppSession; // Import AppSession của bạn
import org.example.final_project.api.HistoryHandling;

import java.util.List;
import java.util.Map;

public class ShowHistoryController {

    @FXML
    private GridPane historyGrid;

    /**
     * Hàm tự động chạy ngay khi giao diện FXML được nạp thành công
     */
    @FXML
    public void initialize() {
        // Lấy session hiện tại
        AppSession session = AppSession.getInstance();

        // Kiểm tra nếu user đã đăng nhập hợp lệ
        if (session.isAuthenticated()) {
            String token = session.getAuthToken();

            // Gọi hàm load dữ liệu lên giao diện
            loadHistoryData(token);
        } else {
            System.err.println("[WARN] Người dùng chưa đăng nhập hoặc phiên làm việc hết hạn.");
            // Bạn có thể hiển thị một Label thông báo "Vui lòng đăng nhập" lên giao diện ở đây nếu muốn
        }
    }

    /**
     * Hàm nội bộ xử lý lấy dữ liệu từ API và vẽ lên GridPane
     */
    private void loadHistoryData(String token) {
        // 1. Lấy toàn bộ danh sách lịch sử giao dịch từ API chi tiết
        List<Map<String, String>> historyList = HistoryHandling.getHistoryAll(token);

        // 2. Dọn dẹp dữ liệu cũ (giữ lại tiêu đề hàng 0 và gạch ngang hàng 1)
        historyGrid.getChildren().removeIf(node -> {
            Integer rowIndex = GridPane.getRowIndex(node);
            return rowIndex != null && rowIndex >= 2;
        });

        int currentRow = 2;

        // 3. Duyệt danh sách để tạo dòng động
        for (Map<String, String> tx : historyList) {
            String time = tx.getOrDefault("transaction_time", "N/A");
            String description = tx.getOrDefault("description", "N/A");
            String amount = tx.getOrDefault("amount_change", "0");
            String status = tx.getOrDefault("status", "N/A");

            // Cột 0: Thời gian
            Label lblDate = new Label(time);
            lblDate.setStyle("-fx-text-fill: #4B5563; -fx-font-weight: 600; -fx-font-size: 14;");

            // Cột 1: Mô tả
            Label lblDesc = new Label(description);
            lblDesc.setStyle("-fx-text-fill: #4B5563; -fx-font-weight: 600; -fx-font-size: 14;");

            // Cột 2: Số tiền (Xử lý màu xanh / đỏ dựa vào dấu + hoặc -)
            Label lblAmount = new Label(amount);
            if (amount != null && amount.trim().startsWith("-")) {
                lblAmount.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: 600; -fx-font-size: 14;"); // Đỏ cho tiền ra
            } else  {
                lblAmount.setText("+" + lblAmount.getText());
                lblAmount.setStyle("-fx-text-fill: #10b981; -fx-font-weight: 600; -fx-font-size: 14;"); // Xanh cho tiền vào
            }

            // Cột 3: Trạng thái
            Label lblStatus = new Label(status);
            lblStatus.setStyle("-fx-text-fill: #4B5563; -fx-font-weight: 600; -fx-font-size: 14;");

            // Thêm các nút nhãn vào hàng hiện tại
            historyGrid.add(lblDate, 0, currentRow);
            historyGrid.add(lblDesc, 1, currentRow);
            historyGrid.add(lblAmount, 2, currentRow);
            historyGrid.add(lblStatus, 3, currentRow);

            currentRow++;

            // Tạo đường gạch ngang phân cách mỏng giữa các hàng
            Separator rowSeparator = new Separator();
            rowSeparator.setStyle("-fx-border-color: #e5e7eb; -fx-border-width: 0 0 1 0;");
            GridPane.setColumnSpan(rowSeparator, 4);

            historyGrid.add(rowSeparator, 0, currentRow);

            currentRow++;
        }
    }
}