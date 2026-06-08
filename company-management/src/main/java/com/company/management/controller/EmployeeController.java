package com.company.management.controller;

import com.company.management.dto.request.CreateEmployeeRequest;
import com.company.management.dto.request.UpdateEmployeeRequest;
import com.company.management.dto.response.ApiResponse;
import com.company.management.dto.response.EmployeeResponse;
import com.company.management.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // POST /api/employees — DIRECTOR, HR_MANAGER
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(
            @Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Employee created successfully", employeeService.create(request)));
    }

    // PUT /api/employees/{id} — DIRECTOR, HR_MANAGER
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Employee updated successfully", employeeService.update(id, request)));
    }

    // GET /api/employees/{id} — DIRECTOR, HR_MANAGER, EMPLOYEE (own only enforced via @PreAuthorize if needed)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.getById(id)));
    }

    // DELETE /api/employees/{id} — DIRECTOR, HR_MANAGER
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> archive(@PathVariable Long id) {
        employeeService.archive(id);
        return ResponseEntity.ok(ApiResponse.ok("Employee archived successfully", null));
    }

    // GET /api/employees — DIRECTOR, HR_MANAGER
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getAll(
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.getAll(pageable)));
    }

    // ── Statistics (DIRECTOR only — enforced in SecurityConfig) ────────────

    // GET /api/employees/stats/by-department
    @GetMapping("/stats/by-department")
    public ResponseEntity<ApiResponse<Map<String, Object>>> statsByDepartment() {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.countByDepartment()));
    }

    // GET /api/employees/stats/by-age?minAge=20&maxAge=40
    @GetMapping("/stats/by-age")
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> filterByAge(
            @RequestParam int minAge,
            @RequestParam int maxAge,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.filterByAge(minAge, maxAge, pageable)));
    }

    // GET /api/employees/stats/total-salary
    @GetMapping("/stats/total-salary")
    public ResponseEntity<ApiResponse<BigDecimal>> totalSalary() {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.totalSalary()));
    }
}
