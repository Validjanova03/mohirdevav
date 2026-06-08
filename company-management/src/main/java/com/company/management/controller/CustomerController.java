package com.company.management.controller;

import com.company.management.dto.request.CreateCustomerRequest;
import com.company.management.dto.request.UpdateCustomerRequest;
import com.company.management.dto.response.ApiResponse;
import com.company.management.dto.response.CustomerResponse;
import com.company.management.service.CustomerService;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // POST /api/customers — CUSTOMER_MANAGER
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> register(
            @Valid @RequestBody CreateCustomerRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Customer registered successfully",
                        customerService.register(request, userDetails.getUsername())));
    }

    // PUT /api/customers/{id} — CUSTOMER_MANAGER
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Customer updated successfully",
                customerService.update(id, request)));
    }

    // GET /api/customers/{id} — DIRECTOR, CUSTOMER_MANAGER
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getById(id)));
    }

    // PATCH /api/customers/{id}/archive — CUSTOMER_MANAGER
    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<Void>> archive(@PathVariable Long id) {
        customerService.archive(id);
        return ResponseEntity.ok(ApiResponse.ok("Customer archived successfully", null));
    }

    // GET /api/customers/my — CUSTOMER_MANAGER (own customers)
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<CustomerResponse>>> myCustomers(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                customerService.getMyCustomers(userDetails.getUsername(), pageable)));
    }

    // GET /api/customers/all — DIRECTOR only
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<CustomerResponse>>> getAll(
            @PageableDefault(size = 10, sort = "registeredAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getAll(pageable)));
    }

    // GET /api/customers/stats — DIRECTOR only
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> stats() {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getStatistics()));
    }
}
