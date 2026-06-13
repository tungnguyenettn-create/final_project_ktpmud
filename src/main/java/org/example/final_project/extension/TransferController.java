package org.example.final_project.extension;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.example.final_project.AppSession;

public class TransferController {

    @FXML
    private ComboBox<String> bankSelect;

    @FXML
    private TextField accountNumberField;

    @FXML
    private TextField amountField;

    private AppSession session;

    @FXML
    public void initialize() {
        // Lấy instance quản lý phiên làm việc hiện tại nếu cần dùng dữ liệu người chuyển
        this.session = AppSession.getInstance();

        // Tự động đổ dữ liệu vào ComboBox thay thế các thẻ <option> trong HTML
        bankSelect.getItems().addAll("Bank A", "Bank B");
        bankSelect.getSelectionModel().selectFirst(); // Mặc định chọn ngân hàng đầu tiên
    }

    @FXML
    private void handleTransfer(ActionEvent event) {
        // Trích xuất dữ liệu từ các ô Input khi nhấn nút Transfer
        String selectedBank = bankSelect.getValue();
        String accountNumber = accountNumberField.getText().trim();
        String amountRaw = amountField.getText().trim();

        // Khu vực xử lý Logic hoặc thực thi câu lệnh SQL gửi tiền sang Database của bạn
        if (accountNumber.isEmpty() || amountRaw.isEmpty()) {
            System.out.println("Vui lòng điền đầy đủ thông tin chuyển khoản!");
            return;
        }

        try {
            double amount = Double.parseDouble(amountRaw);
            System.out.println("--- Thực thi lệnh chuyển khoản ---");
            System.out.println("Ngân hàng đích: " + selectedBank);
            System.out.println("Số tài khoản nhận: " + accountNumber);
            System.out.println("Số tiền gửi: $" + amount);

            // TODO: Thêm logic trừ tiền tài khoản hiện tại và cộng tiền tài khoản đích tại đây

        } catch (NumberFormatException e) {
            System.out.println("Số tiền nhập vào không hợp lệ!");
        }
    }
}