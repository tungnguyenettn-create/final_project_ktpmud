package org.example.final_project.forget_password;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import org.example.final_project.NavigationManager;
import javafx.concurrent.Task;
import org.example.final_project.database.AccountHandling;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalTime;

public class ForgetPasswordController implements Initializable{


    @FXML
    private TextField usernameField;
    @FXML
    private TextField newPasswordField;
    @FXML
    private TextField confirmedPasswordField;
    @FXML
    private Button loginBtn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {setUPForgetPassword();}
    public void setUPForgetPassword(){
        AppSession session = AppSession.getInstance();
        //loginBtn.setVisible(false);
    }

    @FXML private void handleNavHome() {
        Stage primaryStage = (Stage) usernameField.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/MainPage.fxml", "Main Page", "/css/shared.css", "/css/MainPage.css") ;
    }
    @FXML private void handleNavNews() {
        Stage primaryStage = (Stage) usernameField.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/News.fxml", "News Page", "/css/shared.css", "/css/news.css");
    }
    @FXML private void handleNavBranches() {
        Stage primaryStage = (Stage) usernameField.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/Branchs.fxml", "Branchs Page", "/css/shared.css", "/css/branchs.css");
    }
    @FXML private void handleNavProfile() {
        Stage primaryStage = (Stage) usernameField.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/UserInformation.fxml", "User Informaion Page", "/css/shared.css", "/css/user_profile.css");
    }

    @FXML
    private void handleLoginSubmit() {
        String account = usernameField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmedPassword = confirmedPasswordField.getText();

        // 1. Client-side Validation
        if (account.isEmpty() || newPassword.isEmpty() || confirmedPassword.isEmpty()) {
            System.out.println("Error: All fields are required.");
            return;
        }

        if (!newPassword.equals(confirmedPassword)) {
            System.out.println("Error: Passwords do not match.");
            return;
        }

        // 2. Trigger the asynchronous database update
        updatePassword(account, newPassword);
    }

    private void updatePassword(String account, String password) {
        Task<Integer> passwordTask = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return AccountHandling.updatePassword(account, password);
            }
        };

        passwordTask.setOnSucceeded(event -> {
            Integer value = passwordTask.getValue();

            switch (value) {
                case 1:
                    System.out.println("Password updated successfully! Redirecting...");
                    // Move back to LoginPage.fxml
                    Stage primaryStage = (Stage) usernameField.getScene().getWindow();
                    NavigationManager.setPrimaryStage(primaryStage);
                    // Adjust CSS path names if your login stylesheet has a different name
                    NavigationManager.switchScene("/fxml/LoginPage.fxml", "Login Page", "/css/shared.css", "/css/LoginPage.css");
                    break;

                case 0:
                    System.out.println("Error: Account does not exist or is not open.");
                    // Remains at ForgetPassword.fxml (Do nothing/Show alert)
                    break;

                case -1:
                    System.out.println("Error: A database error occurred.");
                    // Remains at ForgetPassword.fxml (Do nothing/Show alert)
                    break;
            }
        });

        passwordTask.setOnFailed(event -> {
            System.err.println("Task failed execution.");
            Throwable e = passwordTask.getException();
            if (e != null) e.printStackTrace();
        });

        Thread thread = new Thread(passwordTask);
        thread.setDaemon(true);
        thread.start();
    }


}
