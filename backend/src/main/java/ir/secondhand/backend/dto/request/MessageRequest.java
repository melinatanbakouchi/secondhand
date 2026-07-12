package ir.secondhand.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {

    @NotBlank(message = "متن پیام نمی‌تواند خالی باشد.")
    @Size(max = 1000, message = "متن پیام طولانی است.")
    private String content;
}
