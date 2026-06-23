package org.example.final_project.extension;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import org.example.final_project.NavigationManager;
import org.example.final_project.api.AccountHandling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Path;

public class MyQRController {

    @FXML
    private Label navLogo;

    @FXML
    private ImageView qrCodeImageView; // ID phải khớp với file FXML
    
    // Thêm biến này để lưu trữ dữ liệu QR hiện tại
    private String currentQRData = "";

    // ==========================================
    // PHẦN 1: ĐIỀU HƯỚNG (NAVIGATION)
    // ==========================================
    @FXML
    private void handleNavHome() {
        switchTo("/fxml/MainPage.fxml", "Main Page", "/css/MainPage.css");
    }

    @FXML 
    private void handleNavNews() { 
        switchTo("/fxml/News.fxml", "News Page", "/css/news.css"); 
    }

    @FXML 
    private void handleNavBranches() { 
        switchTo("/fxml/Branch.fxml", "Branches Page", "/css/branchs.css"); 
    }

    @FXML 
    private void handleNavProfile() { 
        switchTo("/fxml/UserInformation.fxml", "Profile Page", "/css/user_profile.css"); 
    }

    private void switchTo(String fxml, String title, String css) {
        Stage stage = (Stage) navLogo.getScene().getWindow();
        NavigationManager.setPrimaryStage(stage);
        NavigationManager.switchScene(fxml, title, "/css/shared.css", css);
    }

    // ==========================================
    // PHẦN 2: KHỞI TẠO VÀ XỬ LÝ DỮ LIỆU
    // ==========================================
    
    /**
     * Hàm tự động chạy ngay khi giao diện FXML được nạp thành công
     */
    @FXML
    public void initialize() {
        // Lấy session hiện tại
        AppSession session = AppSession.getInstance();

        // Kiểm tra nếu user đã đăng nhập hợp lệ
        if (session.isAuthenticated()) {
            // Giả sử bạn muốn tạo mã QR chứa nội dung chuyển khoản hoặc thông tin User
            // Bạn có thể lấy thông tin từ session, ví dụ: session.getUsername() hoặc STK
            currentQRData = AccountHandling.getUserFromAccount(session.getAuthToken()).get("full_name").toString(); 
            
            // Gọi hàm tạo và hiển thị QR
            generateAndDisplayQRCode(currentQRData);
        } else {
            System.err.println("[WARN] Người dùng chưa đăng nhập hoặc phiên làm việc hết hạn.");
            // Xử lý khi chưa đăng nhập (ví dụ: đá về trang Login)
        }
    }

    /**
     * Hàm nội bộ xử lý thuật toán tạo QR và vẽ lên ImageView
     */
    private void generateAndDisplayQRCode(String data) {
        try {
            // Khởi tạo đối tượng ghi QR từ thư viện ZXing
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            
            // Kích thước mã QR (300x300 pixel)
            int width = 300;
            int height = 300;
            
            // Mã hóa chuỗi String thành ma trận điểm ảnh (BitMatrix)
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

            // Ghi ma trận này vào một luồng bộ nhớ (Memory Stream) thay vì lưu ra file ổ cứng
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            // Chuyển đổi dữ liệu byte thành đối tượng Image của JavaFX
            Image qrImage = new Image(new ByteArrayInputStream(pngData));
            
            // Gán hình ảnh lên giao diện FXML
            qrCodeImageView.setImage(qrImage);

        } catch (Exception e) {
            System.err.println("[ERROR] Lỗi khi tạo mã QR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================
    // PHẦN 3: CÁC CHỨC NĂNG PHỤ THÊM
    // ==========================================
    
    /**
     * Xử lý khi người dùng bấm nút "Lưu mã QR" (Nếu có nút này trên FXML)
     */
    @FXML
    private void handleSaveQR() {
        // Kiểm tra xem có dữ liệu để lưu không
        if (currentQRData == null || currentQRData.isEmpty()) {
            System.err.println("Không có dữ liệu QR để lưu!");
            return;
        }

        // 1. Tạo hộp thoại chọn nơi lưu file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu mã QR Thanh Toán");
        
        // Đặt tên file mặc định và chỉ cho phép lưu định dạng ảnh PNG
        fileChooser.setInitialFileName("Ma_QR_36Bank.png");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Ảnh PNG (*.png)", "*.png")
        );

        // 2. Hiển thị hộp thoại và lấy đường dẫn người dùng đã chọn
        Stage stage = (Stage) navLogo.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        // 3. Nếu người dùng đã chọn vị trí lưu và bấm "Save" (không bấm Cancel)
        if (file != null) {
            try {
                // Tạo lại ma trận QR từ dữ liệu
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(currentQRData, BarcodeFormat.QR_CODE, 300, 300);

                // Ghi thẳng file ảnh ra ổ cứng tại vị trí đã chọn
                Path path = file.toPath();
                MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

                System.out.println("Đã lưu mã QR thành công tại: " + path.toAbsolutePath());

            } catch (Exception e) {
                System.err.println("[ERROR] Lỗi khi lưu file QR: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
