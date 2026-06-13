package org.example.final_project.extension;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import java.io.IOException;

public class NewsUI extends Application {
    private Stage stage;
    private Scene scene;
    private AppSession session;

    public NewsUI() {
        this.session = AppSession.getInstance();
    }

    public NewsUI(Stage stage) {
        this.stage = stage;
        this.session = AppSession.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        try {
            loadNewsPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadNewsPage() throws IOException {
        // Tải cấu trúc layout từ file fxml (Không cần gọi controller)
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/News.fxml"));
        BorderPane root = fxmlLoader.load();

        // Nạp các file CSS cấu hình giao diện
        String sharedCss = getClass().getResource("/css/shared.css").toExternalForm();
        String newsCss = getClass().getResource("/css/news.css").toExternalForm();
        root.getStylesheets().addAll(sharedCss, newsCss);

        // Khởi tạo scene định dạng chuẩn hệ thống 1200x800
        scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("SecureBank - Financial News");
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