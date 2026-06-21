package org.example.final_project.extension;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import org.example.final_project.NavigationManager;
import org.example.final_project.api.TransferHandling;

import java.math.BigDecimal;
import java.util.List;

public class BillController {

    @FXML private ComboBox<String> providerSelect;
    @FXML private TextField amountField;
    @FXML private Label lblMessage;

    // billType duoc truyen vao tu BillTypeController khi chuyen trang
    private String billType;
    private AppSession session;

    @FXML
    public void initialize() {
        this.session = AppSession.getInstance();

        // Chi cho nhap so trong amountField
        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*"))
                amountField.setText(newVal.replaceAll("[^\\d]", ""));
        });
    }

    /**
     * Goi ham nay sau khi load FXML de truyen loai hoa don vao.
     * Vi du: BillTypeController goi controller.setBillType("Dien")
     */
    public void setBillType(String billType) {
        this.billType = billType;
        System.out.println(billType);
        loadProviders();
    }

    private void loadProviders() {
        if (billType == null) return;

        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                return TransferHandling.getSupportedBillProviders(session.getAuthToken(), billType);
            }
        };

        task.setOnSucceeded(e -> {
            List<String> providers = task.getValue();
            providerSelect.getItems().clear();
            if (providers != null && !providers.isEmpty()) {
                providerSelect.getItems().addAll(providers);
                providerSelect.getSelectionModel().selectFirst();
            } else {
                lblMessage.setText("Khong co nha cung cap nao cho loai: " + billType);
            }
        });

        task.setOnFailed(e -> {
            lblMessage.setText("Loi tai danh sach nha cung cap.");
            task.getException().printStackTrace();
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void handleTransfer(ActionEvent event) {
        String provider = providerSelect.getValue();
        String amountRaw = amountField.getText().trim();
        String token = session.getAuthToken();

        if (provider == null || amountRaw.isEmpty()) {
            lblMessage.setText("Vui long chon nha cung cap va nhap so tien.");
            return;
        }

        if (token == null) {
            lblMessage.setText("Phien lam viec het han, vui long dang nhap lai.");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountRaw);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                lblMessage.setText("So tien phai lon hon 0.");
                return;
            }
        } catch (NumberFormatException e) {
            lblMessage.setText("So tien khong hop le.");
            return;
        }

        Task<Integer> payTask = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return TransferHandling.payBill(token, provider, amount);
            }
        };

        payTask.setOnSucceeded(e -> {
            int code = payTask.getValue();
            Alert alert = new Alert(
                    code == 1 ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR
            );
            alert.setTitle(code == 1 ? "Thanh toan thanh cong" : "Thanh toan that bai");
            alert.setHeaderText(null);
            alert.setContentText(TransferHandling.getResultMessage(code));
            alert.showAndWait().ifPresent(resp -> {
                if (code == 1 && resp == ButtonType.OK) goToMainPage();
                else if (code != 1)                     amountField.clear();
            });
        });

        payTask.setOnFailed(e -> {
            lblMessage.setText("Loi ket noi, vui long thu lai.");
            payTask.getException().printStackTrace();
        });

        Thread t = new Thread(payTask);
        t.setDaemon(true);
        t.start();
    }

    private void goToMainPage() {
        Stage stage = (Stage) amountField.getScene().getWindow();
        NavigationManager.setPrimaryStage(stage);
        NavigationManager.switchScene("/fxml/MainPage.fxml", "Main Page", "/css/shared.css", "/css/MainPage.css");
    }
}