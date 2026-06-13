package org.example.final_project.forget_account;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class ForgetAccountSuccessUI extends Application {
    private Stage stage;
    private Scene scene;

    public ForgetAccountSuccessUI() {
    }

    public ForgetAccountSuccessUI(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        try {
            loadForgetAccountSuccessPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadForgetAccountSuccessPage() throws IOException {
        // Nạp đúng file FXML thành công đã thiết kế
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ForgetAccountSuccess.fxml"));
        BorderPane root = fxmlLoader.load();

        // Tải các tệp cấu hình CSS tương ứng
        String sharedCss = getClass().getResource("/css/shared.css").toExternalForm();
        String forgetAccountCss = getClass().getResource("/css/ForgetAccount.css").toExternalForm();

        root.getStylesheets().addAll(sharedCss, forgetAccountCss);

        // Giữ nguyên kích thước màn hình tiêu chuẩn của dự án (1200 x 800)
        scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("36BANK - Forget Account Success");
        stage.show();
    }

    public Scene getScene() {
        return scene;
    }

    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}