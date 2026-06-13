package org.example.final_project.extension;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

// 1. Extend Application so JavaFX knows how to launch it
public class BillTypeUI extends Application {
    private Stage stage;
    private Scene scene;

    // Zero-argument constructor is required by JavaFX launch mechanism
    public BillTypeUI() {
    }

    public BillTypeUI(Stage stage) {
        this.stage = stage;
    }

    // 2. Override the start method to catch the primary stage
    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        try {
            loadBillTypePage();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load the FXML or CSS files. Check your resource paths.");
        }
    }

    public void loadBillTypePage() throws IOException {
        // Path matches src/main/resources folder structure
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/BillType.fxml"));
        BorderPane root = fxmlLoader.load();

        // Load CSS stylesheets
        String sharedCss = getClass().getResource("/css/shared.css").toExternalForm();
        String billTypeCss = getClass().getResource("/css/bill_type.css").toExternalForm();

        root.getStylesheets().addAll(sharedCss, billTypeCss);
        //root.getStylesheets().addAll(sharedCss);
        scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("36BANK - Select Bill Type");
        stage.show();
    }

    public Scene getScene() {
        return scene;
    }

    public Stage getStage() {
        return stage;
    }

    // 3. The main method to launch this application
    public static void main(String[] args) {
        launch(args);
    }
}