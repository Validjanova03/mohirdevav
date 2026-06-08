package com.company.management.dto.request;

import com.company.management.enums.AdType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateAdvertisementRequest {

    @NotNull(message = "Ad type is required")
    private AdType adType;

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost must be positive")
    private BigDecimal cost;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    @NotNull(message = "Start date is required")
    private LocalDateTime startedAt;
}
