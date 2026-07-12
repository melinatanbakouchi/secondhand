package ir.secondhand.backend.service;

import ir.secondhand.backend.dto.response.RatingResponse;
import ir.secondhand.backend.dto.response.SellerRatingSummaryResponse;
import ir.secondhand.backend.entity.Advertisement;
import ir.secondhand.backend.entity.Rating;
import ir.secondhand.backend.entity.User;
import ir.secondhand.backend.exception.BadRequestException;
import ir.secondhand.backend.exception.DuplicateResourceException;
import ir.secondhand.backend.exception.ResourceNotFoundException;
import ir.secondhand.backend.repository.AdvertisementRepository;
import ir.secondhand.backend.repository.RatingRepository;
import ir.secondhand.backend.util.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * امتیازدهی خریداران به فروشندگان پس از تعامل درباره یک آگهی مشخص.
 */
@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final AdvertisementRepository advertisementRepository;
    private final CurrentUserProvider currentUserProvider;

    public RatingService(RatingRepository ratingRepository,
                          AdvertisementRepository advertisementRepository,
                          CurrentUserProvider currentUserProvider) {
        this.ratingRepository = ratingRepository;
        this.advertisementRepository = advertisementRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public RatingResponse submitRating(Long advertisementId, int score, String comment) {
        User buyer = currentUserProvider.getCurrentUser();
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new ResourceNotFoundException("آگهی یافت نشد."));

        if (advertisement.isOwnedBy(buyer.getId())) {
            throw new BadRequestException("امکان امتیازدهی به آگهی خودتان وجود ندارد.");
        }
        if (ratingRepository.existsByBuyerIdAndAdvertisementId(buyer.getId(), advertisementId)) {
            throw new DuplicateResourceException("شما قبلا برای این آگهی امتیاز ثبت کرده‌اید.");
        }

        Rating rating = new Rating();
        rating.setBuyer(buyer);
        rating.setSeller(advertisement.getOwner());
        rating.setAdvertisement(advertisement);
        rating.setScore(score);
        rating.setComment(comment);

        ratingRepository.save(rating);
        return RatingResponse.fromEntity(rating);
    }

    @Transactional(readOnly = true)
    public SellerRatingSummaryResponse getSellerRatingSummary(Long sellerId) {
        SellerRatingSummaryResponse response = new SellerRatingSummaryResponse();
        response.setAverageScore(ratingRepository.calculateAverageScoreForSeller(sellerId));
        response.setRatingCount(ratingRepository.countBySellerId(sellerId));
        response.setRatings(ratingRepository.findBySellerIdOrderByCreatedAtDesc(sellerId).stream()
                .map(RatingResponse::fromEntity)
                .toList());
        return response;
    }
}
