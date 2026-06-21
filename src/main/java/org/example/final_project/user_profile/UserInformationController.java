package org.example.final_project.user_profile;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import org.example.final_project.NavigationManager;
import org.example.final_project.api.AccountHandling;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class UserInformationController implements Initializable {

    @FXML private TextField NameField;
    //@FXML private TextField EmailField;
    @FXML private TextField IdentityField;
    @FXML private TextField PhoneField;
    @FXML private TextField AddressField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AppSession session = AppSession.getInstance();

        if (!session.isAuthenticated()) {
            setAllFields("Chua dang nhap");
            return;
        }

        // Placeholder trong khi doi API
        setAllFields("Dang tai...");

        String token = session.getAuthToken();

        Task<Map<String, Object>> loadTask = new Task<>() {
            @Override
            protected Map<String, Object> call() throws Exception {
                return AccountHandling.getUserFromAccount(token);
            }
        };

        loadTask.setOnSucceeded(event -> {
            Map<String, Object> data = loadTask.getValue();

            if (data == null) {
                setAllFields("Loi tai du lieu");
                return;
            }

            // API tra ve: full_name, identity_card, nationality,
            //             dob, city, address, branch_id
            // EmailField va PhoneField hien API chua co — de trong
            NameField.setText(getOrDefault(data, "full_name"));
            IdentityField.setText(getOrDefault(data, "identity_card"));
            AddressField.setText(getOrDefault(data, "address"));
            //EmailField.setText("---");   // API chua co field nay
            PhoneField.setText(getOrDefault(data, "phone"));   // API chua co field nay
        });

        loadTask.setOnFailed(event -> {
            setAllFields("Loi ket noi");
            loadTask.getException().printStackTrace();
        });

        Thread t = new Thread(loadTask);
        t.setDaemon(true);
        t.start();
    }

    // --- HELPERS ---
    private void setAllFields(String value) {
        NameField.setText(value);
        //EmailField.setText(value);
        IdentityField.setText(value);
        PhoneField.setText(value);
        AddressField.setText(value);
    }

    private String getOrDefault(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return (val != null) ? val.toString() : "---";
    }

    // --- NAVIGATION ---
    @FXML private void handleNavHome()     { switchTo("/fxml/MainPage.fxml",       "Main Page",     "/css/MainPage.css"); }
    @FXML private void handleNavNews()     { switchTo("/fxml/News.fxml",            "News Page",     "/css/news.css"); }
    @FXML private void handleNavBranches() { switchTo("/fxml/Branchs.fxml",         "Branches Page", "/css/branchs.css"); }
    @FXML private void handleNavProfile()  { switchTo("/fxml/UserInformation.fxml", "Profile Page",  "/css/user_profile.css"); }
    @FXML private void handleAccountInformation() { switchTo("/fxml/AccountInformation.fxml", "Account Information Page", "/css/user_profile.css");}
    private void switchTo(String fxml, String title, String css) {
        Stage stage = (Stage) NameField.getScene().getWindow();
        NavigationManager.setPrimaryStage(stage);
        NavigationManager.switchScene(fxml, title, "/css/shared.css", css);
    }
}