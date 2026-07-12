package ir.secondhand.backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingRequest {

    @NotNull(message = "شناسه آگهی الزامی است.")
    private Long advertisementId;

    @NotNull(message = "امتیاز الزامی است.")
    @Min(value = 1, message = "امتیاز باید بین ۱ تا ۵ باشد.")
    @Max(value = 5, message = "امتیاز باید بین ۱ تا ۵ باشد.")
    private Integer score;

    @Size(max = 500, message = "متن نظر طولانی است.")
    private String comment;
}
