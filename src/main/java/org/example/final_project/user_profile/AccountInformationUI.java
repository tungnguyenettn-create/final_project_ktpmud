package org.example.final_project.user_profile;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import java.io.IOException;

public class AccountInformationUI extends Application {
    private Stage stage;
    private Scene scene;
    private AppSession session;

    public AccountInformationUI() {
        this.session = AppSession.getInstance();
    }

    public AccountInformationUI(Stage stage) {
        this.stage = stage;
        this.session = AppSession.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        try {
            loadAccountInformationPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAccountInformationPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AccountInformation.fxml"));

        // Kỹ thuật tự gán chính class này làm Controller cho file FXML tĩnh
        fxmlLoader.setController(this);

        BorderPane root = fxmlLoader.load();

        // Tải các tệp định dạng CSS tương ứng từ HTML
        String sharedCss = getClass().getResource("/css/shared.css").toExternalForm();
        String accountProfileCss = getClass().getResource("/css/account_profile.css").toExternalForm();

        root.getStylesheets().addAll(sharedCss, accountProfileCss);

        // Đảm bảo kích thước phân giải đồng bộ 1200x800 cho toàn bộ hệ thống
        scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("36BANK - Account Profile");
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}