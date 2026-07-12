package ir.secondhand.frontend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
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
}
