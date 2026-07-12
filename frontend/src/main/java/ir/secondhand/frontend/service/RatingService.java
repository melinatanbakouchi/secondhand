package ir.secondhand.frontend.service;

import ir.secondhand.frontend.client.ApiClient;
import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.request.RatingRequest;
import ir.secondhand.frontend.dto.response.RatingResponse;
import ir.secondhand.frontend.dto.response.SellerRatingSummaryResponse;

public class RatingService {

    private final ApiClient apiClient = ApiClient.getInstance();

    public RatingResponse submitRating(Long advertisementId, int score, String comment) throws ApiException {
        return apiClient.post("/ratings", new RatingRequest(advertisementId, score, comment), RatingResponse.class);
    }

    public SellerRatingSummaryResponse getSellerRatingSummary(Long sellerId) throws ApiException {
        return apiClient.get("/ratings/seller/" + sellerId, SellerRatingSummaryResponse.class);
    }
}
