package ir.secondhand.backend.dto.response;

import ir.secondhand.backend.entity.AdvertisementImage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdvertisementImageResponse {
    private Long id;
    private String imagePath;
    private int displayOrder;

    public static AdvertisementImageResponse fromEntity(AdvertisementImage image) {
        AdvertisementImageResponse response = new AdvertisementImageResponse();
        response.setId(image.getId());
        response.setImagePath(image.getImagePath());
        response.setDisplayOrder(image.getDisplayOrder());
        return response;
    }
}
