package ir.secondhand.backend.exception;

/**
 * زمانی که کاربر تلاش کند داده‌ای را که باید یکتا باشد، تکراری ثبت کند.
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
