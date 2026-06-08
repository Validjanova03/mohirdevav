package com.company.management.dto.response;

import com.company.management.entity.Customer;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomerResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String passportSeries;
    private String passportNumber;
    private String pinfl;
    private String address;
    private LocalDateTime registeredAt;
    private String registeredByUsername;
    private boolean archived;

    public static CustomerResponse from(Customer c) {
        return CustomerResponse.builder()
                .id(c.getId())
                .firstName(c.getFirstName())
                .lastName(c.getLastName())
                .passportSeries(c.getPassportSeries())
                .passportNumber(c.getPassportNumber())
                .pinfl(c.getPinfl())
                .address(c.getAddress())
                .registeredAt(c.getRegisteredAt())
                .registeredByUsername(c.getRegisteredBy().getUsername())
                .archived(c.isArchived())
                .build();
    }
}
