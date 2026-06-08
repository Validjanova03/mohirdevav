package com.company.management.service;

import com.company.management.dto.request.CreateAdvertisementRequest;
import com.company.management.dto.request.UpdateAdvertisementRequest;
import com.company.management.dto.response.AdvertisementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface AdvertisementService {

    AdvertisementResponse create(CreateAdvertisementRequest request, String enteredByUsername);

    AdvertisementResponse update(Long id, UpdateAdvertisementRequest request);

    AdvertisementResponse getById(Long id);

    Page<AdvertisementResponse> getAll(Pageable pageable);

    // Statistics
    Map<String, Object> getStatistics();
}
