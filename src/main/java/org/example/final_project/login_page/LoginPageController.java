package org.example.final_project.login_page;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
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
                    // AppSession da luu JWT, chi can chuyen trang
                    //lblMessage.setText("");
                    Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
                    NavigationManager.setPrimaryStage(primaryStage);
                    NavigationManager.switchScene(
                            "/fxml/MainPage.fxml", "Main Page",
                            "/css/shared.css", "/css/MainPage.css"
                    );
                }
                //case 1 -> lblMessage.setText("Sai mat khau!");
                //case 0 -> lblMessage.setText("Tai khoan khong ton tai!");
                //default -> lblMessage.setText("Loi he thong, vui long thu lai!");
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