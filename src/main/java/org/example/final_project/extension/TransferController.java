package org.example.final_project.extension;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.example.final_project.AppSession;
import org.example.final_project.database.TransferHandling;
import org.example.final_project.NavigationManager;
import javafx.concurrent.Task;

import java.math.BigDecimal;
import java.util.List;

public class TransferController {

    @FXML
    private ComboBox<String> bankSelect;

    @FXML
    private TextField accountNumberField;

    @FXML
    private TextField descriptionField;
    @FXML
    private TextField amountField;

    private AppSession session;

    @FXML
    public void initialize() {
        this.session = AppSession.getInstance();

        descriptionField.setText(session.getCurrentFullName() + " chuyen");
        // 1. Create the background task to run the DB query
        Task<List<String>> loadBanksTask = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                // This executes on a background thread.
                // Your database connection and loop happen here safely.
                return TransferHandling.get_supported_bank();
            }
        };

        // 2. Handle successful query completion
        loadBanksTask.setOnSucceeded(event -> {
            // This runs automatically back on the JavaFX Application Thread
            List<String> supportedBanks = loadBanksTask.getValue();

            if (supportedBanks != null && !supportedBanks.isEmpty()) {
                bankSelect.getItems().addAll(supportedBanks);
                bankSelect.getSelectionModel().selectFirst(); // Selects "36 BANKs" or the first item
            }
        });

        // 3. Handle database errors gracefully
        loadBanksTask.setOnFailed(event -> {
            Throwable exception = loadBanksTask.getException();
            System.err.println("Database error while fetching banks!");
            exception.printStackTrace();

            // Optional: Update UI to show an error state
            bankSelect.setPromptText("Failed to load banks");
        });

        // 4. Start the background thread
        Thread thread = new Thread(loadBanksTask);
        thread.setDaemon(true); // Ensures the thread dies if the app is closed
        thread.start();
    }
    @FXML
    private void handleTransfer(ActionEvent event) {
        // 1. Thu thập và chuẩn hóa dữ liệu từ Giao diện (UI Threads)
        String selectedBank = bankSelect.getValue();
        String destinationInput = accountNumberField.getText().trim();
        String amountRaw = amountField.getText().trim();
        String description = descriptionField.getText().trim();

        // Kiểm tra dữ liệu đầu vào cơ bản (Client-side validation)
        if (destinationInput.isEmpty() || amountRaw.isEmpty()) {
            System.out.println("Vui lòng điền đầy đủ thông tin chuyển khoản!");
            // TODO: Bạn có thể hiển thị một Alert dialog tại đây để báo cho user biết
            return;
        }

        // Đọc số tài khoản hiện tại của người dùng đang đăng nhập từ Session
        String sourceAccountId = session.getCurrentUser();
        if (sourceAccountId == null) {
            System.err.println("Lỗi hệ thống: Không tìm thấy thông tin tài khoản người dùng đăng nhập trong Session!");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountRaw);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Số tiền nhập vào phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Số tiền nhập vào không hợp lệ!");
            return;
        }

        System.out.println("Đang chuẩn bị xử lý giao dịch gửi lên hệ thống...");

        // 2. Tạo một Task chạy ngầm để gọi Database xử lý giao dịch tài chính
        Task<Integer> transferTask = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                // Kiểm tra xem người dùng chọn ngân hàng nội bộ ("36 BANK") hay ngân hàng liên kết ngoài
                if ("36 BANK".equalsIgnoreCase(selectedBank)) {
                    // Gọi hàm chuyển khoản nội bộ
                    return TransferHandling.inBankTransfer(sourceAccountId, destinationInput, amount, description);
                } else {
                    // Tìm Bank ID từ tên chi nhánh liên kết ngoài
                    String bankId = TransferHandling.getBankIdFromBankName(selectedBank);

                    if (bankId == null) {
                        return -2; // Định nghĩa mã lỗi tùy biến: Không tìm thấy Bank ID hợp lệ trong DB
                    }

                    // Gọi hàm chuyển khoản liên ngân hàng (Lưu ý: truyền ID dạng String hoặc đổi sang String tùy DB)
                    return TransferHandling.outBankTransfer(sourceAccountId, String.valueOf(bankId), amount, description);
                }
            }
        };

        // 3. Xử lý kết quả sau khi Task chạy ngầm trong Database thực thi xong
        // 3. Xử lý kết quả sau khi Task chạy ngầm trong Database thực thi xong
        transferTask.setOnSucceeded(e -> {
            int resultCode = transferTask.getValue();

            if (resultCode == 1) {
                // --- THÀNH CÔNG: Hiển thị Alert loại INFORMATION ---
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Giao dịch thành công");
                alert.setHeaderText(null);
                alert.setContentText(TransferHandling.getResultMessage(resultCode));

                // Chờ người dùng nhấn nút OK trên Alert
                alert.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        try {
                            // Chuyển hướng về trang chủ MainPage bằng NavigationManager của bạn
                            javafx.stage.Stage primaryStage = (javafx.stage.Stage) amountField.getScene().getWindow();
                            NavigationManager.setPrimaryStage(primaryStage);
                            NavigationManager.switchScene("/fxml/MainPage.fxml", "Main Page", "/css/shared.css", "/css/MainPage.css");
                        } catch (Exception ex) {
                            System.err.println("Lỗi khi chuyển scene về MainPage:");
                            ex.printStackTrace();
                        }
                    }
                });

            } else {
                // --- THẤT BẠI (Mã lỗi nghiệp vụ hoặc không tìm thấy Bank ID): Hiển thị Alert loại ERROR ---
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Giao dịch thất bại");
                alert.setHeaderText(null);

                // Lấy thông báo lỗi tương ứng
                String errorMsg = (resultCode == -2)
                        ? "Ngân hàng đối tác \"" + selectedBank + "\" không khả dụng hoặc sai mã định danh."
                        : TransferHandling.getResultMessage(resultCode);

                alert.setContentText(errorMsg);

                // Chờ người dùng nhấn OK để reset lại Form giao dịch hiện tại
                alert.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        // Reset toàn bộ các ô nhập liệu và ComboBox về mặc định
                        accountNumberField.clear();
                        amountField.clear();
                        descriptionField.setText(session.getCurrentFullName() + " chuyen");

                        if (!bankSelect.getItems().isEmpty()) {
                            bankSelect.getSelectionModel().selectFirst(); // Chọn lại ngân hàng đầu tiên ("36 BANK")
                        }
                    }
                });
            }
        });

        // 4. Bắt lỗi nếu luồng kết nối DB đột ngột bị gãy gánh (Crash, rớt mạng...)
        transferTask.setOnFailed(e -> {
            Throwable exception = transferTask.getException();
            System.err.println("Lỗi nghiêm trọng: Hệ thống xử lý giao dịch gặp sự cố phần cứng/kết nối!");
            exception.printStackTrace();
        });

        // 5. Kích hoạt luồng chạy ngầm thực thi lệnh chuyển tiền
        Thread thread = new Thread(transferTask);
        thread.setDaemon(true);
        thread.start();
    }
}