package ir.secondhand.frontend.util;

import javafx.geometry.NodeOrientation;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * نمایش پیام‌های اطلاع‌رسانی، خطا و تایید به کاربر با چیدمان راست‌به‌چپ.
 */
public final class AlertHelper {

    private AlertHelper() {
    }

    public static void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "خطا", message);
    }

    public static void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "پیام سامانه", message);
    }

    public static void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "هشدار", message);
    }

    public static boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("تایید عملیات");
        alert.setHeaderText(null);
        alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        alert.showAndWait();
    }
}
