package ir.secondhand.backend.controller;

import ir.secondhand.backend.dto.request.AdvertisementRejectRequest;
import ir.secondhand.backend.dto.response.AdminStatsResponse;
import ir.secondhand.backend.dto.response.AdvertisementResponse;
import ir.secondhand.backend.dto.response.AdvertisementSummaryResponse;
import ir.secondhand.backend.dto.response.ApiResponse;
import ir.secondhand.backend.dto.response.UserResponse;
import ir.secondhand.backend.service.AdminService;
import ir.secondhand.backend.service.AdvertisementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * تمام مسیرهای این کنترلر فقط برای کاربران با نقش ADMIN در پیکربندی امنیتی مجاز است.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdvertisementService advertisementService;

    public AdminController(AdminService adminService, AdvertisementService advertisementService) {
        this.adminService = adminService;
        this.advertisementService = advertisementService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("لیست کاربران", adminService.getAllUsers()));
    }

    @PutMapping("/users/{id}/block")
    public ResponseEntity<ApiResponse<UserResponse>> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("کاربر با موفقیت مسدود شد.", adminService.blockUser(id)));
    }

    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<ApiResponse<UserResponse>> unblockUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("کاربر با موفقیت رفع مسدودیت شد.", adminService.unblockUser(id)));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.success("آمار کلی سامانه", adminService.getStats()));
    }

    @GetMapping("/advertisements/pending")
    public ResponseEntity<ApiResponse<List<AdvertisementSummaryResponse>>> getPendingAdvertisements() {
        return ResponseEntity.ok(ApiResponse.success("آگهی‌های در انتظار بررسی", advertisementService.getPendingAdvertisements()));
    }

    @PutMapping("/advertisements/{id}/approve")
    public ResponseEntity<ApiResponse<AdvertisementResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("آگهی با موفقیت تایید شد.", advertisementService.approveAdvertisement(id)));
    }

    @PutMapping("/advertisements/{id}/reject")
    public ResponseEntity<ApiResponse<AdvertisementResponse>> reject(@PathVariable Long id,
                                                                       @Valid @RequestBody AdvertisementRejectRequest request) {
        AdvertisementResponse response = advertisementService.rejectAdvertisement(id, request.getReason());
        return ResponseEntity.ok(ApiResponse.success("آگهی با موفقیت رد شد.", response));
    }

    @DeleteMapping("/advertisements/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteAdvertisement(@PathVariable Long id) {
        advertisementService.deleteAdvertisement(id);
        return ResponseEntity.ok(ApiResponse.success("آگهی نامناسب با موفقیت حذف شد."));
    }
}
