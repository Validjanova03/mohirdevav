package com.company.management.service.impl;

import com.company.management.dto.request.CreateCustomerRequest;
import com.company.management.dto.request.UpdateCustomerRequest;
import com.company.management.dto.response.CustomerResponse;
import com.company.management.entity.Customer;
import com.company.management.entity.Employee;
import com.company.management.exception.NotFoundException;
import com.company.management.repository.CustomerRepository;
import com.company.management.repository.EmployeeRepository;
import com.company.management.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public CustomerResponse register(CreateCustomerRequest req, String registeredByUsername) {
        Employee employee = findEmployeeByUsername(registeredByUsername);

        Customer customer = Customer.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .passportSeries(req.getPassportSeries())
                .passportNumber(req.getPassportNumber())
                .pinfl(req.getPinfl())
                .address(req.getAddress())
                .registeredAt(LocalDateTime.now())
                .registeredBy(employee)
                .archived(false)
                .build();

        Customer saved = customerRepository.save(customer);
        log.info("Customer registered: id={}, name={} {}, by={}",
                saved.getId(), saved.getFirstName(), saved.getLastName(), registeredByUsername);
        return CustomerResponse.from(saved);
    }

    @Override
    public CustomerResponse update(Long id, UpdateCustomerRequest req) {
        Customer customer = findActiveById(id);

        if (req.getFirstName() != null) customer.setFirstName(req.getFirstName());
        if (req.getLastName()  != null) customer.setLastName(req.getLastName());
        if (req.getAddress()   != null) customer.setAddress(req.getAddress());

        Customer saved = customerRepository.save(customer);
        log.info("Customer updated: id={}, name={} {}", saved.getId(), saved.getFirstName(), saved.getLastName());
        return CustomerResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        return CustomerResponse.from(findActiveById(id));
    }

    @Override
    public void archive(Long id) {
        Customer customer = findActiveById(id);
        customer.setArchived(true);
        customerRepository.save(customer);
        log.info("Customer archived: id={}, name={} {}", id, customer.getFirstName(), customer.getLastName());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getMyCustomers(String username, Pageable pageable) {
        Employee employee = findEmployeeByUsername(username);
        return customerRepository.findByRegisteredBy(employee, pageable).map(CustomerResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAll(Pageable pageable) {
        return customerRepository.findAll(pageable).map(CustomerResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime oneMonthAgo = now.minusMonths(1);

        Map<String, Object> stats = new LinkedHashMap<>();

        // 1. Daily count
        stats.put("registeredToday", customerRepository.countByRegisteredAtBetween(startOfDay, now));

        // 2. Top registrar (1st place)
        List<Object[]> top = customerRepository.findTopRegistrars(PageRequest.of(0, 3));
        if (!top.isEmpty()) {
            Employee topEmployee = (Employee) top.get(0)[0];
            stats.put("topRegistrar", Map.of(
                    "username", topEmployee.getUsername(),
                    "name", topEmployee.getFirstName() + " " + topEmployee.getLastName(),
                    "count", top.get(0)[1]
            ));

            // 3. Top 3
            List<Map<String, Object>> top3 = top.stream().map(row -> {
                Employee emp = (Employee) row[0];
                return (Map<String, Object>) Map.of(
                        "username", emp.getUsername(),
                        "name", emp.getFirstName() + " " + emp.getLastName(),
                        "count", row[1]
                );
            }).toList();
            stats.put("top3Registrars", top3);
        }

        // 4. Last 30 days count
        stats.put("last30DaysCount", customerRepository.countSince(oneMonthAgo));

        // 5. Busiest day in last month
        Object[] busiest = customerRepository.busiestDaySince(oneMonthAgo);
        if (busiest != null) {
            stats.put("busiestDay", Map.of("date", busiest[0].toString(), "count", busiest[1]));
        }

        return stats;
    }

    private Customer findActiveById(Long id) {
        return customerRepository.findById(id)
                .filter(c -> !c.isArchived())
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
    }

    private Employee findEmployeeByUsername(String username) {
        return employeeRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + username));
    }
}
