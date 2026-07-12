package ir.secondhand.frontend;

/**
 * نقطه ورود مستقل از JavaFX Application برای اجرای jar نهایی بدون نیاز به
 * module-path؛ این کلاس فقط MainApplication را اجرا می‌کند.
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        MainApplication.main(args);
    }
}
