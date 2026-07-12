package ir.secondhand.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalUsers;
    private long blockedUsers;
    private long totalAdvertisements;
    private long pendingAdvertisements;
    private long activeAdvertisements;
    private long soldAdvertisements;
}
