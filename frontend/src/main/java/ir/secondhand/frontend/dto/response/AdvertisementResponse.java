package ir.secondhand.frontend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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
}
