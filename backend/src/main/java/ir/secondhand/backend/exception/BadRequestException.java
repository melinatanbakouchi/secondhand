package ir.secondhand.backend.exception;

/**
 * زمانی که درخواست ارسالی از نظر منطق کسب‌وکار نامعتبر باشد.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
