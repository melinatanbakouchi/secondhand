package ir.secondhand.backend.repository;

import ir.secondhand.backend.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Favorite> findByUserIdAndAdvertisementId(Long userId, Long advertisementId);

    boolean existsByUserIdAndAdvertisementId(Long userId, Long advertisementId);
}
