package org.example.final_project.extension;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.example.final_project.AppSession;
import org.example.final_project.NavigationManager;
import org.example.final_project.api.TransferHandling;

import java.math.BigDecimal;
import java.util.List;

public class TransferController {

    @FXML private ComboBox<String> bankSelect;
    @FXML private TextField accountNumberField;
    @FXML private TextField descriptionField;
    @FXML private TextField amountField;

    private AppSession session;

    @FXML
    public void initialize() {
        this.session = AppSession.getInstance();

        // Load danh sach ngan hang tren background thread
        Task<List<String>> loadBanksTask = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                return TransferHandling.getSupportedBanks(session.getAuthToken());
            }
        };

        loadBanksTask.setOnSucceeded(event -> {
            List<String> banks = loadBanksTask.getValue();
            if (banks != null && !banks.isEmpty()) {
                bankSelect.getItems().addAll(banks);
                bankSelect.getSelectionModel().selectFirst();
            }
        });

        loadBanksTask.setOnFailed(event -> {
            bankSelect.setPromptText("Khong the tai danh sach ngan hang");
            loadBanksTask.getException().printStackTrace();
        });

        Thread t = new Thread(loadBanksTask);
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void handleTransfer(ActionEvent event) {
        String selectedBank   = bankSelect.getValue();
        String destAccount    = accountNumberField.getText().trim();
        String amountRaw      = amountField.getText().trim();
        String description    = descriptionField.getText().trim();
        String token          = session.getAuthToken();

        if (destAccount.isEmpty() || amountRaw.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Giao dich that bai", "Vui long dien day du thong tin chuyen khoan.");
            return;
        }

        if (token == null) {
            showAlert(Alert.AlertType.ERROR, "Phien lam viec het han", "Vui long dang nhap lai.");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountRaw);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert(Alert.AlertType.ERROR, "So tien khong hop le", "So tien phai lon hon 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "So tien khong hop le", "Vui long chi nhap so.");
            return;
        }

        Task<Integer> transferTask = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                if ("36 BANK".equalsIgnoreCase(selectedBank)) {
                    // Chuyen khoan noi bo: chi can token + so tk dich
                    return TransferHandling.inBankTransfer(token, destAccount, amount, description);
                } else {
                    // Chuyen lien ngan hang: truyen ten ngan hang + so tk ben ngoai
                    return TransferHandling.outBankTransfer(token, selectedBank, destAccount, amount, description);
                }
            }
        };

        transferTask.setOnSucceeded(e -> {
            int code = transferTask.getValue();
            if (code == 1) {
                showAlert(Alert.AlertType.INFORMATION, "Giao dich thanh cong",
                        TransferHandling.getResultMessage(code))
                        .ifPresent(resp -> {
                            if (resp == ButtonType.OK) goToMainPage();
                        });
            } else {
                showAlert(Alert.AlertType.ERROR, "Giao dich that bai",
                        TransferHandling.getResultMessage(code))
                        .ifPresent(resp -> resetForm());
            }
        });

        transferTask.setOnFailed(e -> {
            showAlert(Alert.AlertType.ERROR, "Loi he thong", "Vui long kiem tra ket noi mang.");
            transferTask.getException().printStackTrace();
        });

        Thread t = new Thread(transferTask);
        t.setDaemon(true);
        t.start();
    }

    private void resetForm() {
        accountNumberField.clear();
        amountField.clear();
        descriptionField.clear();
        if (!bankSelect.getItems().isEmpty())
            bankSelect.getSelectionModel().selectFirst();
    }
    @FXML
    private Label navLogo;

    // 2. The method triggered by clicking the Logo (or the Home button)
    @FXML
    private void handleNavHome() {
        switchTo("/fxml/MainPage.fxml", "Main Page", "/css/MainPage.css");
    }

    // 3. The helper method that actually performs the transition
    private void switchTo(String fxml, String title, String css) {
        // Get the current window using the navLogo
        Stage stage = (Stage) navLogo.getScene().getWindow();
        NavigationManager.setPrimaryStage(stage);

        // Tell the NavigationManager to switch the scene
        NavigationManager.switchScene(fxml, title, "/css/shared.css", css);
    }

    private void goToMainPage() {
        Stage stage = (Stage) amountField.getScene().getWindow();
        NavigationManager.setPrimaryStage(stage);
        NavigationManager.switchScene("/fxml/MainPage.fxml", "Main Page", "/css/shared.css", "/css/MainPage.css");
    }

    private java.util.Optional<ButtonType> showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}