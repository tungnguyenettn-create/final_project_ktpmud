package org.example.final_project.extension;

import javafx.fxml.FXML;
import javafx.scene.control.Label; // Changed from Button to Label
import javafx.stage.Stage;
import org.example.final_project.NavigationManager;

public class NewsController {

    // 1. We will link this to the 36Bank Logo
    @FXML
    private Label navLogo;

    // 2. The method triggered by clicking the Logo (or the Home button)
    @FXML
    private void handleNavHome() {
        switchTo("/fxml/MainPage.fxml", "Main Page", "/css/MainPage.css");
    }
    @FXML private void handleNavNews()     { switchTo("/fxml/News.fxml",            "News Page",        "/css/news.css"); }
    @FXML private void handleNavBranches() { switchTo("/fxml/Branch.fxml",         "Branches Page",    "/css/branchs.css"); }
    @FXML private void handleLogin()       { switchTo("/fxml/LoginPage.fxml",       "Login Page",       "/css/LoginPage.css"); }

    // 3. The helper method that actually performs the transition
    private void switchTo(String fxml, String title, String css) {
        if (navLogo == null || navLogo.getScene() == null) {
            System.err.println("Cannot switch scene: navLogo or Scene is null.");
            return;
        }

        Stage stage = (Stage) navLogo.getScene().getWindow();
        NavigationManager.setPrimaryStage(stage);
        NavigationManager.switchScene(fxml, title, "/css/shared.css", css);
    }
}