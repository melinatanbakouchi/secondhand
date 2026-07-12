package ir.secondhand.backend.exception;

/**
 * زمانی که موجودیت درخواستی در پایگاه داده یافت نشود، این خطا صادر می‌شود.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
