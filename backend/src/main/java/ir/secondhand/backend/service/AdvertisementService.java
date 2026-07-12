package ir.secondhand.backend.service;

import ir.secondhand.backend.dto.request.AdvertisementRequest;
import ir.secondhand.backend.dto.response.AdvertisementResponse;
import ir.secondhand.backend.dto.response.AdvertisementSummaryResponse;
import ir.secondhand.backend.entity.*;
import ir.secondhand.backend.exception.BadRequestException;
import ir.secondhand.backend.exception.ForbiddenOperationException;
import ir.secondhand.backend.exception.ResourceNotFoundException;
import ir.secondhand.backend.repository.AdvertisementRepository;
import ir.secondhand.backend.repository.CategoryRepository;
import ir.secondhand.backend.repository.CityRepository;
import ir.secondhand.backend.repository.RatingRepository;
import ir.secondhand.backend.util.CurrentUserProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * قلب سامانه: مدیریت کامل چرخه‌ی حیات آگهی از ثبت تا فروش یا حذف.
 */
@Service
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;
    private final RatingRepository ratingRepository;
    private final ImageStorageService imageStorageService;
    private final CurrentUserProvider currentUserProvider;

    public AdvertisementService(AdvertisementRepository advertisementRepository,
                                 CategoryRepository categoryRepository,
                                 CityRepository cityRepository,
                                 RatingRepository ratingRepository,
                                 ImageStorageService imageStorageService,
                                 CurrentUserProvider currentUserProvider) {
        this.advertisementRepository = advertisementRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
        this.ratingRepository = ratingRepository;
        this.imageStorageService = imageStorageService;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public AdvertisementResponse createAdvertisement(AdvertisementRequest request) {
        User owner = currentUserProvider.getCurrentUser();
        Category category = findCategoryOrThrow(request.getCategoryId());
        City city = findCityOrThrow(request.getCityId());

        Advertisement advertisement = new Advertisement();
        advertisement.setTitle(request.getTitle());
        advertisement.setDescription(request.getDescription());
        advertisement.setPrice(request.getPrice());
        advertisement.setCategory(category);
        advertisement.setCity(city);
        advertisement.setOwner(owner);
        advertisement.setStatus(AdvertisementStatus.PENDING);

        advertisementRepository.save(advertisement);
        return toFullResponse(advertisement);
    }

    @Transactional
    public AdvertisementResponse updateAdvertisement(Long id, AdvertisementRequest request) {
        Advertisement advertisement = findAdvertisementOrThrow(id);
        User currentUser = currentUserProvider.getCurrentUser();
        ensureOwner(advertisement, currentUser);

        if (advertisement.getStatus() == AdvertisementStatus.SOLD
                || advertisement.getStatus() == AdvertisementStatus.DELETED) {
            throw new BadRequestException("امکان ویرایش آگهی فروخته‌شده یا حذف‌شده وجود ندارد.");
        }

        advertisement.setTitle(request.getTitle());
        advertisement.setDescription(request.getDescription());
        advertisement.setPrice(request.getPrice());
        advertisement.setCategory(findCategoryOrThrow(request.getCategoryId()));
        advertisement.setCity(findCityOrThrow(request.getCityId()));
        advertisement.setStatus(AdvertisementStatus.PENDING);
        advertisement.setRejectionReason(null);

        advertisementRepository.save(advertisement);
        return toFullResponse(advertisement);
    }

    @Transactional
    public void addImage(Long advertisementId, MultipartFile file) {
        Advertisement advertisement = findAdvertisementOrThrow(advertisementId);
        User currentUser = currentUserProvider.getCurrentUser();
        ensureOwner(advertisement, currentUser);

        if (advertisement.getStatus() == AdvertisementStatus.SOLD
                || advertisement.getStatus() == AdvertisementStatus.DELETED) {
            throw new BadRequestException("امکان افزودن تصویر به این آگهی وجود ندارد.");
        }

        String imagePath = imageStorageService.store(file);
        int nextOrder = advertisement.getImages().size();
        advertisement.addImage(new ir.secondhand.backend.entity.AdvertisementImage(imagePath, nextOrder));
        advertisementRepository.save(advertisement);
    }

    @Transactional
    public void deleteImage(Long advertisementId, Long imageId) {
        Advertisement advertisement = findAdvertisementOrThrow(advertisementId);
        User currentUser = currentUserProvider.getCurrentUser();
        ensureOwner(advertisement, currentUser);

        boolean removed = advertisement.getImages()
                .removeIf(image -> image.getId().equals(imageId));
        if (!removed) {
            throw new ResourceNotFoundException("این تصویر برای آگهی یافت نشد.");
        }
        advertisementRepository.save(advertisement);
    }

    @Transactional
    public void deleteAdvertisement(Long id) {
        Advertisement advertisement = findAdvertisementOrThrow(id);
        User currentUser = currentUserProvider.getCurrentUser();
        if (!advertisement.isOwnedBy(currentUser.getId()) && !currentUser.isAdmin()) {
            throw new ForbiddenOperationException("اجازه حذف این آگهی را ندارید.");
        }
        advertisement.setStatus(AdvertisementStatus.DELETED);
        advertisementRepository.save(advertisement);
    }

    @Transactional
    public AdvertisementResponse markAsSold(Long id) {
        Advertisement advertisement = findAdvertisementOrThrow(id);
        User currentUser = currentUserProvider.getCurrentUser();
        ensureOwner(advertisement, currentUser);

        if (advertisement.getStatus() != AdvertisementStatus.ACTIVE) {
            throw new BadRequestException("فقط آگهی فعال قابل تبدیل به فروخته‌شده است.");
        }
        advertisement.setStatus(AdvertisementStatus.SOLD);
        advertisementRepository.save(advertisement);
        return toFullResponse(advertisement);
    }

    @Transactional
    public AdvertisementResponse approveAdvertisement(Long id) {
        Advertisement advertisement = findAdvertisementOrThrow(id);
        if (advertisement.getStatus() != AdvertisementStatus.PENDING) {
            throw new BadRequestException("فقط آگهی در وضعیت در انتظار بررسی، قابل تایید است.");
        }
        advertisement.setStatus(AdvertisementStatus.ACTIVE);
        advertisement.setRejectionReason(null);
        advertisementRepository.save(advertisement);
        return toFullResponse(advertisement);
    }

    @Transactional
    public AdvertisementResponse rejectAdvertisement(Long id, String reason) {
        Advertisement advertisement = findAdvertisementOrThrow(id);
        if (advertisement.getStatus() != AdvertisementStatus.PENDING) {
            throw new BadRequestException("فقط آگهی در وضعیت در انتظار بررسی، قابل رد است.");
        }
        advertisement.setStatus(AdvertisementStatus.REJECTED);
        advertisement.setRejectionReason(reason);
        advertisementRepository.save(advertisement);
        return toFullResponse(advertisement);
    }

    @Transactional(readOnly = true)
    public AdvertisementResponse getAdvertisementById(Long id) {
        Advertisement advertisement = findAdvertisementOrThrow(id);

        boolean isPubliclyVisible = advertisement.getStatus() == AdvertisementStatus.ACTIVE
                || advertisement.getStatus() == AdvertisementStatus.SOLD;

        if (!isPubliclyVisible) {
            User currentUser = tryGetCurrentUser();
            boolean isOwnerOrAdmin = currentUser != null
                    && (advertisement.isOwnedBy(currentUser.getId()) || currentUser.isAdmin());
            if (!isOwnerOrAdmin) {
                throw new ResourceNotFoundException("آگهی یافت نشد.");
            }
        }
        return toFullResponse(advertisement);
    }

    @Transactional(readOnly = true)
    public List<AdvertisementSummaryResponse> searchAdvertisements(String keyword, Long categoryId, Long cityId,
                                                                     BigDecimal minPrice, BigDecimal maxPrice) {
        Specification<Advertisement> spec = AdvertisementSpecifications.hasStatus(AdvertisementStatus.ACTIVE);

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(AdvertisementSpecifications.titleContains(keyword.trim()));
        }
        if (categoryId != null) {
            spec = spec.and(AdvertisementSpecifications.hasCategory(categoryId));
        }
        if (cityId != null) {
            spec = spec.and(AdvertisementSpecifications.hasCity(cityId));
        }
        if (minPrice != null) {
            spec = spec.and(AdvertisementSpecifications.priceGreaterOrEqual(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(AdvertisementSpecifications.priceLessOrEqual(maxPrice));
        }

        return advertisementRepository.findAll(spec, org.springframework.data.domain.Sort.by("createdAt").descending())
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdvertisementSummaryResponse> getMyAdvertisements() {
        Long userId = currentUserProvider.getCurrentUserId();
        return advertisementRepository.findByOwnerIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdvertisementSummaryResponse> getPendingAdvertisements() {
        return advertisementRepository.findByStatusOrderByCreatedAtAsc(AdvertisementStatus.PENDING).stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    Advertisement findAdvertisementOrThrow(Long id) {
        return advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("آگهی یافت نشد."));
    }

    private void ensureOwner(Advertisement advertisement, User currentUser) {
        if (!advertisement.isOwnedBy(currentUser.getId())) {
            throw new ForbiddenOperationException("این آگهی متعلق به شما نیست.");
        }
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("دسته‌بندی انتخاب‌شده یافت نشد."));
    }

    private City findCityOrThrow(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("شهر انتخاب‌شده یافت نشد."));
    }

    private User tryGetCurrentUser() {
        try {
            return currentUserProvider.getCurrentUser();
        } catch (ResourceNotFoundException ex) {
            return null;
        }
    }

    private AdvertisementResponse toFullResponse(Advertisement advertisement) {
        Long sellerId = advertisement.getOwner().getId();
        double avgRating = ratingRepository.calculateAverageScoreForSeller(sellerId);
        long ratingCount = ratingRepository.countBySellerId(sellerId);
        return AdvertisementResponse.fromEntity(advertisement, avgRating, ratingCount);
    }

    private AdvertisementSummaryResponse toSummaryResponse(Advertisement advertisement) {
        Long sellerId = advertisement.getOwner().getId();
        double avgRating = ratingRepository.calculateAverageScoreForSeller(sellerId);
        long ratingCount = ratingRepository.countBySellerId(sellerId);
        return AdvertisementSummaryResponse.fromEntity(advertisement, avgRating, ratingCount);
    }
}
