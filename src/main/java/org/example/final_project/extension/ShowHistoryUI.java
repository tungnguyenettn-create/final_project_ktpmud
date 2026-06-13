package org.example.final_project.extension;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import java.io.IOException;

public class ShowHistoryUI extends Application {
    private Stage stage;
    private Scene scene;
    private AppSession session;

    public ShowHistoryUI() {
        this.session = AppSession.getInstance();
    }

    public ShowHistoryUI(Stage stage) {
        this.stage = stage;
        this.session = AppSession.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        try {
            loadShowHistoryPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadShowHistoryPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ShowHistory.fxml"));

        // Tự gán chính lớp UI này điều khiển file FXML
        fxmlLoader.setController(this);

        BorderPane root = fxmlLoader.load();

        // Tải các tệp CSS định dạng hệ thống
        String sharedCss = getClass().getResource("/css/shared.css").toExternalForm();
        String showHistoryCss = getClass().getResource("/css/show_history.css").toExternalForm();

        root.getStylesheets().addAll(sharedCss, showHistoryCss);

        // Kích thước chuẩn cửa sổ 1200x800
        scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("36BANK - Transaction History");
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