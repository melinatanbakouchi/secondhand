package ir.secondhand.backend.service;

import ir.secondhand.backend.dto.response.AdvertisementSummaryResponse;
import ir.secondhand.backend.entity.Advertisement;
import ir.secondhand.backend.entity.Favorite;
import ir.secondhand.backend.entity.User;
import ir.secondhand.backend.exception.DuplicateResourceException;
import ir.secondhand.backend.exception.ResourceNotFoundException;
import ir.secondhand.backend.repository.AdvertisementRepository;
import ir.secondhand.backend.repository.FavoriteRepository;
import ir.secondhand.backend.repository.RatingRepository;
import ir.secondhand.backend.util.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * لیست علاقه‌مندی‌های هر کاربر برای دسترسی سریع به آگهی‌های پسندیده‌شده.
 */
@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final AdvertisementRepository advertisementRepository;
    private final RatingRepository ratingRepository;
    private final CurrentUserProvider currentUserProvider;

    public FavoriteService(FavoriteRepository favoriteRepository,
                            AdvertisementRepository advertisementRepository,
                            RatingRepository ratingRepository,
                            CurrentUserProvider currentUserProvider) {
        this.favoriteRepository = favoriteRepository;
        this.advertisementRepository = advertisementRepository;
        this.ratingRepository = ratingRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly = true)
    public List<AdvertisementSummaryResponse> getMyFavorites() {
        Long userId = currentUserProvider.getCurrentUserId();
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(Favorite::getAdvertisement)
                .map(this::toSummaryResponse)
                .toList();
    }

    @Transactional
    public void addFavorite(Long advertisementId) {
        User currentUser = currentUserProvider.getCurrentUser();
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new ResourceNotFoundException("آگهی یافت نشد."));

        if (favoriteRepository.existsByUserIdAndAdvertisementId(currentUser.getId(), advertisementId)) {
            throw new DuplicateResourceException("این آگهی قبلا به علاقه‌مندی‌ها اضافه شده است.");
        }
        favoriteRepository.save(new Favorite(currentUser, advertisement));
    }

    @Transactional
    public void removeFavorite(Long advertisementId) {
        Long userId = currentUserProvider.getCurrentUserId();
        Favorite favorite = favoriteRepository.findByUserIdAndAdvertisementId(userId, advertisementId)
                .orElseThrow(() -> new ResourceNotFoundException("این آگهی در لیست علاقه‌مندی‌های شما نیست."));
        favoriteRepository.delete(favorite);
    }

    public boolean isFavorite(Long advertisementId) {
        Long userId = currentUserProvider.getCurrentUserId();
        return favoriteRepository.existsByUserIdAndAdvertisementId(userId, advertisementId);
    }

    private AdvertisementSummaryResponse toSummaryResponse(Advertisement advertisement) {
        Long sellerId = advertisement.getOwner().getId();
        double avgRating = ratingRepository.calculateAverageScoreForSeller(sellerId);
        long ratingCount = ratingRepository.countBySellerId(sellerId);
        return AdvertisementSummaryResponse.fromEntity(advertisement, avgRating, ratingCount);
    }
}
