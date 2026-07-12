package ir.secondhand.backend.service;

import ir.secondhand.backend.entity.Advertisement;
import ir.secondhand.backend.entity.AdvertisementStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * ساخت پویای شرط‌های جست‌وجوی آگهی برای صفحه اصلی و نتایج جست‌وجو.
 */
public final class AdvertisementSpecifications {

    private AdvertisementSpecifications() {
    }

    public static Specification<Advertisement> hasStatus(AdvertisementStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Advertisement> titleContains(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Advertisement> hasCategory(Long categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Advertisement> hasCity(Long cityId) {
        return (root, query, cb) -> cb.equal(root.get("city").get("id"), cityId);
    }

    public static Specification<Advertisement> priceGreaterOrEqual(BigDecimal minPrice) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Advertisement> priceLessOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }
}
