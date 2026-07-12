package ir.secondhand.frontend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private Long cityId;
}
