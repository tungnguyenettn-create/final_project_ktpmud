package org.example.final_project.forget_account;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import org.example.final_project.NavigationManager;
import org.example.final_project.database.AccountHandling;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ForgetAccountSuccessController implements Initializable {

    // Inject VBox bên trong ScrollPane từ file FXML vào đây
    // Hãy nhớ sửa file FXML thêm thuộc tính fx:id="accountContainer" vào thẻ <VBox> nhé!
    @FXML
    private VBox accountContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpForgetAccountSuccess();
    }

    public void setUpForgetAccountSuccess() {
        // Khởi tạo lấy dữ liệu từ session hiện tại nếu cần thiết
        AppSession session = AppSession.getInstance();

        // Gọi hàm load dữ liệu tài khoản lên giao diện
        loadAccount();
    }

    private void loadAccount() {
        // 1. Lấy số IdentityCard từ AccountSharing
        AccountSharing sharing = AccountSharing.getInstance();
        String identityCard = sharing.getIdentityCard();

        if (identityCard == null || identityCard.isEmpty()) {
            System.out.println("Lỗi: Không tìm thấy số CMND/CCCD trong phiên làm việc.");
            return;
        }

        // 2. Sử dụng Task để truy vấn Database ở luồng ngầm (Background Thread)
        // Giúp giao diện mượt mà, không bị khựng/lag khi đang kết nối DB
        Task<List<String>> loadTask = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                // Giả định hàm getAccountFromUser nhận vào identityCard và trả về List<String>
                return AccountHandling.getAccountFromUser(identityCard);
            }
        };

        // 3. Xử lý sau khi lấy dữ liệu thành công từ Database
        loadTask.setOnSucceeded(event -> {
            List<String> accounts = loadTask.getValue();

            // Xóa các nhãn (Label) mặc định hoặc placeholder cũ nếu có
            accountContainer.getChildren().clear();

            if (accounts == null || accounts.isEmpty()) {
                Label noAccountLabel = new Label("Không tìm thấy tài khoản nào ứng với số CMND này.");
                noAccountLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray; -fx-font-size: 13;");
                accountContainer.getChildren().add(noAccountLabel);
                return;
            }

            // Duyệt qua danh sách tài khoản và render ra UI
            for (String accountNumber : accounts) {
                Label accountLabel = new Label(accountNumber);

                // Giữ style giống hệt như file FXML cũ của bạn
                accountLabel.setStyle("-fx-font-weight: 500; -fx-font-size: 14; -fx-text-fill: #333333;");

                // Thêm label vào container
                accountContainer.getChildren().add(accountLabel);
            }
        });

        // 4. Xử lý nếu xảy ra lỗi trong quá trình kết nối Database
        loadTask.setOnFailed(event -> {
            Throwable e = loadTask.getException();
            System.err.println("Lỗi khi tải danh sách tài khoản từ DB: " + e.getMessage());
            e.printStackTrace();

            accountContainer.getChildren().clear();
            Label errorLabel = new Label("Lỗi hệ thống: Không thể tải dữ liệu.");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            accountContainer.getChildren().add(errorLabel);
        });

        // 5. Chạy luồng ngầm
        new Thread(loadTask).start();
    }

    /**
     * Xử lý sự kiện khi người dùng bấm nút OK thành công
     * Điều hướng người dùng quay trở lại trang Đăng nhập (Login)
     */
    @FXML
    private void handleOkAction() {
        Stage primaryStage = (Stage) accountContainer.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/LoginPage.fxml", "Login Page", "/css/shared.css", "/css/LoginPage.css") ;
    }

    // Các hàm điều hướng trên thanh Navbar (Kế thừa đồng bộ từ controller trước của bạn)
    @FXML private void handleNavHome() {
        Stage primaryStage = (Stage) accountContainer.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/MainPage.fxml", "Main Page", "/css/shared.css", "/css/MainPage.css") ;
    }
    @FXML private void handleNavNews() {
        Stage primaryStage = (Stage) accountContainer.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/News.fxml", "News Page", "/css/shared.css", "/css/news.css");
    }
    @FXML private void handleNavBranches() {
        Stage primaryStage = (Stage) accountContainer.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/Branchs.fxml", "Branchs Page", "/css/shared.css", "/css/branchs.css");
    }
    @FXML private void handleNavProfile() {
        Stage primaryStage = (Stage) accountContainer.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/UserInformation.fxml", "User Informaion Page", "/css/shared.css", "/css/user_profile.css");
    }
}