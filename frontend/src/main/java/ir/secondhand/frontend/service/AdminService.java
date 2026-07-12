package ir.secondhand.frontend.service;

import ir.secondhand.frontend.client.ApiClient;
import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.request.AdvertisementRejectRequest;
import ir.secondhand.frontend.dto.response.AdminStatsResponse;
import ir.secondhand.frontend.dto.response.AdvertisementResponse;
import ir.secondhand.frontend.dto.response.AdvertisementSummaryResponse;
import ir.secondhand.frontend.dto.response.UserResponse;

import java.util.List;

public class AdminService {

    private final ApiClient apiClient = ApiClient.getInstance();

    public List<UserResponse> getAllUsers() throws ApiException {
        return apiClient.get("/admin/users", apiClient.getMapper().getTypeFactory()
                .constructCollectionType(List.class, UserResponse.class));
    }

    public UserResponse blockUser(Long id) throws ApiException {
        return apiClient.put("/admin/users/" + id + "/block", null, UserResponse.class);
    }

    public UserResponse unblockUser(Long id) throws ApiException {
        return apiClient.put("/admin/users/" + id + "/unblock", null, UserResponse.class);
    }

    public AdminStatsResponse getStats() throws ApiException {
        return apiClient.get("/admin/stats", AdminStatsResponse.class);
    }

    public List<AdvertisementSummaryResponse> getPendingAdvertisements() throws ApiException {
        return apiClient.get("/admin/advertisements/pending", apiClient.getMapper().getTypeFactory()
                .constructCollectionType(List.class, AdvertisementSummaryResponse.class));
    }

    public AdvertisementResponse approveAdvertisement(Long id) throws ApiException {
        return apiClient.put("/admin/advertisements/" + id + "/approve", null, AdvertisementResponse.class);
    }

    public AdvertisementResponse rejectAdvertisement(Long id, String reason) throws ApiException {
        return apiClient.put("/admin/advertisements/" + id + "/reject",
                new AdvertisementRejectRequest(reason), AdvertisementResponse.class);
    }

    public void deleteAdvertisement(Long id) throws ApiException {
        apiClient.delete("/admin/advertisements/" + id);
    }
}
