package org.example.final_project.extension;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import java.io.IOException;

public class BranchUI extends Application {
    private Stage stage;
    private Scene scene;
    private AppSession session;

    public BranchUI() {
        this.session = AppSession.getInstance();
    }

    public BranchUI(Stage stage) {
        this.stage = stage;
        this.session = AppSession.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        try {
            loadBranchPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadBranchPage() throws IOException {
        // Tải cấu trúc layout từ file fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Branch.fxml"));
        BorderPane root = fxmlLoader.load();

        // Nạp các file CSS cấu hình giao diện
        String sharedCss = getClass().getResource("/css/shared.css").toExternalForm();
        String branchCss = getClass().getResource("/css/branchs.css").toExternalForm();
        root.getStylesheets().addAll(sharedCss, branchCss);

        // Khởi tạo scene định dạng chuẩn hệ thống
        scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("SecureBank - Our Branches");
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