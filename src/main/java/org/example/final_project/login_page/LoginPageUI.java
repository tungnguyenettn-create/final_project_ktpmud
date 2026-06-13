package org.example.final_project.login_page;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginPageUI extends Application{
    private Stage stage;
    private Scene scene;

    public LoginPageUI(){

    }
    public LoginPageUI(Stage stage){
        this.stage = stage;
    }
    @Override
    public void start(Stage primaryStage){
        this.stage = primaryStage;
        try {
            loadLoginPage();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadLoginPage() throws  IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LoginPage.fxml"));
        BorderPane root = fxmlLoader.load();

        // Load CSS stylesheets
        String sharedCss = getClass().getResource("/css/shared.css").toExternalForm();
        String LoginPageCss = getClass().getResource("/css/LoginPage.css").toExternalForm();

        root.getStylesheets().addAll(sharedCss, LoginPageCss);

        scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("36BANK - Login Page");
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
