package org.example.final_project.extension;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import org.example.final_project.NavigationManager;

import java.net.URL;
import java.util.ResourceBundle;

public class BillTypeController implements Initializable {

    // Giu lai 1 reference toi Stage qua event dau tien nhan duoc
    private Stage stage;
    @FXML private Label navLogo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    // --- CHON LOAI HOA DON ---
    @FXML private void handleElectricity(MouseEvent e) { goToBill(e, "Electricity"); }
    @FXML private void handleWater(MouseEvent e)        { goToBill(e, "Water"); }
    @FXML private void handleInternet(MouseEvent e)     { goToBill(e, "Internet"); }
    @FXML private void handleMobile(MouseEvent e)       { goToBill(e, "Mobile"); }

    private void goToBill(MouseEvent e, String billType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Bill.fxml"));
            Parent root = loader.load();

            // Truyen billType vao BillController de no load dung danh sach nha cung cap
            BillController billController = loader.getController();
            billController.setBillType(billType);

            Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().addAll(
                    getClass().getResource("/css/shared.css").toExternalForm(),
                    getClass().getResource("/css/bill.css").toExternalForm()
            );
            s.setScene(scene);
            s.setTitle("Thanh toan — " + billType);
            s.show();

        } catch (Exception ex) {
            System.err.println("Loi chuyen trang Bill: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleNavHome(){
        switchTo("/fxml/MainPage.fxml", "Main Page", "/css/MainPage.css");
    }

    // Triggered by the Home Button
    @FXML
    private void handleNavHomeBtn() {
        switchTo("/fxml/MainPage.fxml", "Main Page", "/css/MainPage.css");
    }

    // Triggered by the News Button
    @FXML
    private void handleNavNews() {
        switchTo("/fxml/News.fxml", "News Page", "/css/news.css");
    }

    // Triggered by the Branches Button
    @FXML
    private void handleNavBranches() {
        switchTo("/fxml/Branch.fxml", "Branches Page", "/css/branchs.css");
    }

    // ĐÃ SỬA: Xóa bỏ tham số (ActionEvent event) ở đây
    @FXML
    private void handleNavProfile(){
        switchTo("/fxml/UserInformation.fxml", "User Profile Page", "/css/user_profile.css");
    }

    // Hàm Helper chuyển scene an toàn
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
