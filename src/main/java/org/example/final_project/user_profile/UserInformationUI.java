package org.example.final_project.user_profile;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import java.io.IOException;

public class UserInformationUI extends Application {
    private Stage stage;
    private Scene scene;
    private AppSession session; // Khai báo instance AppSession tại đây

    public UserInformationUI() {
        // Lấy instance của AppSession ngay khi khởi tạo giao diện
        this.session = AppSession.getInstance();
    }

    public UserInformationUI(Stage stage) {
        this.stage = stage;
        this.session = AppSession.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        try {
            loadUserInformationPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadUserInformationPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/UserInformation.fxml"));

        // Gán chính class UI này làm Controller để xử lý FXML (không cần tạo file Controller riêng)
        fxmlLoader.setController(this);

        BorderPane root = fxmlLoader.load();

        // Tải các tệp định dạng CSS đồng bộ cho Profile
        String sharedCss = getClass().getResource("/css/shared.css").toExternalForm();
        String userProfileCss = getClass().getResource("/css/user_profile.css").toExternalForm();

        root.getStylesheets().addAll(sharedCss, userProfileCss);

        scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("36BANK - User Profile");
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