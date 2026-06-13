package org.example.final_project.extension;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class BillController {

    @FXML
    private ComboBox<String> providerSelect;

    @FXML
    private TextField amountField;

    @FXML
    public void initialize() {
        // Thêm các option từ file HTML cũ vào ComboBox
        providerSelect.getItems().addAll("Bank A", "Bank B");
        providerSelect.getSelectionModel().selectFirst();

        // Ràng buộc Amount chỉ cho phép nhập số (tương đương type="number" trong HTML)
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                amountField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    private void handleTransfer(ActionEvent event) {
        String selectedProvider = providerSelect.getValue();
        String amount = amountField.getText();

        if (amount.isEmpty()) {
            System.out.println("Vui lòng nhập số tiền cần chuyển.");
            return;
        }

        // Logic xử lý chuyển tiền thực tế tại đây
        System.out.println("Đang thực hiện chuyển khoản...");
        System.out.println("Nhà cung cấp: " + selectedProvider);
        System.out.println("Số tiền: " + amount);
    }
}