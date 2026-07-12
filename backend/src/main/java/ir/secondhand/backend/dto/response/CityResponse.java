package ir.secondhand.backend.dto.response;

import ir.secondhand.backend.entity.City;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityResponse {
    private Long id;
    private String name;

    public static CityResponse fromEntity(City city) {
        CityResponse response = new CityResponse();
        response.setId(city.getId());
        response.setName(city.getName());
        return response;
    }
}
