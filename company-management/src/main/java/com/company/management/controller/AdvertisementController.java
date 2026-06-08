package com.company.management.controller;

import com.company.management.dto.request.CreateAdvertisementRequest;
import com.company.management.dto.request.UpdateAdvertisementRequest;
import com.company.management.dto.response.AdvertisementResponse;
import com.company.management.dto.response.ApiResponse;
import com.company.management.service.AdvertisementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/advertisements")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    // POST /api/advertisements — SALES_MANAGER
    @PostMapping
    public ResponseEntity<ApiResponse<AdvertisementResponse>> create(
            @Valid @RequestBody CreateAdvertisementRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Advertisement created successfully",
                        advertisementService.create(request, userDetails.getUsername())));
    }

    // PUT /api/advertisements/{id} — SALES_MANAGER
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdvertisementResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAdvertisementRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Advertisement updated successfully",
                advertisementService.update(id, request)));
    }

    // GET /api/advertisements/{id} — DIRECTOR, SALES_MANAGER
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdvertisementResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(advertisementService.getById(id)));
    }

    // GET /api/advertisements — DIRECTOR, SALES_MANAGER (paginated)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdvertisementResponse>>> getAll(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(advertisementService.getAll(pageable)));
    }

    // GET /api/advertisements/stats — DIRECTOR only
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> stats() {
        return ResponseEntity.ok(ApiResponse.ok(advertisementService.getStatistics()));
    }
}
