package ir.secondhand.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdvertisementRejectRequest {

    @NotBlank(message = "دلیل رد آگهی الزامی است.")
    private String reason;
}
