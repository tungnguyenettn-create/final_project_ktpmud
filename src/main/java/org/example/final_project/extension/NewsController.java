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

    // 3. The helper method that actually performs the transition
    private void switchTo(String fxml, String title, String css) {
        // Get the current window using the navLogo
        Stage stage = (Stage) navLogo.getScene().getWindow();
        NavigationManager.setPrimaryStage(stage);

        // Tell the NavigationManager to switch the scene
        NavigationManager.switchScene(fxml, title, "/css/shared.css", css);
    }
}