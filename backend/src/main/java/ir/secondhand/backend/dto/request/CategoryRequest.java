package ir.secondhand.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message = "عنوان دسته‌بندی الزامی است.")
    private String title;

    private String description;

    private Long parentId;
}
