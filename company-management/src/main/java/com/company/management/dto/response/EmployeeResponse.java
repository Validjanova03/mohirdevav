package com.company.management.dto.response;

import com.company.management.entity.Employee;
import com.company.management.enums.Department;
import com.company.management.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private Integer age;
    private String passportSeries;
    private String passportNumber;
    private String pinfl;
    private String nationality;
    private BigDecimal salary;
    private String address;
    private Department department;
    private Role role;
    private String username;
    private boolean active;
    private LocalDateTime createdAt;

    public static EmployeeResponse from(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .middleName(e.getMiddleName())
                .age(e.getAge())
                .passportSeries(e.getPassportSeries())
                .passportNumber(e.getPassportNumber())
                .pinfl(e.getPinfl())
                .nationality(e.getNationality())
                .salary(e.getSalary())
                .address(e.getAddress())
                .department(e.getDepartment())
                .role(e.getRole())
                .username(e.getUsername())
                .active(e.isActive())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
