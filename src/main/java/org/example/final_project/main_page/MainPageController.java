package org.example.final_project.main_page;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import org.example.final_project.NavigationManager;
import org.example.final_project.database.AccountHandling;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalTime;

public class MainPageController implements Initializable {

    @FXML
    private Label greetingLabel;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label balanceLabel;


    @FXML
    private Button loginBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupMainPage();
    }

    private void setupMainPage() {
        // Get the current session
        AppSession session = AppSession.getInstance();

        // Set greeting based on time of day
        setTimeBasedGreeting();

        // Check if user is authenticated
        if (session.isAuthenticated()) {
            String currentUser = session.getCurrentFullName();
            if (currentUser != null) {
                // Display user name (convert to uppercase for consistency with original)
                userNameLabel.setText(currentUser.toUpperCase());
                userNameLabel.setVisible(true);
                userNameLabel.setManaged(true);

                loadBalance();
                // Show balance and hide login button
                balanceLabel.setVisible(true);
                balanceLabel.setManaged(true);
                loginBtn.setVisible(false);
                loginBtn.setManaged(false);

            }
        } else {
            // User is not authenticated
            userNameLabel.setVisible(false);
            userNameLabel.setManaged(false);
            balanceLabel.setVisible(false);
            balanceLabel.setManaged(false);
            loginBtn.setVisible(true);
            loginBtn.setManaged(true);
        }
    }

    private void loadBalance() {

        AppSession session = AppSession.getInstance();
        String accountId = session.getCurrentUser();

        Task<BigDecimal> balanceTask = new Task<>() {
            @Override
            protected BigDecimal call() throws Exception {
                return AccountHandling.getBalance(accountId);
            }
        };

        balanceTask.setOnSucceeded(event -> {
            BigDecimal balance = balanceTask.getValue();
            balanceLabel.setText("$" + balance);
        });

        balanceTask.setOnFailed(event -> {
            balanceLabel.setText("Error");
            balanceTask.getException().printStackTrace();
        });

        Thread thread = new Thread(balanceTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void setTimeBasedGreeting() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();

        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = "GOOD MORNING";
        } else if (hour >= 12 && hour < 17) {
            greeting = "GOOD AFTERNOON";
        } else if (hour >= 17 && hour < 21) {
            greeting = "GOOD EVENING";
        } else {
            greeting = "GOOD NIGHT";
        }

        greetingLabel.setText(greeting);
    }

    // Navigation handlers
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
        if (AppSession.getInstance().isAuthenticated()) {
            Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
            NavigationManager.setPrimaryStage(primaryStage);
            NavigationManager.switchScene("/fxml/UserInformation.fxml", "User Informaion Page", "/css/shared.css", "/css/user_profile.css");
        }
        else {
            Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
            NavigationManager.setPrimaryStage(primaryStage);
            NavigationManager.switchScene("/fxml/LoginPage.fxml", "Login Page", "/css/shared.css", "/css/LoginPage.css");
        }
        }

    // Icon grid handlers
    @FXML
    private void handleTransfer(MouseEvent event) {
        if (AppSession.getInstance().isAuthenticated()) {
            System.out.println("Navigate to Transfer");
            Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
            NavigationManager.setPrimaryStage(primaryStage);
            NavigationManager.switchScene("/fxml/Transfer.fxml", "Transfer Page", "/css/shared.css", "/css/transfer.css");}
        else {
            Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
            NavigationManager.setPrimaryStage(primaryStage);
            NavigationManager.switchScene("/fxml/LoginPage.fxml", "Login Page", "/css/shared.css", "/css/LoginPage.css");
        }
    }

    @FXML
    private void handleShowHistory(MouseEvent event) {
        if (AppSession.getInstance().isAuthenticated()){
            System.out.println("Show History clicked");
            Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
            NavigationManager.setPrimaryStage(primaryStage);
            NavigationManager.switchScene("/fxml/ShowHistory.fxml", "Show History Page", "/css/shared.css", "/css/show_history.css");
        }else {
            Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
            NavigationManager.setPrimaryStage(primaryStage);
            NavigationManager.switchScene("/fxml/LoginPage.fxml", "Login Page", "/css/shared.css", "/css/LoginPage.css");
        }
    }

    @FXML
    private void handlePayBill(MouseEvent event) {
        if (AppSession.getInstance().isAuthenticated()){
            System.out.println("Pay Bill clicked");
            Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
            NavigationManager.setPrimaryStage(primaryStage);
            NavigationManager.switchScene("/fxml/BillType.fxml", "Bill Type Page", "/css/shared.css", "/css/bill_type.css");
        } else {
            Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
            NavigationManager.setPrimaryStage(primaryStage);
            NavigationManager.switchScene("/fxml/LoginPage.fxml", "Login Page", "/css/shared.css", "/css/LoginPage.css");
        }
    }

    @FXML
    private void handleSummary(MouseEvent event) {
        if (AppSession.getInstance().isAuthenticated()){
            System.out.println("Summary clicked");
            Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
            NavigationManager.setPrimaryStage(primaryStage);
            NavigationManager.switchScene("/fxml/Summary.fxml", "Summary Page", "/css/shared.css", "/css/summary.css");
        }
        else {
            Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
            NavigationManager.setPrimaryStage(primaryStage);
            NavigationManager.switchScene("/fxml/LoginPage.fxml", "Login Page", "/css/shared.css", "/css/LoginPage.css");

        }

    }

    @FXML
    private void handleExplore(MouseEvent event) {
        System.out.println("Explore clicked");
        // TODO: Navigate to explore page
    }

    @FXML
    private void handleMyQR(MouseEvent event) {
        System.out.println("My QR clicked");
        // TODO: Navigate to QR page
    }

    @FXML
    private void handleLogin() {
        Stage primaryStage = (Stage) loginBtn.getScene().getWindow();
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.switchScene("/fxml/LoginPage.fxml", "Login Page", "/css/shared.css", "/css/LoginPage.css");

    }
}