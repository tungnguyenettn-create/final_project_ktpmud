package org.example.final_project;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class NavigationManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage){
        primaryStage = stage;
    }

    public static void switchScene(String fxmlPath, String title, String... cssPaths){
        long totalStart = System.nanoTime();
        try {
            // Bước 3.1: Đọc file FXML và chạy hàm initialize() của Controller mới
            long s1 = System.nanoTime();
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
            Pane root = loader.load();
            long e1 = System.nanoTime();
            System.out.printf("[DEBUG-NAV] 3.1. FXML Load & Controller Init: %.2f ms%n", (e1 - s1) / 1_000_000.0);

            // Bước 3.2: Tìm và nạp các file CSS vào vùng chứa (Root)
            long s2 = System.nanoTime();
            for (String cssPath : cssPaths){
                if (cssPath != null && !cssPath.isEmpty()) {
                    String cssUrl = NavigationManager.class.getResource(cssPath).toExternalForm();
                    root.getStylesheets().add(cssUrl);
                }
            }
            long e2 = System.nanoTime();
            System.out.printf("[DEBUG-NAV] 3.2. CSS Processing: %.2f ms%n", (e2 - s2) / 1_000_000.0);

            // Bước 3.3: Tối ưu hóa bằng cách thay đổi ROOT thay vì tạo mới SCENE
            long s3 = System.nanoTime();
            Scene currentScene = primaryStage.getScene();

            if (currentScene == null) {
                // Lần đầu tiên chạy (màn hình đầu), chưa có Scene thì bắt buộc tạo mới
                currentScene = new Scene(root, 1200, 800);
                primaryStage.setScene(currentScene);
            } else {
                // TỪ LẦN THỨ HAI TRỞ ĐI: Chỉ thay lõi giao diện, không tạo lại cửa sổ hệ thống
                currentScene.setRoot(root);
            }

            primaryStage.setTitle(title);
            long e3 = System.nanoTime();
            System.out.printf("[DEBUG-NAV] 3.3. Scene & Stage Setup (Optimized): %.2f ms%n", (e3 - s3) / 1_000_000.0);

            // Bước 3.4: Kích hoạt hiển thị/vẽ giao diện lên màn hình
            long s4 = System.nanoTime();
            primaryStage.show();
            long e4 = System.nanoTime();
            System.out.printf("[DEBUG-NAV] 3.4. Stage Show (Rendering): %.2f ms%n", (e4 - s4) / 1_000_000.0);

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            long totalEnd = System.nanoTime();
            System.out.printf("[DEBUG-NAV] ==============================================%n");
            System.out.printf("[DEBUG-NAV] >>> TOTAL SWITCH SCENE TIME: %.2f ms <<<%n", (totalEnd - totalStart) / 1_000_000.0);
            System.out.printf("[DEBUG-NAV] ==============================================%n");
        }
    }
}