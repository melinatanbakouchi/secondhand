package ir.secondhand.frontend.client;

/**
 * خطای دریافت‌شده از Backend یا خطای شبکه؛ پیام آن مستقیما برای نمایش
 * به کاربر فارسی‌زبان مناسب است.
 */
public class ApiException extends Exception {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
