package org.example.final_project.extension;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import java.io.IOException;

public class SummaryUI extends Application {
    private Stage stage;
    private Scene scene;
    private AppSession session;

    public SummaryUI() {
        this.session = AppSession.getInstance();
    }

    public SummaryUI(Stage stage) {
        this.stage = stage;
        this.session = AppSession.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        try {
            loadSummaryPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSummaryPage() throws IOException {
        // Tải cấu trúc FXML từ thư mục resource tương ứng
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Summary.fxml"));
        BorderPane root = fxmlLoader.load();

        // Nạp chồng các file CSS cấu trúc và giao diện đồ thị tài chính
        String sharedCss = getClass().getResource("/css/shared.css").toExternalForm();
        String summaryCss = getClass().getResource("/css/summary.css").toExternalForm();
        root.getStylesheets().addAll(sharedCss, summaryCss);

        // Khởi tạo scene độ phân giải chuẩn hệ thống
        scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("SecureBank - Financial Summary");
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