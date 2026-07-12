package ir.secondhand.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "نام کاربری الزامی است.")
    private String username;

    @NotBlank(message = "رمز عبور الزامی است.")
    private String password;
}
