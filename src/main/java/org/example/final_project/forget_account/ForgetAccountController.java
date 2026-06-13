package org.example.final_project.forget_account;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.concurrent.Task;

import org.example.final_project.AppSession;
import org.example.final_project.NavigationManager;
import org.example.final_project.database.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalTime;

public class ForgetAccountController implements Initializable {

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField identityField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUPForgetAccount();
    }

    public void setUPForgetAccount() {
        AppSession session = AppSession.getInstance();
        // Bạn có thể lấy instance của AccountSharing ở đây nếu cần khởi tạo trước
        AccountSharing sharing = AccountSharing.getInstance();
    }

    @FXML
    private void handleNavHome() {
        Stage primaryStage = (Stage) phoneNumberField.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/MainPage.fxml", "Main Page", "/css/shared.css", "/css/MainPage.css");
    }

    @FXML
    private void handleNavNews() {
        Stage primaryStage = (Stage) phoneNumberField.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/News.fxml", "News Page", "/css/shared.css", "/css/news.css");
    }

    @FXML
    private void handleNavBranches() {
        Stage primaryStage = (Stage) phoneNumberField.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/Branchs.fxml", "Branchs Page", "/css/shared.css", "/css/branchs.css");
    }

    @FXML
    private void handleNavProfile() {
        Stage primaryStage = (Stage) phoneNumberField.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/UserInformation.fxml", "User Information Page", "/css/shared.css", "/css/user_profile.css");
    }

    @FXML
    private void handleGetAccount() {
        String identityCard = identityField.getText();

        // 1. Kiểm tra dữ liệu đầu vào cơ bản
        if (identityCard == null || identityCard.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng nhập số Căn cước công dân / CMND.");
            return;
        }

        String finalIdentityCard = identityCard.trim();

        // 2. Tạo một Task xử lý bất đồng bộ (Background Thread) để kiểm tra DB
        Task<Boolean> checkIdTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // Gọi tầng Database xử lý (Hàm này trả về true nếu tồn tại)
                return UserHandling.identityCardExists(finalIdentityCard);
            }
        };

        // 3. Xử lý khi Task chạy THÀNH CÔNG (Đã kết nối DB và trả về kết quả)
        checkIdTask.setOnSucceeded(e -> {
            boolean isExist = checkIdTask.getValue();

            if (isExist) {
                // Lưu ID vào Singleton AccountSharing để Controller tiếp theo sử dụng
                AccountSharing.getInstance().setIdentityCard(finalIdentityCard);

                // Chuyển scene sang trang ForgetAccountSuccess
                Stage primaryStage = (Stage) identityField.getScene().getWindow();
                NavigationManager.setPrimaryStage(primaryStage);
                NavigationManager.switchScene(
                        "/fxml/ForgetAccountSuccess.fxml",
                        "Danh sách tài khoản",
                        "/css/shared.css",
                        "/css/ForgetAccountSuccess.css" // Thay đổi đường dẫn CSS tương ứng của bạn nếu có
                );
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Số CMND/CCCD không tồn tại trên hệ thống.");
            }
        });

        // 4. Xử lý khi Task bị LỖI (Ví dụ mất kết nối database, SQLException...)
        checkIdTask.setOnFailed(e -> {
            Throwable exception = checkIdTask.getException();
            if (exception != null) {
                exception.printStackTrace(); // Log lỗi ra console để debug
            }
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể kết nối đến cơ sở dữ liệu. Vui lòng thử lại sau.");
        });

        // 5. Chạy Task trên một Thread mới
        Thread thread = new Thread(checkIdTask);
        thread.setDaemon(true); // Đảm bảo thread tự tắt khi đóng ứng dụng
        thread.start();
    }

    // Hàm tiện ích để hiển thị thông báo nhanh
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}