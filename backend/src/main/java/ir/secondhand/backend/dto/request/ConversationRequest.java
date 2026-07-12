package ir.secondhand.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationRequest {

    @NotNull(message = "شناسه آگهی الزامی است.")
    private Long advertisementId;
}
