package ir.secondhand.backend.repository;

import ir.secondhand.backend.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByBuyerIdAndSellerIdAndAdvertisementId(Long buyerId, Long sellerId, Long advertisementId);

    List<Conversation> findByBuyerIdOrSellerIdOrderByCreatedAtDesc(Long buyerId, Long sellerId);
}
