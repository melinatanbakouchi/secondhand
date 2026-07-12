package ir.secondhand.backend.dto.response;

import ir.secondhand.backend.entity.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponse {
    private Long id;
    private String title;
    private String description;
    private Long parentId;
    private String parentTitle;

    public static CategoryResponse fromEntity(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setTitle(category.getTitle());
        response.setDescription(category.getDescription());
        if (category.getParent() != null) {
            response.setParentId(category.getParent().getId());
            response.setParentTitle(category.getParent().getTitle());
        }
        return response;
    }
}
