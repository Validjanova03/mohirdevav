package com.company.management.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// adType and enteredBy are NOT updatable
@Data
public class UpdateAdvertisementRequest {

    @DecimalMin(value = "0.0", inclusive = false, message = "Cost must be positive")
    private BigDecimal cost;

    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    private LocalDateTime startedAt;
}
