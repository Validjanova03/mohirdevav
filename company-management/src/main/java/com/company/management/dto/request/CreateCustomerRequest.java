package com.company.management.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateCustomerRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Passport series is required")
    @Size(min = 2, max = 2, message = "Passport series must be 2 characters")
    private String passportSeries;

    @NotBlank(message = "Passport number is required")
    @Size(min = 7, max = 7, message = "Passport number must be 7 characters")
    private String passportNumber;

    @NotBlank(message = "PINFL is required")
    @Size(min = 14, max = 14, message = "PINFL must be 14 characters")
    private String pinfl;

    @NotBlank(message = "Address is required")
    private String address;
}
