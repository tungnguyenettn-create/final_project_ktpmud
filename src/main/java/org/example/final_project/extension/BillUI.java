package org.example.final_project.extension;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import java.io.IOException;

public class BillUI extends Application {
    private Stage stage;
    private Scene scene;
    private AppSession session;

    public BillUI() {
        this.session = AppSession.getInstance();
    }

    public BillUI(Stage stage) {
        this.stage = stage;
        this.session = AppSession.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        try {
            loadBillPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadBillPage() throws IOException {
        // Tải file giao diện từ thư mục tài nguyên /fxml/
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Bill.fxml"));
        Parent root = fxmlLoader.load();

        // Nạp các file CSS cấu hình giao diện hóa đơn từ thư mục /css/
        String sharedCss = getClass().getResource("/css/shared.css").toExternalForm();
        String billCss = getClass().getResource("/css/bill.css").toExternalForm();
        root.getStylesheets().addAll(sharedCss, billCss);

        // Khởi tạo scene với kích thước tiêu chuẩn 1200x800 giống form Transfer
        scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("SecureBank - Bills Transfer");
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