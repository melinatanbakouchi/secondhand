package ir.secondhand.frontend.service;

import ir.secondhand.frontend.client.ApiClient;
import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.request.CityRequest;
import ir.secondhand.frontend.dto.response.CityResponse;

import java.util.List;

public class CityService {

    private final ApiClient apiClient = ApiClient.getInstance();

    public List<CityResponse> getAllCities() throws ApiException {
        return apiClient.get("/cities", apiClient.getMapper().getTypeFactory()
                .constructCollectionType(List.class, CityResponse.class));
    }

    public CityResponse createCity(CityRequest request) throws ApiException {
        return apiClient.post("/cities", request, CityResponse.class);
    }

    public void deleteCity(Long id) throws ApiException {
        apiClient.delete("/cities/" + id);
    }
}
