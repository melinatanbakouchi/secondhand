package ir.secondhand.backend.controller;

import ir.secondhand.backend.dto.response.ApiResponse;
import ir.secondhand.backend.dto.response.AdvertisementSummaryResponse;
import ir.secondhand.backend.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdvertisementSummaryResponse>>> getMyFavorites() {
        return ResponseEntity.ok(ApiResponse.success("لیست علاقه‌مندی‌های من", favoriteService.getMyFavorites()));
    }

    @PostMapping("/{advertisementId}")
    public ResponseEntity<ApiResponse<Object>> addFavorite(@PathVariable Long advertisementId) {
        favoriteService.addFavorite(advertisementId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("آگهی به علاقه‌مندی‌ها اضافه شد."));
    }

    @DeleteMapping("/{advertisementId}")
    public ResponseEntity<ApiResponse<Object>> removeFavorite(@PathVariable Long advertisementId) {
        favoriteService.removeFavorite(advertisementId);
        return ResponseEntity.ok(ApiResponse.success("آگهی از علاقه‌مندی‌ها حذف شد."));
    }

    @GetMapping("/{advertisementId}/status")
    public ResponseEntity<ApiResponse<Boolean>> isFavorite(@PathVariable Long advertisementId) {
        return ResponseEntity.ok(ApiResponse.success("وضعیت علاقه‌مندی", favoriteService.isFavorite(advertisementId)));
    }
}
