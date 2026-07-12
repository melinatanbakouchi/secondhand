package ir.secondhand.frontend.session;

/**
 * نگهداری وضعیت ورود کاربر جاری (توکن JWT و اطلاعات پایه) در حافظه اپلیکیشن.
 */
public final class SessionManager {

    private static SessionManager instance;

    private String token;
    private Long userId;
    private String username;
    private String fullName;
    private String role;

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setSession(String token, Long userId, String username, String fullName, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public void clear() {
        token = null;
        userId = null;
        username = null;
        fullName = null;
        role = null;
    }

    public boolean isLoggedIn() {
        return token != null;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}
