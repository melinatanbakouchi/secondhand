package ir.secondhand.backend.controller;

import ir.secondhand.backend.dto.request.AdvertisementRequest;
import ir.secondhand.backend.dto.response.ApiResponse;
import ir.secondhand.backend.dto.response.AdvertisementResponse;
import ir.secondhand.backend.dto.response.AdvertisementSummaryResponse;
import ir.secondhand.backend.service.AdvertisementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    public AdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdvertisementSummaryResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        List<AdvertisementSummaryResponse> result =
                advertisementService.searchAdvertisements(keyword, categoryId, cityId, minPrice, maxPrice);
        return ResponseEntity.ok(ApiResponse.success("نتایج جست‌وجوی آگهی", result));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<AdvertisementSummaryResponse>>> getMyAdvertisements() {
        return ResponseEntity.ok(ApiResponse.success("آگهی‌های من", advertisementService.getMyAdvertisements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdvertisementResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("جزئیات آگهی", advertisementService.getAdvertisementById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdvertisementResponse>> create(@Valid @RequestBody AdvertisementRequest request) {
        AdvertisementResponse response = advertisementService.createAdvertisement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("آگهی با موفقیت ثبت شد و در انتظار تایید است.", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdvertisementResponse>> update(@PathVariable Long id,
                                                                       @Valid @RequestBody AdvertisementRequest request) {
        AdvertisementResponse response = advertisementService.updateAdvertisement(id, request);
        return ResponseEntity.ok(ApiResponse.success("آگهی با موفقیت ویرایش شد و در انتظار تایید مجدد است.", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id) {
        advertisementService.deleteAdvertisement(id);
        return ResponseEntity.ok(ApiResponse.success("آگهی با موفقیت حذف شد."));
    }

    @PatchMapping("/{id}/sold")
    public ResponseEntity<ApiResponse<AdvertisementResponse>> markAsSold(@PathVariable Long id) {
        AdvertisementResponse response = advertisementService.markAsSold(id);
        return ResponseEntity.ok(ApiResponse.success("آگهی به‌عنوان فروخته‌شده علامت‌گذاری شد.", response));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<Object>> addImage(@PathVariable Long id,
                                                          @RequestParam("file") MultipartFile file) {
        advertisementService.addImage(id, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("تصویر با موفقیت افزوده شد."));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<ApiResponse<Object>> deleteImage(@PathVariable Long id, @PathVariable Long imageId) {
        advertisementService.deleteImage(id, imageId);
        return ResponseEntity.ok(ApiResponse.success("تصویر با موفقیت حذف شد."));
    }
}
