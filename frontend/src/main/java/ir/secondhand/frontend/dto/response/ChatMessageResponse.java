package ir.secondhand.frontend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;
}
