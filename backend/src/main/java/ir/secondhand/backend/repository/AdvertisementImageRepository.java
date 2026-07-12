package ir.secondhand.backend.repository;

import ir.secondhand.backend.entity.AdvertisementImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementImageRepository extends JpaRepository<AdvertisementImage, Long> {
}
