package ir.secondhand.backend.controller;

import ir.secondhand.backend.dto.request.CityRequest;
import ir.secondhand.backend.dto.response.ApiResponse;
import ir.secondhand.backend.dto.response.CityResponse;
import ir.secondhand.backend.service.CityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CityResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("لیست شهرها", cityService.getAllCities()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CityResponse>> create(@Valid @RequestBody CityRequest request) {
        CityResponse response = cityService.createCity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("شهر با موفقیت ثبت شد.", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id) {
        cityService.deleteCity(id);
        return ResponseEntity.ok(ApiResponse.success("شهر با موفقیت حذف شد."));
    }
}
