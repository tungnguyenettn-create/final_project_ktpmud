package org.example.final_project.login_page;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import org.example.final_project.NavigationManager;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginPageController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    //@FXML private Label lblMessage;

    private AppSession session;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.session = AppSession.getInstance();
    }

    @FXML
    private void handleLoginSubmit() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            //lblMessage.setText("Vui long dien day du thong tin!");
            showAlert(Alert.AlertType.ERROR, "Thieu thong tin dang nhap", "Ten nguoi dung hoac mat khau khong duoc de trong");
            return;
        }

        // Khoa UI tranh spam
        setUILocked(true);
        //lblMessage.setText("Dang xac thuc...");

        // Task chi lam 1 viec: goi AppSession.login() — no tu goi API va luu JWT
        Task<Integer> loginTask = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return session.login(username, password);
            }
        };

        loginTask.setOnSucceeded(event -> {
            setUILocked(false);
            int result = loginTask.getValue();

            switch (result) {
                case 2 -> {
                    showAlert(Alert.AlertType.CONFIRMATION, "Dang Nhap Thanh Cong", "Chuyen sang trang chu");
                    // AppSession da luu JWT, chi can chuyen trang
                    //lblMessage.setText("");
                    Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
                    NavigationManager.setPrimaryStage(primaryStage);
                    NavigationManager.switchScene(
                            "/fxml/MainPage.fxml", "Main Page",
                            "/css/shared.css", "/css/MainPage.css"
                    );
                }
                case 1 -> {
                    showAlert(Alert.AlertType.ERROR, "Dang nhap khong thanh cong", "Sai mat khau!");
                }
                case 3 -> {
                    showAlert(Alert.AlertType.ERROR, "Dang nhap khong thanh cong", "Da dang nhap o thiet bi khac");
                }
                case 0 -> {
                    showAlert(Alert.AlertType.ERROR, "Dang nhap khong thanh cong", "Tai khoan khong ton tai!");
                }
                default -> {
                    showAlert(Alert.AlertType.ERROR, "Dang nhap khong thanh cong", "Loi he thong, vui long thu lai!");
                }
            }
        });

        loginTask.setOnFailed(event -> {
            setUILocked(false);
            //lblMessage.setText("Mat ket noi, vui long kiem tra mang!");
            loginTask.getException().printStackTrace();
        });

        Thread thread = new Thread(loginTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void setUILocked(boolean locked) {
        loginBtn.setDisable(locked);
        usernameField.setEditable(!locked);
        passwordField.setEditable(!locked);
    }

    private java.util.Optional<ButtonType> showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    // --- DIEU HUONG ---
    @FXML private void handleNavHome() { switchTo("/fxml/MainPage.fxml", "Main Page", "/css/MainPage.css"); }
    @FXML private void handleNavNews() { switchTo("/fxml/News.fxml", "News Page", "/css/news.css"); }
    @FXML private void handleNavBranches() { switchTo("/fxml/Branchs.fxml", "Branchs Page", "/css/branchs.css"); }
    @FXML private void handleNavProfile() { switchTo("/fxml/UserInformation.fxml", "User Information Page", "/css/user_profile.css"); }
    @FXML private void handleForgetAccount() { switchTo("/fxml/ForgetAccount.fxml", "Forget Account Page", "/css/ForgetAccount.css"); }
    @FXML private void handleForgetPassword() { switchTo("/fxml/ForgetPassword.fxml", "Forget Password Page", "/css/ForgetPassword.css"); }

    private void switchTo(String fxml, String title, String pageCss) {
        Stage stage = (Stage) loginBtn.getScene().getWindow();
        NavigationManager.setPrimaryStage(stage);
        NavigationManager.switchScene(fxml, title, "/css/shared.css", pageCss);
    }
}