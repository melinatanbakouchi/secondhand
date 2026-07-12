package ir.secondhand.backend.util;

import ir.secondhand.backend.entity.User;
import ir.secondhand.backend.exception.ResourceNotFoundException;
import ir.secondhand.backend.repository.UserRepository;
import ir.secondhand.backend.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * استخراج کاربر واردشده جاری از SecurityContext؛ به این ترتیب هیچ کنترلر یا
 * سرویسی مجبور نیست مستقیم به کلاس‌های Spring Security وابسته باشد و شناسه
 * مالک همیشه از توکن معتبر خوانده می‌شود، نه از بدنه درخواست.
 */
@Component
public class CurrentUserProvider {

    private final UserRepository userRepository;

    public CurrentUserProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ResourceNotFoundException("کاربر وارد نشده است.");
        }
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("کاربر پیدا نشد."));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
