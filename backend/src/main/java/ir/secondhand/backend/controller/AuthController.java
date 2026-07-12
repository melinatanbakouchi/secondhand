package ir.secondhand.backend.controller;

import ir.secondhand.backend.dto.request.LoginRequest;
import ir.secondhand.backend.dto.request.RegisterRequest;
import ir.secondhand.backend.dto.response.ApiResponse;
import ir.secondhand.backend.dto.response.AuthResponse;
import ir.secondhand.backend.dto.response.UserResponse;
import ir.secondhand.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("ثبت‌نام با موفقیت انجام شد.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("ورود با موفقیت انجام شد.", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse response = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("اطلاعات کاربر جاری", response));
    }
}
