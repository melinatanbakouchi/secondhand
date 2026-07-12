package ir.secondhand.frontend.service;

import ir.secondhand.frontend.client.ApiClient;
import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.request.ConversationRequest;
import ir.secondhand.frontend.dto.request.MessageRequest;
import ir.secondhand.frontend.dto.response.ChatMessageResponse;
import ir.secondhand.frontend.dto.response.ConversationResponse;

import java.util.List;

public class ConversationService {

    private final ApiClient apiClient = ApiClient.getInstance();

    public List<ConversationResponse> getMyConversations() throws ApiException {
        return apiClient.get("/conversations", apiClient.getMapper().getTypeFactory()
                .constructCollectionType(List.class, ConversationResponse.class));
    }

    public ConversationResponse startConversation(Long advertisementId) throws ApiException {
        return apiClient.post("/conversations", new ConversationRequest(advertisementId), ConversationResponse.class);
    }

    public List<ChatMessageResponse> getMessages(Long conversationId) throws ApiException {
        return apiClient.get("/conversations/" + conversationId + "/messages", apiClient.getMapper().getTypeFactory()
                .constructCollectionType(List.class, ChatMessageResponse.class));
    }

    public ChatMessageResponse sendMessage(Long conversationId, String content) throws ApiException {
        return apiClient.post("/conversations/" + conversationId + "/messages",
                new MessageRequest(content), ChatMessageResponse.class);
    }
}
