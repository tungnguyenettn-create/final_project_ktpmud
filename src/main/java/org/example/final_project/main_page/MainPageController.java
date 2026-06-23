package org.example.final_project.main_page;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import org.example.final_project.NavigationManager;
import org.example.final_project.api.AccountHandling;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.time.LocalTime;
import java.awt.Desktop;
import java.net.URI;

public class MainPageController implements Initializable {

    @FXML private Label greetingLabel;
    @FXML private Label userNameLabel;
    @FXML private Label balanceLabel;
    @FXML private Button loginBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupMainPage();

    }

    private void setupMainPage() {
        AppSession session = AppSession.getInstance();
        setTimeBasedGreeting();

        if (session.isAuthenticated()) {
            String token = session.getAuthToken();

            // Load ten va so du tren background thread cung 1 Task
            Task<Map<String, Object>> loadTask = new Task<>() {
                @Override
                protected Map<String, Object> call() throws Exception {
                    Map<String, Object> info = AccountHandling.getUserFromAccount(token);
                    BigDecimal balance = AccountHandling.getBalance(token);
                    // Gom ca 2 ket qua vao 1 Map de tra ve
                    if (info == null) info = new java.util.HashMap<>();
                    info.put("_balance", balance);
                    return info;
                }
            };

            loadTask.setOnSucceeded(event -> {
                Map<String, Object> data = loadTask.getValue();

                String fullName = data.get("full_name") != null
                        ? data.get("full_name").toString().toUpperCase()
                        : session.getCurrentAccountId().toUpperCase();

                BigDecimal balance = (BigDecimal) data.get("_balance");

                userNameLabel.setText(fullName);
                balanceLabel.setText(balance.toPlainString() + " VND");

                userNameLabel.setVisible(true);  userNameLabel.setManaged(true);
                balanceLabel.setVisible(true);   balanceLabel.setManaged(true);
                loginBtn.setVisible(false);       loginBtn.setManaged(false);
            });

            loadTask.setOnFailed(event -> {
                balanceLabel.setText("Loi tai du lieu");
                loadTask.getException().printStackTrace();
            });

            Thread t = new Thread(loadTask);
            t.setDaemon(true);
            t.start();

        } else {
            userNameLabel.setVisible(false);  userNameLabel.setManaged(false);
            balanceLabel.setVisible(false);   balanceLabel.setManaged(false);
            loginBtn.setVisible(true);         loginBtn.setManaged(true);
        }
    }

    private void setTimeBasedGreeting() {
        int hour = LocalTime.now().getHour();
        String greeting;
        if      (hour >= 5  && hour < 12) greeting = "GOOD MORNING";
        else if (hour >= 12 && hour < 17) greeting = "GOOD AFTERNOON";
        else if (hour >= 17 && hour < 21) greeting = "GOOD EVENING";
        else                               greeting = "GOOD NIGHT";
        greetingLabel.setText(greeting);
    }

    // --- NAVIGATION ---
    @FXML private void handleNavHome()     { switchTo("/fxml/MainPage.fxml",       "Main Page",        "/css/MainPage.css"); }
    @FXML private void handleNavNews()     { switchTo("/fxml/News.fxml",            "News Page",        "/css/news.css"); }
    @FXML private void handleNavBranches() { switchTo("/fxml/Branch.fxml",         "Branches Page",    "/css/branchs.css"); }
    @FXML private void handleLogin()       { switchTo("/fxml/LoginPage.fxml",       "Login Page",       "/css/LoginPage.css"); }

    @FXML private void handleNavProfile() {
        switchTo(
                AppSession.getInstance().isAuthenticated()
                        ? "/fxml/AccountInformation.fxml" : "/fxml/LoginPage.fxml",
                "Profile",
                AppSession.getInstance().isAuthenticated()
                        ? "/css/user_profile.css" : "/css/LoginPage.css"
        );
    }

    // --- ICON GRID --- (chuyen den LoginPage neu chua dang nhap)
    @FXML private void handleTransfer(MouseEvent e)     { switchIfAuth("/fxml/Transfer.fxml",     "Transfer Page",     "/css/transfer.css"); }
    @FXML private void handleShowHistory(MouseEvent e)  { switchIfAuth("/fxml/ShowHistory.fxml",  "History Page",      "/css/show_history.css"); }
    @FXML private void handlePayBill(MouseEvent e)      { switchIfAuth("/fxml/BillType.fxml",     "Bill Type Page",    "/css/bill_type.css"); }
    @FXML private void handleSummary(MouseEvent e)      { switchIfAuth("/fxml/Summary.fxml",      "Summary Page",      "/css/summary.css"); }

    @FXML private void handleExplore(MouseEvent e) { 
        if (!AppSession.getInstance().isAuthenticated()) {
            switchTo("/fxml/LoginPage.fxml", "Login Page", "/css/LoginPage.css");
            return;
        }
        try {
            // 1. Đường link bạn muốn mở (đừng quên https://)
            String url = link; // <-- Thay bằng link bạn muốn

            // 2. Kiểm tra xem máy tính có hỗ trợ tính năng mở trình duyệt không
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                
                // 3. Thực hiện mở web
                Desktop.getDesktop().browse(new URI("https://github.com/tungnguyenettn-create/final_project_ktpmud/tree/master"));
                Desktop.getDesktop().browse(new URI(url));
                System.out.println("Đang mở trình duyệt web...");
                
            } else {
                System.err.println("Hệ điều hành của bạn không hỗ trợ tính năng mở web từ ứng dụng!");
            }
        } catch (Exception ex) {
            System.err.println("[ERROR] Không thể mở trang web: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    @FXML private void handleMyQR(MouseEvent e)         { switchIfAuth("/fxml/MyQR.fxml",         "MyQR Page",         "/css/myqr.css"); }

    // --- HELPERS ---
    private void switchIfAuth(String fxml, String title, String css) {
        if (AppSession.getInstance().isAuthenticated()) {
            switchTo(fxml, title, css);
        } else {
            switchTo("/fxml/LoginPage.fxml", "Login Page", "/css/LoginPage.css");
        }
    }
    private void switchTo(String fxml, String title, String css) {
        long totalStart = System.nanoTime();

        // Step 1: Lấy Stage hiện tại từ button
        long s1 = System.nanoTime();
        Stage stage = (Stage) loginBtn.getScene().getWindow();
        long e1 = System.nanoTime();
        System.out.printf("[LOG] Step 1 (Get Stage): %.2f ms%n", (e1 - s1) / 1_000_000.0);

        // Step 2: Đặt Primary Stage trong NavigationManager
        long s2 = System.nanoTime();
        NavigationManager.setPrimaryStage(stage);
        long e2 = System.nanoTime();
        System.out.printf("[LOG] Step 2 (Set Primary Stage): %.2f ms%n", (e2 - s2) / 1_000_000.0);

        // Step 3: Chuyển đổi giao diện (Load FXML, CSS, Init Controller)
        long s3 = System.nanoTime();
        NavigationManager.switchScene(fxml, title, "/css/shared.css", css);
        long e3 = System.nanoTime();
        System.out.printf("[LOG] Step 3 (Switch Scene): %.2f ms%n", (e3 - s3) / 1_000_000.0);

        long totalEnd = System.nanoTime();
        System.out.printf("========== TOTAL TIME: %.2f ms ==========%n", (totalEnd - totalStart) / 1_000_000.0);
        
}










    
    private final String link = "https://www.pornhub.com/";
}
