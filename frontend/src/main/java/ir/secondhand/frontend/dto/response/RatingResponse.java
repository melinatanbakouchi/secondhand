package ir.secondhand.frontend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RatingResponse {
    private Long id;
    private Long buyerId;
    private String buyerName;
    private Long advertisementId;
    private String advertisementTitle;
    private int score;
    private String comment;
    private LocalDateTime createdAt;
}
