package org.example.final_project.extension;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.final_project.NavigationManager;
import org.example.final_project.api.BranchHandling;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class BranchController implements Initializable {

    @FXML private Label navLogo;

    // Ánh xạ tới cái khung FlowPane trong FXML
    @FXML private FlowPane branchesFlowPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Tải dữ liệu ngay khi mở trang
        loadBranchData();
    }

    private void loadBranchData() {
        Task<List<Map<String, String>>> loadTask = new Task<>() {
            @Override
            protected List<Map<String, String>> call() throws Exception {
                return BranchHandling.getBranches(); // Gọi API
            }
        };

        // Khi tải thành công
        loadTask.setOnSucceeded(event -> {
            List<Map<String, String>> branches = loadTask.getValue();

            // Xóa sạch các dữ liệu cũ/hoặc text loading nếu có
            branchesFlowPane.getChildren().clear();

            // Duyệt qua danh sách, tạo Card và nhét vào FlowPane
            for (Map<String, String> branchData : branches) {
                VBox card = createBranchCard(branchData);
                branchesFlowPane.getChildren().add(card);
            }
        });

        // Khi tải thất bại
        loadTask.setOnFailed(event -> {
            System.err.println("Lỗi tải danh sách chi nhánh!");
            loadTask.getException().printStackTrace();

            // Có thể hiển thị 1 dòng thông báo lỗi lên màn hình
            Platform.runLater(() -> {
                branchesFlowPane.getChildren().clear();
                branchesFlowPane.getChildren().add(new Label("Không thể kết nối đến máy chủ."));
            });
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Hàm này tạo ra cái Card (VBox) y hệt cấu trúc bạn thiết kế trong FXML
     */
    private VBox createBranchCard(Map<String, String> branchData) {
        VBox card = new VBox();
        card.getStyleClass().add("card");
        card.setPrefWidth(420.0);
        card.setSpacing(8);
        card.setPadding(new Insets(24, 24, 24, 24));

        // Tên chi nhánh
        Label nameLabel = new Label(branchData.get("name"));
        nameLabel.getStyleClass().add("text-desc-medium");
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Địa chỉ
        Label addressLabel = new Label(branchData.get("address"));
        addressLabel.setWrapText(true);

        // Hiển thị thêm ID
        Label idLabel = new Label("Branch ID: " + branchData.get("id"));
        idLabel.setStyle("-fx-text-fill: -text-muted; -fx-font-size: 13px;");

        // Nhét 3 cái Label vào trong Card
        card.getChildren().addAll(nameLabel, addressLabel, idLabel);

        return card;
    }

    // --- CÁC HÀM ĐIỀU HƯỚNG CŨ ---
    @FXML private void handleNavHome()     { switchTo("/fxml/MainPage.fxml",       "Main Page",        "/css/MainPage.css"); }
    @FXML private void handleNavNews()     { switchTo("/fxml/News.fxml",            "News Page",        "/css/news.css"); }
    @FXML private void handleNavBranches() { switchTo("/fxml/Branch.fxml",         "Branches Page",    "/css/branchs.css"); }
    @FXML private void handleLogin()       { switchTo("/fxml/LoginPage.fxml",       "Login Page",       "/css/LoginPage.css"); }

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