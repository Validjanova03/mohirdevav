package com.company.management.config;

import com.company.management.entity.Employee;
import com.company.management.enums.Department;
import com.company.management.enums.Role;
import com.company.management.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (employeeRepository.existsByUsername("director")) {
            return;
        }

        Employee director = Employee.builder()
                .firstName("System")
                .lastName("Director")
                .middleName("")
                .age(40)
                .passportSeries("AA")
                .passportNumber("1234567")
                .pinfl("12345678901234")
                .nationality("Uzbek")
                .salary(BigDecimal.valueOf(10_000_000))
                .address("Tashkent, Uzbekistan")
                .department(Department.MANAGEMENT)
                .role(Role.DIRECTOR)
                .username("director")
                .password(passwordEncoder.encode("director123"))
                .active(true)
                .build();

        employeeRepository.save(director);
        log.info("Default DIRECTOR account created. username=director password=director123");
    }
}
