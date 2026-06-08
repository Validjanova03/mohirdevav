package com.company.management.dto.request;

import com.company.management.enums.Department;
import com.company.management.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateEmployeeRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String middleName;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 70, message = "Age must be at most 70")
    private Integer age;

    @NotBlank(message = "Passport series is required")
    @Size(min = 2, max = 2, message = "Passport series must be 2 characters")
    private String passportSeries;

    @NotBlank(message = "Passport number is required")
    @Size(min = 7, max = 7, message = "Passport number must be 7 characters")
    private String passportNumber;

    @NotBlank(message = "PINFL is required")
    @Size(min = 14, max = 14, message = "PINFL must be 14 characters")
    private String pinfl;

    @NotBlank(message = "Nationality is required")
    private String nationality;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be positive")
    private BigDecimal salary;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Department is required")
    private Department department;

    @NotNull(message = "Role is required")
    private Role role;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
