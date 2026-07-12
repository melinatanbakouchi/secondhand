package ir.secondhand.frontend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String username;
    private String phoneNumber;
    private String email;
    private String role;
    private String status;
    private LocalDateTime createdAt;
}
