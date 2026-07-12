package ir.secondhand.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityRequest {

    @NotBlank(message = "نام شهر الزامی است.")
    private String name;
}
