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
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
            Pane root = loader.load();
            for (String cssPath : cssPaths){
                String cssUrl = NavigationManager.class.getResource(cssPath).toExternalForm();

                root.getStylesheets().add(cssUrl);
            }
            Scene scene =new Scene(root, 1200, 800);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
