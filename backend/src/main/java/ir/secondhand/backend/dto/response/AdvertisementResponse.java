package ir.secondhand.backend.dto.response;

import ir.secondhand.backend.entity.Advertisement;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * نمایش کامل جزئیات یک آگهی.
 */
@Getter
@Setter
public class AdvertisementResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String status;
    private String rejectionReason;

    private Long categoryId;
    private String categoryTitle;

    private Long cityId;
    private String cityName;

    private Long ownerId;
    private String ownerFullName;
    private String ownerPhoneNumber;

    private double sellerAverageRating;
    private long sellerRatingCount;

    private List<AdvertisementImageResponse> images;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AdvertisementResponse fromEntity(Advertisement ad, double sellerAverageRating, long sellerRatingCount) {
        AdvertisementResponse response = new AdvertisementResponse();
        response.setId(ad.getId());
        response.setTitle(ad.getTitle());
        response.setDescription(ad.getDescription());
        response.setPrice(ad.getPrice());
        response.setStatus(ad.getStatus().name());
        response.setRejectionReason(ad.getRejectionReason());

        response.setCategoryId(ad.getCategory().getId());
        response.setCategoryTitle(ad.getCategory().getTitle());

        response.setCityId(ad.getCity().getId());
        response.setCityName(ad.getCity().getName());

        response.setOwnerId(ad.getOwner().getId());
        response.setOwnerFullName(ad.getOwner().getFullName());
        response.setOwnerPhoneNumber(ad.getOwner().getPhoneNumber());

        response.setSellerAverageRating(sellerAverageRating);
        response.setSellerRatingCount(sellerRatingCount);

        response.setImages(ad.getImages().stream().map(AdvertisementImageResponse::fromEntity).toList());

        response.setCreatedAt(ad.getCreatedAt());
        response.setUpdatedAt(ad.getUpdatedAt());
        return response;
    }
}
