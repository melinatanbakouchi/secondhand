package ir.secondhand.frontend.util;

import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * مدیریت متمرکز تغییر صفحات JavaFX. تمام صفحات از راست به چپ (فارسی) و با
 * استایل مشترک بارگذاری می‌شوند.
 */
public final class Navigator {

    private static Stage primaryStage;
    private static final String STYLESHEET = "/css/style.css";
    private static Object payload;

    private Navigator() {
    }

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void switchTo(String fxmlPath, String title) {
        switchTo(fxmlPath, title, null);
    }

    public static void switchTo(String fxmlPath, String title, Object navigationPayload) {
        payload = navigationPayload;
        try {
            Parent root = loadFxml(fxmlPath);
            Scene scene = new Scene(root, 1100, 720);
            applyRtlAndStyle(scene);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("بارگذاری صفحه با خطا مواجه شد: " + fxmlPath, ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T consumePayload() {
        Object value = payload;
        payload = null;
        return (T) value;
    }

    public static <T> T load(String fxmlPath, Parent[] rootOut) {
        try {
            FXMLLoader loader = new FXMLLoader(resolve(fxmlPath));
            Parent root = loader.load();
            rootOut[0] = root;
            return loader.getController();
        } catch (IOException ex) {
            throw new IllegalStateException("بارگذاری کامپوننت با خطا مواجه شد: " + fxmlPath, ex);
        }
    }

    private static Parent loadFxml(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(resolve(fxmlPath));
        return loader.load();
    }

    private static URL resolve(String fxmlPath) {
        URL url = Navigator.class.getResource(fxmlPath);
        if (url == null) {
            throw new IllegalStateException("فایل FXML پیدا نشد: " + fxmlPath);
        }
        return url;
    }

    public static void applyRtlAndStyle(Scene scene) {
        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        URL cssUrl = Navigator.class.getResource(STYLESHEET);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }
}
