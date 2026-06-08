package com.company.management.entity;

import com.company.management.enums.AdType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "advertisements")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NOT updatable — ad type is fixed after creation
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private AdType adType;

    // Updatable
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal cost;

    // Updatable — duration in days
    @Column(nullable = false)
    private Integer durationDays;

    // Updatable
    @Column(nullable = false)
    private LocalDateTime startedAt;

    // NOT updatable — who entered this record
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entered_by_id", nullable = false, updatable = false)
    private Employee enteredBy;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
