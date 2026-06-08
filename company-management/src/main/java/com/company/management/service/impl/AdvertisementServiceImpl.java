package com.company.management.service.impl;

import com.company.management.dto.request.CreateAdvertisementRequest;
import com.company.management.dto.request.UpdateAdvertisementRequest;
import com.company.management.dto.response.AdvertisementResponse;
import com.company.management.entity.Advertisement;
import com.company.management.entity.Employee;
import com.company.management.enums.AdType;
import com.company.management.exception.NotFoundException;
import com.company.management.repository.AdvertisementRepository;
import com.company.management.repository.EmployeeRepository;
import com.company.management.service.AdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public AdvertisementResponse create(CreateAdvertisementRequest req, String enteredByUsername) {
        Employee employee = findEmployeeByUsername(enteredByUsername);

        Advertisement ad = Advertisement.builder()
                .adType(req.getAdType())
                .cost(req.getCost())
                .durationDays(req.getDurationDays())
                .startedAt(req.getStartedAt())
                .enteredBy(employee)
                .build();

        Advertisement saved = advertisementRepository.save(ad);
        log.info("Advertisement created: id={}, type={}, cost={}, by={}",
                saved.getId(), saved.getAdType(), saved.getCost(), enteredByUsername);
        return AdvertisementResponse.from(saved);
    }

    @Override
    public AdvertisementResponse update(Long id, UpdateAdvertisementRequest req) {
        Advertisement ad = findById(id);

        // adType and enteredBy are NOT updatable
        if (req.getCost()        != null) ad.setCost(req.getCost());
        if (req.getDurationDays() != null) ad.setDurationDays(req.getDurationDays());
        if (req.getStartedAt()   != null) ad.setStartedAt(req.getStartedAt());

        Advertisement saved = advertisementRepository.save(ad);
        log.info("Advertisement updated: id={}, type={}", saved.getId(), saved.getAdType());
        return AdvertisementResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AdvertisementResponse getById(Long id) {
        return AdvertisementResponse.from(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdvertisementResponse> getAll(Pageable pageable) {
        return advertisementRepository.findAll(pageable).map(AdvertisementResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Map<String, Object> stats = new LinkedHashMap<>();

        // 1. Highest cost ad type
        List<Object[]> costByType = advertisementRepository.totalCostByAdType();
        if (!costByType.isEmpty()) {
            stats.put("highestCostAdType", Map.of(
                    "adType", costByType.get(0)[0].toString(),
                    "totalCost", costByType.get(0)[1]
            ));
        }

        // 2. Employee with most entries
        List<Object[]> topEntrers = advertisementRepository.topAdEnterers(PageRequest.of(0, 1));
        if (!topEntrers.isEmpty()) {
            Employee emp = (Employee) topEntrers.get(0)[0];
            stats.put("topAdEnterer", Map.of(
                    "username", emp.getUsername(),
                    "name", emp.getFirstName() + " " + emp.getLastName(),
                    "count", topEntrers.get(0)[1]
            ));
        }

        // 3. Ads launched last month
        stats.put("launchedLastMonth", advertisementRepository.countLaunchedSince(oneMonthAgo));

        // 4. Ads ended last month
        stats.put("endedLastMonth", advertisementRepository.countEndedSince(oneMonthAgo));

        // 5. Count per ad type
        List<Object[]> countPerType = advertisementRepository.countByAdType();
        Map<String, Long> perType = new LinkedHashMap<>();
        for (AdType t : AdType.values()) perType.put(t.name(), 0L);
        for (Object[] row : countPerType) {
            perType.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("countPerAdType", perType);

        return stats;
    }

    private Advertisement findById(Long id) {
        return advertisementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Advertisement not found with id: " + id));
    }

    private Employee findEmployeeByUsername(String username) {
        return employeeRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + username));
    }
}
