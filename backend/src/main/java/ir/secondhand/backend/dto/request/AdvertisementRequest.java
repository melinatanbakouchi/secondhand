package ir.secondhand.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdvertisementRequest {

    @NotBlank(message = "عنوان آگهی الزامی است.")
    @Size(max = 150, message = "عنوان آگهی طولانی است.")
    private String title;

    @NotBlank(message = "توضیحات آگهی الزامی است.")
    @Size(max = 2000, message = "توضیحات آگهی طولانی است.")
    private String description;

    @NotNull(message = "قیمت آگهی الزامی است.")
    @DecimalMin(value = "0", inclusive = true, message = "قیمت آگهی نامعتبر است.")
    private BigDecimal price;

    @NotNull(message = "انتخاب دسته‌بندی الزامی است.")
    private Long categoryId;

    @NotNull(message = "انتخاب شهر الزامی است.")
    private Long cityId;
}
