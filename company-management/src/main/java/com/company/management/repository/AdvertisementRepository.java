package com.company.management.repository;

import com.company.management.entity.Advertisement;
import com.company.management.entity.Employee;
import com.company.management.enums.AdType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    Page<Advertisement> findAll(Pageable pageable);

    // Total cost grouped by ad type — returns [AdType, BigDecimal]
    @Query("SELECT a.adType, SUM(a.cost) FROM Advertisement a GROUP BY a.adType ORDER BY SUM(a.cost) DESC")
    List<Object[]> totalCostByAdType();

    // Employee who entered the most ads
    @Query("""
            SELECT a.enteredBy, COUNT(a)
            FROM Advertisement a
            GROUP BY a.enteredBy
            ORDER BY COUNT(a) DESC
            """)
    List<Object[]> topAdEnterers(Pageable pageable);

    // Ads launched in last month (startedAt within range)
    @Query("SELECT COUNT(a) FROM Advertisement a WHERE a.startedAt >= :since")
    long countLaunchedSince(@Param("since") LocalDateTime since);

    // Ads ended in last month (startedAt + durationDays * interval ended before now)
    @Query(value = """
            SELECT COUNT(*) FROM advertisements
            WHERE started_at + (duration_days || ' days')::interval < NOW()
            AND started_at + (duration_days || ' days')::interval >= :since
            """, nativeQuery = true)
    long countEndedSince(@Param("since") LocalDateTime since);

    // Count per ad type
    @Query("SELECT a.adType, COUNT(a) FROM Advertisement a GROUP BY a.adType")
    List<Object[]> countByAdType();
}
