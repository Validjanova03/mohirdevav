package com.company.management.dto.response;

import com.company.management.entity.Advertisement;
import com.company.management.enums.AdType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AdvertisementResponse {

    private Long id;
    private AdType adType;
    private BigDecimal cost;
    private Integer durationDays;
    private LocalDateTime startedAt;
    private String enteredByUsername;
    private LocalDateTime createdAt;

    public static AdvertisementResponse from(Advertisement a) {
        return AdvertisementResponse.builder()
                .id(a.getId())
                .adType(a.getAdType())
                .cost(a.getCost())
                .durationDays(a.getDurationDays())
                .startedAt(a.getStartedAt())
                .enteredByUsername(a.getEnteredBy().getUsername())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
