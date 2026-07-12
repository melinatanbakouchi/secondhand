package ir.secondhand.backend.controller;

import ir.secondhand.backend.dto.request.RatingRequest;
import ir.secondhand.backend.dto.response.ApiResponse;
import ir.secondhand.backend.dto.response.RatingResponse;
import ir.secondhand.backend.dto.response.SellerRatingSummaryResponse;
import ir.secondhand.backend.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RatingResponse>> submitRating(@Valid @RequestBody RatingRequest request) {
        RatingResponse response = ratingService.submitRating(
                request.getAdvertisementId(), request.getScore(), request.getComment());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("امتیاز شما با موفقیت ثبت شد.", response));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<ApiResponse<SellerRatingSummaryResponse>> getSellerRatings(@PathVariable Long sellerId) {
        return ResponseEntity.ok(ApiResponse.success("امتیازات فروشنده", ratingService.getSellerRatingSummary(sellerId)));
    }
}
