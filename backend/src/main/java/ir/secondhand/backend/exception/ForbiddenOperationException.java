package ir.secondhand.backend.exception;

/**
 * زمانی که کاربر واردشده اجازه انجام عملیات درخواستی را نداشته باشد.
 */
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
