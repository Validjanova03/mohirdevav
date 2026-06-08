package com.company.management.service;

import com.company.management.dto.request.CreateCustomerRequest;
import com.company.management.dto.request.UpdateCustomerRequest;
import com.company.management.dto.response.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface CustomerService {

    CustomerResponse register(CreateCustomerRequest request, String registeredByUsername);

    CustomerResponse update(Long id, UpdateCustomerRequest request);

    CustomerResponse getById(Long id);

    void archive(Long id);

    Page<CustomerResponse> getMyCustomers(String username, Pageable pageable);

    Page<CustomerResponse> getAll(Pageable pageable);

    // Statistics
    Map<String, Object> getStatistics();
}
