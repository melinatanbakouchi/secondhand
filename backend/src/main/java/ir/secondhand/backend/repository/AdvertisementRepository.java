package ir.secondhand.backend.repository;

import ir.secondhand.backend.entity.Advertisement;
import ir.secondhand.backend.entity.AdvertisementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long>, JpaSpecificationExecutor<Advertisement> {

    List<Advertisement> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    List<Advertisement> findByStatusOrderByCreatedAtAsc(AdvertisementStatus status);

    long countByStatus(AdvertisementStatus status);
}
