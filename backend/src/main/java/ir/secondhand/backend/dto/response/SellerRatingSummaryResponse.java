package ir.secondhand.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SellerRatingSummaryResponse {
    private double averageScore;
    private long ratingCount;
    private List<RatingResponse> ratings;
}
