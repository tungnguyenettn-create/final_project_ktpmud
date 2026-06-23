module org.example.final_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    // 1. Khai báo sử dụng Jackson
    requires com.fasterxml.jackson.databind;

    // 2. Mở đúng package của bạn cho Jackson gom dữ liệu
    opens org.example.final_project.api to com.fasterxml.jackson.databind;
    requires java.net.http;
    
    // Thêm 2 dòng này để cấp quyền cho ZXing
    requires com.google.zxing;
    requires com.google.zxing.javase;

    requires java.desktop;

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
