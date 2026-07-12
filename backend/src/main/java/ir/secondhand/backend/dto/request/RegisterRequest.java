package ir.secondhand.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "نام و نام خانوادگی الزامی است.")
    @Size(max = 100, message = "نام و نام خانوادگی طولانی است.")
    private String fullName;

    @NotBlank(message = "نام کاربری الزامی است.")
    @Size(min = 3, max = 50, message = "نام کاربری باید بین ۳ تا ۵۰ کاراکتر باشد.")
    private String username;

    @NotBlank(message = "رمز عبور الزامی است.")
    @Size(min = 6, message = "رمز عبور باید حداقل ۶ کاراکتر باشد.")
    private String password;

    @NotBlank(message = "تکرار رمز عبور الزامی است.")
    private String confirmPassword;

    @NotBlank(message = "شماره تماس الزامی است.")
    @Pattern(regexp = "^09\\d{9}$", message = "شماره تماس باید به فرمت صحیح باشد (مثال: 09123456789).")
    private String phoneNumber;

    @Email(message = "ایمیل وارد شده معتبر نیست.")
    private String email;
}
