package ir.secondhand.frontend;

import ir.secondhand.frontend.util.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * نقطه شروع اپلیکیشن دسکتاپ JavaFX. صفحه ابتدایی سامانه، فهرست عمومی
 * آگهی‌های فعال (خانه) است تا کاربران مهمان نیز بتوانند بدون ورود آن را ببینند.
 */
public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        Navigator.init(primaryStage);
        Navigator.switchTo("/fxml/home.fxml", "بازار دست دوم");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
