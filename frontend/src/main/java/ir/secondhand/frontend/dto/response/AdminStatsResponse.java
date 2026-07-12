package ir.secondhand.frontend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminStatsResponse {
    private long totalUsers;
    private long blockedUsers;
    private long totalAdvertisements;
    private long pendingAdvertisements;
    private long activeAdvertisements;
    private long soldAdvertisements;
}
