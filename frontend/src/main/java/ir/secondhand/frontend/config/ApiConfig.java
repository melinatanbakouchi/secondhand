package ir.secondhand.frontend.config;

/**
 * تنظیمات آدرس سرویس Backend. Frontend هرگز مستقیم به دیتابیس متصل نمی‌شود
 * و تمام ارتباطات صرفا از طریق این REST API انجام می‌گیرد.
 */
public final class ApiConfig {

    public static final String BASE_URL = "http://localhost:8083/api";
    public static final String FILES_BASE_URL = "http://localhost:8083";

    private ApiConfig() {
    }
}
