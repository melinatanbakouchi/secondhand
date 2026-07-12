package ir.secondhand.frontend.controller;

import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.request.RegisterRequest;
import ir.secondhand.frontend.service.AuthService;
import ir.secondhand.frontend.util.AlertHelper;
import ir.secondhand.frontend.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void onRegister() {
        String fullName = trimmed(fullNameField.getText());
        String username = trimmed(usernameField.getText());
        String phone = trimmed(phoneField.getText());
        String email = trimmed(emailField.getText());
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        String confirmPassword = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText();

        if (fullName.isEmpty() || username.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showError("لطفا تمام فیلدهای الزامی را پر کنید.");
            return;
        }

        try {
            authService.register(new RegisterRequest(fullName, username, password, confirmPassword, phone,
                    email.isEmpty() ? null : email));
            AlertHelper.showInfo("ثبت‌نام با موفقیت انجام شد. اکنون می‌توانید وارد شوید.");
            Navigator.switchTo("/fxml/login.fxml", "ورود به سامانه");
        } catch (ApiException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void onGoToLogin() {
        Navigator.switchTo("/fxml/login.fxml", "ورود به سامانه");
    }

    private String trimmed(String value) {
        return value == null ? "" : value.trim();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}
