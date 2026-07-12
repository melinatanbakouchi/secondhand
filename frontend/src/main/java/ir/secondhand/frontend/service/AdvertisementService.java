package ir.secondhand.frontend.service;

import ir.secondhand.frontend.client.ApiClient;
import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.request.AdvertisementRequest;
import ir.secondhand.frontend.dto.response.AdvertisementResponse;
import ir.secondhand.frontend.dto.response.AdvertisementSummaryResponse;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AdvertisementService {

    private final ApiClient apiClient = ApiClient.getInstance();

    public List<AdvertisementSummaryResponse> search(String keyword, Long categoryId, Long cityId,
                                                       BigDecimal minPrice, BigDecimal maxPrice) throws ApiException {
        List<String> params = new ArrayList<>();
        addParam(params, "keyword", keyword);
        addParam(params, "categoryId", categoryId);
        addParam(params, "cityId", cityId);
        addParam(params, "minPrice", minPrice);
        addParam(params, "maxPrice", maxPrice);

        String query = params.isEmpty() ? "" : "?" + String.join("&", params);
        return apiClient.get("/advertisements" + query, apiClient.getMapper().getTypeFactory()
                .constructCollectionType(List.class, AdvertisementSummaryResponse.class));
    }

    public AdvertisementResponse getById(Long id) throws ApiException {
        return apiClient.get("/advertisements/" + id, AdvertisementResponse.class);
    }

    public List<AdvertisementSummaryResponse> getMyAdvertisements() throws ApiException {
        return apiClient.get("/advertisements/my", apiClient.getMapper().getTypeFactory()
                .constructCollectionType(List.class, AdvertisementSummaryResponse.class));
    }

    public AdvertisementResponse create(AdvertisementRequest request) throws ApiException {
        return apiClient.post("/advertisements", request, AdvertisementResponse.class);
    }

    public AdvertisementResponse update(Long id, AdvertisementRequest request) throws ApiException {
        return apiClient.put("/advertisements/" + id, request, AdvertisementResponse.class);
    }

    public void delete(Long id) throws ApiException {
        apiClient.delete("/advertisements/" + id);
    }

    public AdvertisementResponse markAsSold(Long id) throws ApiException {
        return apiClient.patch("/advertisements/" + id + "/sold", null, AdvertisementResponse.class);
    }

    public void addImage(Long advertisementId, File imageFile) throws ApiException {
        apiClient.uploadFile("/advertisements/" + advertisementId + "/images", imageFile, Void.class);
    }

    public void deleteImage(Long advertisementId, Long imageId) throws ApiException {
        apiClient.delete("/advertisements/" + advertisementId + "/images/" + imageId);
    }

    private void addParam(List<String> params, String name, Object value) {
        if (value == null) {
            return;
        }
        String encoded = URLEncoder.encode(value.toString(), StandardCharsets.UTF_8);
        params.add(name + "=" + encoded);
    }
}
