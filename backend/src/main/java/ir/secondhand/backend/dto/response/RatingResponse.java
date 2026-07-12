package ir.secondhand.backend.dto.response;

import ir.secondhand.backend.entity.Rating;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RatingResponse {
    private Long id;
    private Long buyerId;
    private String buyerName;
    private Long advertisementId;
    private String advertisementTitle;
    private int score;
    private String comment;
    private LocalDateTime createdAt;

    public static RatingResponse fromEntity(Rating rating) {
        RatingResponse response = new RatingResponse();
        response.setId(rating.getId());
        response.setBuyerId(rating.getBuyer().getId());
        response.setBuyerName(rating.getBuyer().getFullName());
        response.setAdvertisementId(rating.getAdvertisement().getId());
        response.setAdvertisementTitle(rating.getAdvertisement().getTitle());
        response.setScore(rating.getScore());
        response.setComment(rating.getComment());
        response.setCreatedAt(rating.getCreatedAt());
        return response;
    }
}
