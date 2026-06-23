/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

public class MyQRController {

    @FXML
    private Label navLogo;

    @FXML
    private ImageView qrCodeImageView; // ID phải khớp với file FXML

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
            String qrData = AccountHandling.getUserFromAccount(session.getAuthToken()).get("full_name").toString(); 
            
            // Gọi hàm tạo và hiển thị QR
            generateAndDisplayQRCode(qrData);
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
        System.out.println("Đang xử lý tải mã QR xuống thiết bị...");
        // Bạn có thể code thêm tính năng bật cửa sổ FileChooser để lưu file PNG tại đây
    }
}