package ir.secondhand.frontend.service;

import ir.secondhand.frontend.client.ApiClient;
import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.request.LoginRequest;
import ir.secondhand.frontend.dto.request.RegisterRequest;
import ir.secondhand.frontend.dto.response.AuthResponse;
import ir.secondhand.frontend.dto.response.UserResponse;
import ir.secondhand.frontend.session.SessionManager;

/**
 * عملیات ثبت‌نام و ورود؛ در صورت ورود موفق، اطلاعات نشست کاربر ذخیره می‌شود.
 */
public class AuthService {

    private final ApiClient apiClient = ApiClient.getInstance();

    public UserResponse register(RegisterRequest request) throws ApiException {
        return apiClient.post("/auth/register", request, UserResponse.class);
    }

    public AuthResponse login(String username, String password) throws ApiException {
        AuthResponse response = apiClient.post("/auth/login", new LoginRequest(username, password), AuthResponse.class);
        SessionManager.getInstance().setSession(
                response.getToken(), response.getUserId(), response.getUsername(),
                response.getFullName(), response.getRole());
        return response;
    }

    public void logout() {
        SessionManager.getInstance().clear();
    }
}
