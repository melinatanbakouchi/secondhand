package ir.secondhand.backend.repository;

import ir.secondhand.backend.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findBySellerIdOrderByCreatedAtDesc(Long sellerId);

    boolean existsByBuyerIdAndAdvertisementId(Long buyerId, Long advertisementId);

    long countBySellerId(Long sellerId);

    @Query("select coalesce(avg(r.score), 0) from Rating r where r.seller.id = :sellerId")
    double calculateAverageScoreForSeller(@Param("sellerId") Long sellerId);
}
