package ir.secondhand.frontend.controller;

import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.service.AuthService;
import ir.secondhand.frontend.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void onLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("نام کاربری و رمز عبور را وارد کنید.");
            return;
        }

        try {
            authService.login(username, password);
            Navigator.switchTo("/fxml/home.fxml", "بازار دست دوم");
        } catch (ApiException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void onGoToRegister() {
        Navigator.switchTo("/fxml/register.fxml", "ثبت‌نام در سامانه");
    }

    @FXML
    private void onGuest() {
        Navigator.switchTo("/fxml/home.fxml", "بازار دست دوم");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}
