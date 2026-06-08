package com.company.management.service.impl;

import com.company.management.dto.request.CreateEmployeeRequest;
import com.company.management.dto.request.UpdateEmployeeRequest;
import com.company.management.dto.response.EmployeeResponse;
import com.company.management.entity.Employee;
import com.company.management.enums.Department;
import com.company.management.exception.AlreadyExistsException;
import com.company.management.exception.NotFoundException;
import com.company.management.repository.EmployeeRepository;
import com.company.management.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public EmployeeResponse create(CreateEmployeeRequest req) {
        if (employeeRepository.existsByUsername(req.getUsername()))
            throw new AlreadyExistsException("Username already taken: " + req.getUsername());
        if (employeeRepository.existsByPinfl(req.getPinfl()))
            throw new AlreadyExistsException("PINFL already registered: " + req.getPinfl());
        if (employeeRepository.existsByPassportNumber(req.getPassportNumber()))
            throw new AlreadyExistsException("Passport number already registered: " + req.getPassportNumber());

        Employee employee = Employee.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .middleName(req.getMiddleName())
                .age(req.getAge())
                .passportSeries(req.getPassportSeries())
                .passportNumber(req.getPassportNumber())
                .pinfl(req.getPinfl())
                .nationality(req.getNationality())
                .salary(req.getSalary())
                .address(req.getAddress())
                .department(req.getDepartment())
                .role(req.getRole())
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .active(true)
                .build();

        Employee saved = employeeRepository.save(employee);
        log.info("Employee created: id={}, username={}, role={}, department={}",
                saved.getId(), saved.getUsername(), saved.getRole(), saved.getDepartment());
        return EmployeeResponse.from(saved);
    }

    @Override
    public EmployeeResponse update(Long id, UpdateEmployeeRequest req) {
        Employee employee = findActiveById(id);

        if (req.getFirstName()  != null) employee.setFirstName(req.getFirstName());
        if (req.getLastName()   != null) employee.setLastName(req.getLastName());
        if (req.getMiddleName() != null) employee.setMiddleName(req.getMiddleName());
        if (req.getAge()        != null) employee.setAge(req.getAge());
        if (req.getSalary()     != null) employee.setSalary(req.getSalary());
        if (req.getAddress()    != null) employee.setAddress(req.getAddress());
        if (req.getDepartment() != null) employee.setDepartment(req.getDepartment());
        if (req.getRole()       != null) employee.setRole(req.getRole());

        Employee saved = employeeRepository.save(employee);
        log.info("Employee updated: id={}, username={}", saved.getId(), saved.getUsername());
        return EmployeeResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return EmployeeResponse.from(findActiveById(id));
    }

    @Override
    public void archive(Long id) {
        Employee employee = findActiveById(id);
        employee.setActive(false);
        employeeRepository.save(employee);
        log.info("Employee archived: id={}, username={}", id, employee.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAll(Pageable pageable) {
        return employeeRepository.findAllByActiveTrue(pageable).map(EmployeeResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> filterByAge(int minAge, int maxAge, Pageable pageable) {
        return employeeRepository
                .findByActiveTrueAndAgeBetween(minAge, maxAge, pageable)
                .map(EmployeeResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> countByDepartment() {
        List<Object[]> rows = employeeRepository.countByDepartment();
        long total = rows.stream().mapToLong(r -> (Long) r[1]).sum();

        Map<String, Object> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Department dept = (Department) row[0];
            long count = (Long) row[1];
            double percent = total == 0 ? 0 : (count * 100.0 / total);

            Map<String, Object> entry = new HashMap<>();
            entry.put("count", count);
            entry.put("percentage", BigDecimal.valueOf(percent).setScale(2, RoundingMode.HALF_UP));
            result.put(dept.name(), entry);
        }
        result.put("total", total);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal totalSalary() {
        BigDecimal sum = employeeRepository.totalSalary();
        return sum != null ? sum : BigDecimal.ZERO;
    }

    private Employee findActiveById(Long id) {
        return employeeRepository.findById(id)
                .filter(Employee::isActive)
                .orElseThrow(() -> new NotFoundException("Employee not found with id: " + id));
    }
}
