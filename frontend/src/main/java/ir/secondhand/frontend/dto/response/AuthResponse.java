package ir.secondhand.frontend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String fullName;
    private String role;
}
