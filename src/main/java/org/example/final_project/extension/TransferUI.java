package org.example.final_project.extension;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import java.io.IOException;

public class TransferUI extends Application {
    private Stage stage;
    private Scene scene;
    private AppSession session;

    public TransferUI() {
        this.session = AppSession.getInstance();
    }

    public TransferUI(Stage stage) {
        this.stage = stage;
        this.session = AppSession.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        try {
            loadTransferPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTransferPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Transfer.fxml"));
        BorderPane root = fxmlLoader.load();

        // Nạp các file CSS cấu hình giao diện form
        String sharedCss = getClass().getResource("/css/shared.css").toExternalForm();
        String transferCss = getClass().getResource("/css/transfer.css").toExternalForm();
        root.getStylesheets().addAll(sharedCss, transferCss);

        scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("36BANK - Transfer Funds");
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