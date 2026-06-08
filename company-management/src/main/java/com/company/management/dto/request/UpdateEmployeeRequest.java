package com.company.management.dto.request;

import com.company.management.enums.Department;
import com.company.management.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateEmployeeRequest {

    private String firstName;

    private String lastName;

    private String middleName;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 70, message = "Age must be at most 70")
    private Integer age;

    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be positive")
    private BigDecimal salary;

    private String address;

    private Department department;

    private Role role;
}
