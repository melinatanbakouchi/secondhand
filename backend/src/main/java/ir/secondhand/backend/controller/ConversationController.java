package ir.secondhand.backend.controller;

import ir.secondhand.backend.dto.request.ConversationRequest;
import ir.secondhand.backend.dto.request.MessageRequest;
import ir.secondhand.backend.dto.response.ApiResponse;
import ir.secondhand.backend.dto.response.ChatMessageResponse;
import ir.secondhand.backend.dto.response.ConversationResponse;
import ir.secondhand.backend.service.ConversationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getMyConversations() {
        return ResponseEntity.ok(ApiResponse.success("لیست گفت‌وگوهای من", conversationService.getMyConversations()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ConversationResponse>> startConversation(@Valid @RequestBody ConversationRequest request) {
        ConversationResponse response = conversationService.startOrGetConversation(request.getAdvertisementId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("گفت‌وگو آماده است.", response));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("پیام‌های گفت‌وگو", conversationService.getMessages(id)));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessage(@PathVariable Long id,
                                                                          @Valid @RequestBody MessageRequest request) {
        ChatMessageResponse response = conversationService.sendMessage(id, request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("پیام ارسال شد.", response));
    }
}
