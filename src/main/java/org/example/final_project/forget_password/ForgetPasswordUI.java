package org.example.final_project.forget_password;


import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class ForgetPasswordUI extends Application{
    private Stage stage;
    private Scene scene;

    public ForgetPasswordUI(){

    }
    public ForgetPasswordUI(Stage stage){
        this.stage = stage;
    }
    @Override
    public void start(Stage primaryStage){
        this.stage = primaryStage;
        try {
            loadForgetPasswordPage();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadForgetPasswordPage() throws  IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ForgetPassword.fxml"));
        BorderPane root = fxmlLoader.load();

        // Load CSS stylesheets
        String sharedCss = getClass().getResource("/css/shared.css").toExternalForm();
        String ForgetPasswordCss = getClass().getResource("/css/LoginPage.css").toExternalForm();

        root.getStylesheets().addAll(sharedCss, ForgetPasswordCss);

        scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("36BANK - Forget Password Page");
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
