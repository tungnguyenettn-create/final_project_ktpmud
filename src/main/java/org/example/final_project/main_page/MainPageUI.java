package org.example.final_project.main_page;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.Parent;
// 1. Extend Application so JavaFX knows how to launch it
public class MainPageUI extends Application {
    private Stage stage;
    private Scene scene;

    // Zero-argument constructor is required by JavaFX launch mechanism
    public MainPageUI() {
    }

    public MainPageUI(Stage stage) {
        this.stage = stage;
    }

    // 2. Override the start method to catch the primary stage
    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        try {
            loadMainPage();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load the FXML or CSS files. Check your resource paths.");
        }
    }

    public void loadMainPage() throws IOException {
        // Double-check this path matches your src/main/resources folder structure
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainPage.fxml"));
        Parent root = fxmlLoader.load();

        // Load CSS stylesheets
        String sharedCss = getClass().getResource("/css/shared.css").toExternalForm();
        String mainPageCss = getClass().getResource("/css/MainPage.css").toExternalForm();

        root.getStylesheets().addAll(sharedCss, mainPageCss);

        scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("36BANK - Home");
        stage.show();
    }

    public Scene getScene() {
        return scene;
    }

    public Stage getStage() {
        return stage;
    }

    // 3. The main method now launches this class successfully
    public static void main(String[] args) {
        launch(args);
    }
}