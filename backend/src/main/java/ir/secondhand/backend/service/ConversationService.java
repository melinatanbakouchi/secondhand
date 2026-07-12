package ir.secondhand.backend.service;

import ir.secondhand.backend.dto.response.ChatMessageResponse;
import ir.secondhand.backend.dto.response.ConversationResponse;
import ir.secondhand.backend.entity.Advertisement;
import ir.secondhand.backend.entity.ChatMessage;
import ir.secondhand.backend.entity.Conversation;
import ir.secondhand.backend.entity.User;
import ir.secondhand.backend.exception.BadRequestException;
import ir.secondhand.backend.exception.ForbiddenOperationException;
import ir.secondhand.backend.exception.ResourceNotFoundException;
import ir.secondhand.backend.repository.AdvertisementRepository;
import ir.secondhand.backend.repository.ChatMessageRepository;
import ir.secondhand.backend.repository.ConversationRepository;
import ir.secondhand.backend.util.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * گفت‌وگوی خریدار و فروشنده درباره یک آگهی مشخص، به همراه پیام‌های متنی آن.
 */
@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AdvertisementRepository advertisementRepository;
    private final CurrentUserProvider currentUserProvider;

    public ConversationService(ConversationRepository conversationRepository,
                                ChatMessageRepository chatMessageRepository,
                                AdvertisementRepository advertisementRepository,
                                CurrentUserProvider currentUserProvider) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.advertisementRepository = advertisementRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public ConversationResponse startOrGetConversation(Long advertisementId) {
        User buyer = currentUserProvider.getCurrentUser();
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new ResourceNotFoundException("آگهی یافت نشد."));

        if (advertisement.isOwnedBy(buyer.getId())) {
            throw new BadRequestException("امکان گفت‌وگو با خودتان درباره آگهی خودتان وجود ندارد.");
        }

        User seller = advertisement.getOwner();
        Conversation conversation = conversationRepository
                .findByBuyerIdAndSellerIdAndAdvertisementId(buyer.getId(), seller.getId(), advertisementId)
                .orElseGet(() -> conversationRepository.save(new Conversation(buyer, seller, advertisement)));

        return toResponse(conversation, buyer.getId());
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getMyConversations() {
        Long userId = currentUserProvider.getCurrentUserId();
        return conversationRepository.findByBuyerIdOrSellerIdOrderByCreatedAtDesc(userId, userId).stream()
                .map(conversation -> toResponse(conversation, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long conversationId) {
        Conversation conversation = findConversationOrThrow(conversationId);
        Long userId = currentUserProvider.getCurrentUserId();
        ensureParticipant(conversation, userId);

        return chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(ChatMessageResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ChatMessageResponse sendMessage(Long conversationId, String content) {
        Conversation conversation = findConversationOrThrow(conversationId);
        User sender = currentUserProvider.getCurrentUser();
        ensureParticipant(conversation, sender.getId());

        ChatMessage message = new ChatMessage(conversation, sender, content);
        chatMessageRepository.save(message);
        return ChatMessageResponse.fromEntity(message);
    }

    private Conversation findConversationOrThrow(Long id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("گفت‌وگو یافت نشد."));
    }

    private void ensureParticipant(Conversation conversation, Long userId) {
        if (!conversation.hasParticipant(userId)) {
            throw new ForbiddenOperationException("شما عضو این گفت‌وگو نیستید.");
        }
    }

    private ConversationResponse toResponse(Conversation conversation, Long currentUserId) {
        ConversationResponse response = new ConversationResponse();
        response.setId(conversation.getId());
        response.setAdvertisementId(conversation.getAdvertisement().getId());
        response.setAdvertisementTitle(conversation.getAdvertisement().getTitle());
        response.setBuyerId(conversation.getBuyer().getId());
        response.setBuyerName(conversation.getBuyer().getFullName());
        response.setSellerId(conversation.getSeller().getId());
        response.setSellerName(conversation.getSeller().getFullName());

        boolean isBuyer = conversation.getBuyer().getId().equals(currentUserId);
        response.setOtherPartyId(isBuyer ? conversation.getSeller().getId() : conversation.getBuyer().getId());
        response.setOtherPartyName(isBuyer ? conversation.getSeller().getFullName() : conversation.getBuyer().getFullName());

        List<ChatMessage> messages = conversation.getMessages();
        if (!messages.isEmpty()) {
            ChatMessage lastMessage = messages.get(messages.size() - 1);
            response.setLastMessage(lastMessage.getContent());
            response.setLastMessageAt(lastMessage.getCreatedAt());
        }
        response.setCreatedAt(conversation.getCreatedAt());
        return response;
    }
}
