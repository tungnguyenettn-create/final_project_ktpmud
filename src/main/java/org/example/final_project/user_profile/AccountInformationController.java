package org.example.final_project.user_profile;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import org.example.final_project.NavigationManager;
import org.example.final_project.api.AccountHandling;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class AccountInformationController implements Initializable {

    @FXML private TextField AccountField;
    @FXML private TextField DateField;
    @FXML private TextField BalanceField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AppSession session = AppSession.getInstance();

        if (!session.isAuthenticated()) {
            AccountField.setText("Chua dang nhap");
            BalanceField.setText("---");
            DateField.setText("---");
            return;
        }

        // Hien thi account ID ngay lap tuc — khong can doi API
        AccountField.setText(session.getCurrentAccountId());

        // Set placeholder trong khi cho load
        BalanceField.setText("Dang tai...");
        DateField.setText("Khong co du lieu");   // API chua tra open_date

        String token = session.getAuthToken();

        Task<Map<String, Object>> loadTask = new Task<>() {
            @Override
            protected Map<String, Object> call() throws Exception {
                return AccountHandling.getMetadata(token);
            }
        };

        loadTask.setOnSucceeded(event -> {
            Map<String, Object> meta = loadTask.getValue();
            if (meta == null) {
                BalanceField.setText("Loi tai du lieu");
                return;
            }

            String balance =  (String) meta.get("balance");
            String openDate    = (String) meta.get("open_date");

            BalanceField.setText(balance != null
                    ? balance + " VND"
                    : "---");

            DateField.setText(openDate != null
                    ? openDate
                    : "Khong co du lieu");
        });

        loadTask.setOnFailed(event -> {
            BalanceField.setText("Loi ket noi");
            loadTask.getException().printStackTrace();
        });

        Thread t = new Thread(loadTask);
        t.setDaemon(true);
        t.start();
    }

    // --- NAVIGATION ---
    @FXML private void handleNavHome()         { switchTo("/fxml/MainPage.fxml",       "Main Page",     "/css/MainPage.css"); }
    @FXML private void handleNavNews()         { switchTo("/fxml/News.fxml",            "News Page",     "/css/news.css"); }
    @FXML private void handleNavBranches()     { switchTo("/fxml/Branchs.fxml",         "Branches Page", "/css/branchs.css"); }
    @FXML private void handleNavProfile()      { switchTo("/fxml/UserInformation.fxml", "Profile Page",  "/css/user_profile.css"); }
    @FXML private void handleUserInformation() {switchTo("/fxml/UserInformation.fxml", "Profile Page",  "/css/user_profile.css");}
    private void switchTo(String fxml, String title, String css) {
        Stage stage = (Stage) AccountField.getScene().getWindow();
        NavigationManager.setPrimaryStage(stage);
        NavigationManager.switchScene(fxml, title, "/css/shared.css", css);
    }
}