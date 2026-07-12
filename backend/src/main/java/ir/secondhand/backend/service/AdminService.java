package ir.secondhand.backend.service;

import ir.secondhand.backend.dto.response.AdminStatsResponse;
import ir.secondhand.backend.dto.response.UserResponse;
import ir.secondhand.backend.entity.AdvertisementStatus;
import ir.secondhand.backend.entity.User;
import ir.secondhand.backend.entity.UserStatus;
import ir.secondhand.backend.exception.BadRequestException;
import ir.secondhand.backend.exception.ResourceNotFoundException;
import ir.secondhand.backend.repository.AdvertisementRepository;
import ir.secondhand.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * عملیات مخصوص مدیر سیستم: مدیریت کاربران و مشاهده آمار کلی سامانه.
 * تایید/رد آگهی از طریق AdvertisementService انجام می‌شود.
 */
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;

    public AdminService(UserRepository userRepository, AdvertisementRepository advertisementRepository) {
        this.userRepository = userRepository;
        this.advertisementRepository = advertisementRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @Transactional
    public UserResponse blockUser(Long userId) {
        User user = findUserOrThrow(userId);
        if (user.isAdmin()) {
            throw new BadRequestException("امکان مسدود کردن مدیر سیستم وجود ندارد.");
        }
        user.setStatus(UserStatus.BLOCKED);
        userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse unblockUser(Long userId) {
        User user = findUserOrThrow(userId);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long blockedUsers = userRepository.findAll().stream()
                .filter(User::isBlocked)
                .count();
        long totalAdvertisements = advertisementRepository.count();
        long pendingAdvertisements = advertisementRepository.countByStatus(AdvertisementStatus.PENDING);
        long activeAdvertisements = advertisementRepository.countByStatus(AdvertisementStatus.ACTIVE);
        long soldAdvertisements = advertisementRepository.countByStatus(AdvertisementStatus.SOLD);

        return new AdminStatsResponse(totalUsers, blockedUsers, totalAdvertisements,
                pendingAdvertisements, activeAdvertisements, soldAdvertisements);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("کاربر یافت نشد."));
    }
}
