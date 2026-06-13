package org.example.final_project.main_page;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import org.example.final_project.AppSession;
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
    @FXML
    private void handleNavHome() {
        System.out.println("Home clicked");
        // TODO: Navigate to home page
    }

    @FXML
    private void handleNavNews() {
        System.out.println("News clicked");
        // TODO: Navigate to news page
    }

    @FXML
    private void handleNavBranches() {
        System.out.println("Branches clicked");
        // TODO: Navigate to branches page
    }

    @FXML
    private void handleNavProfile() {
        System.out.println("Profile clicked");
        // TODO: Navigate to profile page
    }

    // Icon grid handlers
    @FXML
    private void handleTransfer(MouseEvent event) {
        System.out.println("Money Transfer clicked");
        // TODO: Navigate to transfer page
    }

    @FXML
    private void handleShowHistory(MouseEvent event) {
        System.out.println("Show History clicked");
        // TODO: Navigate to history page
    }

    @FXML
    private void handlePayBill(MouseEvent event) {
        System.out.println("Pay Bill clicked");
        // TODO: Navigate to bill payment page
    }

    @FXML
    private void handleSummary(MouseEvent event) {
        System.out.println("Summary clicked");
        // TODO: Navigate to summary page
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
        System.out.println("Login clicked");
        // TODO: Navigate to login page
    }
}