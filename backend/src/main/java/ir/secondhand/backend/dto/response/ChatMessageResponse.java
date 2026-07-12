package ir.secondhand.backend.dto.response;

import ir.secondhand.backend.entity.ChatMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;

    public static ChatMessageResponse fromEntity(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setSenderId(message.getSender().getId());
        response.setSenderName(message.getSender().getFullName());
        response.setContent(message.getContent());
        response.setCreatedAt(message.getCreatedAt());
        return response;
    }
}
