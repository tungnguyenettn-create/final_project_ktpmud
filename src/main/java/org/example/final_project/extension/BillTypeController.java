package org.example.final_project.extension;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class BillTypeController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization logic can go here if needed
    }

    // Navigation handlers (Navbar)
    @FXML
    private void handleNavHome() {
        System.out.println("Home clicked");
        // TODO: Navigate back to main page
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

    // Bill Type Cards Click Handlers
    @FXML
    private void handleElectricity(MouseEvent event) {
        System.out.println("Electricity bill selected");
        // TODO: Navigate to bill.html target page
    }

    @FXML
    private void handleWater(MouseEvent event) {
        System.out.println("Water bill selected");
        // TODO: Navigate to bill.html target page
    }

    @FXML
    private void handleInternet(MouseEvent event) {
        System.out.println("Internet bill selected");
        // TODO: Navigate to bill.html target page
    }

    @FXML
    private void handleMobile(MouseEvent event) {
        System.out.println("Mobile recharge selected");
        // TODO: Navigate to bill.html target page
    }
}
