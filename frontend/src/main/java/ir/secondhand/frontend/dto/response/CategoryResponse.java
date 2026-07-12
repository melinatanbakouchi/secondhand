package ir.secondhand.frontend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String title;
    private String description;
    private Long parentId;
    private String parentTitle;

    @Override
    public String toString() {
        return title;
    }
}
