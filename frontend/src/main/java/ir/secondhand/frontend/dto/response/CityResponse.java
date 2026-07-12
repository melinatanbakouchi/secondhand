package ir.secondhand.frontend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CityResponse {
    private Long id;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
