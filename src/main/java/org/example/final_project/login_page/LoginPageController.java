package org.example.final_project.login_page;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField; // Khuyên dùng cho mật khẩu
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import org.example.final_project.NavigationManager;
import org.example.final_project.database.AccountHandling;
import javafx.fxml.FXMLLoader;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginPageController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private TextField passwordField; // Bạn nên đổi thành PasswordField trong FXML nếu được
    @FXML private Button loginBtn;
    @FXML private Label lblMessage; // Thêm một Label để báo lỗi lên UI cho người dùng thấy

    // Khai báo session làm thuộc tính của Class để dùng chung giữa các hàm
    private AppSession session;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupLoginPage();
    }

    public void setupLoginPage() {
        // Lấy instance của session ngay khi khởi tạo màn hình
        this.session = AppSession.getInstance();
    }

    // --- LỚP PHỤ ĐỂ CHỨA KẾT QUẢ ĐĂNG NHẬP ---
    private static class LoginResult {
        int status;       // 0: wrong acc, 1: wrong pass, 2: success, -1: error
        String fullName;  // Tên đầy đủ lấy từ DB nếu thành công

        LoginResult(int status, String fullName) {
            this.status = status;
            this.fullName = fullName;
        }
    }

    @FXML
    private void handleLoginSubmit() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Kiểm tra nhanh dữ liệu đầu vào trước khi gọi DB
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        // 1. Khóa UI để tránh người dùng nhấn spam liên tục khi đang xử lý
        loginBtn.setDisable(true);
        usernameField.setEditable(false);
        passwordField.setEditable(false);

        // 2. Tạo 1 Task duy nhất xử lý toàn bộ logic nặng dưới Database
        Task<LoginResult> loginTask = new Task<>() {
            @Override
            protected LoginResult call() throws Exception {
                // Bước A: Kiểm tra tài khoản mật khẩu
                int status = AccountHandling.login(username, password);
                String fullName = null;

                // Bước B: Nếu đăng nhập thành công (status == 2), lấy luôn full_name từ DB
                if (status == 2) {
                    var userMap = AccountHandling.getUserFromAccount(username);
                    if (userMap != null && userMap.containsKey("full_name")) {

                        //Load new page
                        fullName = userMap.get("full_name").toString();
                    }
                }

                return new LoginResult(status, fullName);
            }
        };

        // 3. Xử lý kết quả trả về trên UI Thread
        loginTask.setOnSucceeded(event -> {
            LoginResult result = loginTask.getValue();

            // Mở khóa lại các trường nhập liệu
            loginBtn.setDisable(false);
            usernameField.setEditable(true);
            passwordField.setEditable(true);

            switch (result.status) {
                case 2:

                    // Lưu thông tin vào AppSession (Sử dụng username gốc và token/password phù hợp)
                    // Lưu ý: Hàm login của AppSession nhận (username, password). Ở đây ta truyền tên user vào.
                    boolean isSessionSaved = session.login(username, "AUTHENTICATED", result.fullName);

                    System.out.println("Đăng nhập thành công! Hello: " + result.fullName);
                    Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
                    NavigationManager.setPrimaryStage(primaryStage);
                    NavigationManager.switchScene("/fxml/MainPage.fxml", "Main Page", "/css/shared.css", "/css/MainPage.css") ;

                    if (isSessionSaved) {
                        System.out.println("Session khởi tạo thành công. Chuyển trang...");
                    }
                    break;

                case 0:
                    System.out.println("Tài khoản không tồn tại!");
                    break;

                case 1:
                    System.out.println("Sai mật khẩu!");
                    break;

                case -1:
                default:
                    System.out.println("Có lỗi hệ thống xảy ra!");
                    break;
            }
        });

        // 4. Xử lý nếu Task bị crash đột ngột (Lỗi mất mạng, crash driver database...)
        loginTask.setOnFailed(event -> {
            loginBtn.setDisable(false);
            usernameField.setEditable(true);
            passwordField.setEditable(true);

            System.out.println("Lỗi kết nối đến Cơ sở dữ liệu!");
            loginTask.getException().printStackTrace();
        });

        // 5. Chạy luồng ngầm
        Thread dbThread = new Thread(loginTask);
        dbThread.setDaemon(true);
        dbThread.start();
    }

    // --- CÁC HÀM ĐIỀU HƯỚNG KHÁC ---
    @FXML private void handleNavHome() {
        Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/MainPage.fxml", "Main Page", "/css/shared.css", "/css/MainPage.css") ;
    }
    @FXML private void handleNavNews() {
        Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/News.fxml", "News Page", "/css/shared.css", "/css/news.css");
    }
    @FXML private void handleNavBranches() {
        Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/Branchs.fxml", "Branchs Page", "/css/shared.css", "/css/branchs.css");
    }
    @FXML private void handleNavProfile() {
        Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/UserInformation.fxml", "User Informaion Page", "/css/shared.css", "/css/user_profile.css");
    }
    @FXML private void handleForgetAccount() {
        Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/ForgetAccount.fxml", "Forget Account Page", "/css/shared.css", "/css/ForgetAccount.css");
    }
    @FXML private void handleForgetPassword() {
        Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/ForgetPassword.fxml", "Forget Password Page", "/css/shared.css", "/css/ForgetPassword.css");
    }
}