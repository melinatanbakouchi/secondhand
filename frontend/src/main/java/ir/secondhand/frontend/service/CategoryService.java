package ir.secondhand.frontend.service;

import ir.secondhand.frontend.client.ApiClient;
import ir.secondhand.frontend.client.ApiException;
import ir.secondhand.frontend.dto.request.CategoryRequest;
import ir.secondhand.frontend.dto.response.CategoryResponse;

import java.util.List;

public class CategoryService {

    private final ApiClient apiClient = ApiClient.getInstance();

    public List<CategoryResponse> getAllCategories() throws ApiException {
        return apiClient.get("/categories", apiClient.getMapper().getTypeFactory()
                .constructCollectionType(List.class, CategoryResponse.class));
    }

    public CategoryResponse createCategory(CategoryRequest request) throws ApiException {
        return apiClient.post("/categories", request, CategoryResponse.class);
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) throws ApiException {
        return apiClient.put("/categories/" + id, request, CategoryResponse.class);
    }

    public void deleteCategory(Long id) throws ApiException {
        apiClient.delete("/categories/" + id);
    }
}
