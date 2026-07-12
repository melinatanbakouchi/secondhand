package ir.secondhand.backend.service;

import ir.secondhand.backend.dto.request.LoginRequest;
import ir.secondhand.backend.dto.request.RegisterRequest;
import ir.secondhand.backend.dto.response.AuthResponse;
import ir.secondhand.backend.dto.response.UserResponse;
import ir.secondhand.backend.entity.Role;
import ir.secondhand.backend.entity.User;
import ir.secondhand.backend.entity.UserStatus;
import ir.secondhand.backend.exception.BadRequestException;
import ir.secondhand.backend.exception.DuplicateResourceException;
import ir.secondhand.backend.exception.ForbiddenOperationException;
import ir.secondhand.backend.repository.UserRepository;
import ir.secondhand.backend.security.JwtUtil;
import ir.secondhand.backend.util.CurrentUserProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * منطق ثبت‌نام، ورود و دریافت کاربر جاری.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CurrentUserProvider currentUserProvider;

    public AuthService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtUtil jwtUtil,
                        CurrentUserProvider currentUserProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("رمز عبور و تکرار آن یکسان نیستند.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("این نام کاربری قبلا استفاده شده است.");
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("این شماره تماس قبلا ثبت شده است.");
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("این ایمیل قبلا استفاده شده است.");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail() == null || request.getEmail().isBlank() ? null : request.getEmail());
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("نام کاربری یا رمز عبور اشتباه است."));

        if (user.isBlocked()) {
            throw new ForbiddenOperationException("حساب کاربری شما مسدود شده است.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name(), user.getId());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getFullName(), user.getRole().name());
    }

    public UserResponse getCurrentUser() {
        return UserResponse.fromEntity(currentUserProvider.getCurrentUser());
    }
}
