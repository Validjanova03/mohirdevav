package com.company.management.service;

import com.company.management.dto.request.CreateEmployeeRequest;
import com.company.management.dto.request.UpdateEmployeeRequest;
import com.company.management.dto.response.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface EmployeeService {

    EmployeeResponse create(CreateEmployeeRequest request);

    EmployeeResponse update(Long id, UpdateEmployeeRequest request);

    EmployeeResponse getById(Long id);

    void archive(Long id);

    Page<EmployeeResponse> getAll(Pageable pageable);

    // Statistics
    Page<EmployeeResponse> filterByAge(int minAge, int maxAge, Pageable pageable);

    Map<String, Object> countByDepartment();

    BigDecimal totalSalary();
}
