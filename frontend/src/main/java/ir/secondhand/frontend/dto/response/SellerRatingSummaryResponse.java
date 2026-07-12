package ir.secondhand.frontend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SellerRatingSummaryResponse {
    private double averageScore;
    private long ratingCount;
    private List<RatingResponse> ratings;
}
