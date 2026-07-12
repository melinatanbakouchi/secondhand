package ir.secondhand.frontend.service;

import ir.secondhand.frontend.client.ApiClient;
import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.response.AdvertisementSummaryResponse;

import java.util.List;

public class FavoriteService {

    private final ApiClient apiClient = ApiClient.getInstance();

    public List<AdvertisementSummaryResponse> getMyFavorites() throws ApiException {
        return apiClient.get("/favorites", apiClient.getMapper().getTypeFactory()
                .constructCollectionType(List.class, AdvertisementSummaryResponse.class));
    }

    public void addFavorite(Long advertisementId) throws ApiException {
        apiClient.post("/favorites/" + advertisementId, null);
    }

    public void removeFavorite(Long advertisementId) throws ApiException {
        apiClient.delete("/favorites/" + advertisementId);
    }

    public boolean isFavorite(Long advertisementId) throws ApiException {
        Boolean result = apiClient.get("/favorites/" + advertisementId + "/status", Boolean.class);
        return result != null && result;
    }
}
