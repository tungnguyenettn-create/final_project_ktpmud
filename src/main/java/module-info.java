module org.example.final_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.final_project to javafx.fxml;
    exports org.example.final_project;
    opens org.example.final_project.main_page to javafx.graphics, javafx.fxml;


    // 1. Cho phép javafx.graphics truy cập class LoginPageUI để chạy ứng dụng
    exports org.example.final_project.login_page;

    // 2. Cho phép javafx.fxml truy cập LoginPageController bằng cơ chế Reflection
    opens org.example.final_project.login_page to javafx.fxml;

    // 1. Cho phép javafx.graphics truy cập class LoginPageUI để chạy ứng dụng
    exports org.example.final_project.forget_password;

    // 2. Cho phép javafx.fxml truy cập LoginPageController bằng cơ chế Reflection
    opens org.example.final_project.forget_password to javafx.fxml;

    // 1. Cho phép javafx.graphics truy cập class LoginPageUI để chạy ứng dụng
    exports org.example.final_project.forget_account;

    // 2. Cho phép javafx.fxml truy cập LoginPageController bằng cơ chế Reflection
    opens org.example.final_project.forget_account to javafx.fxml;


    // 1. Cho phép javafx.graphics truy cập class LoginPageUI để chạy ứng dụng
    exports org.example.final_project.user_profile;

    // 2. Cho phép javafx.fxml truy cập LoginPageController bằng cơ chế Reflection
    opens org.example.final_project.user_profile to javafx.fxml;

    exports org.example.final_project.extension;

    // 2. Cho phép javafx.fxml truy cập LoginPageController bằng cơ chế Reflection
    opens org.example.final_project.extension to javafx.fxml;


}