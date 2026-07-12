package ir.secondhand.backend.dto.response;

import ir.secondhand.backend.entity.Advertisement;
import ir.secondhand.backend.entity.AdvertisementImage;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * نمایش خلاصه یک آگهی، مناسب برای لیست‌ها و کارت‌های آگهی.
 */
@Getter
@Setter
public class AdvertisementSummaryResponse {
    private Long id;
    private String title;
    private BigDecimal price;
    private String status;
    private String cityName;
    private String categoryTitle;
    private String coverImagePath;
    private double averageRating;
    private long ratingCount;
    private LocalDateTime createdAt;

    public static AdvertisementSummaryResponse fromEntity(Advertisement ad, double averageRating, long ratingCount) {
        AdvertisementSummaryResponse response = new AdvertisementSummaryResponse();
        response.setId(ad.getId());
        response.setTitle(ad.getTitle());
        response.setPrice(ad.getPrice());
        response.setStatus(ad.getStatus().name());
        response.setCityName(ad.getCity().getName());
        response.setCategoryTitle(ad.getCategory().getTitle());
        response.setCoverImagePath(ad.getImages().stream()
                .min(Comparator.comparingInt(AdvertisementImage::getDisplayOrder))
                .map(AdvertisementImage::getImagePath)
                .orElse(null));
        response.setAverageRating(averageRating);
        response.setRatingCount(ratingCount);
        response.setCreatedAt(ad.getCreatedAt());
        return response;
    }
}
