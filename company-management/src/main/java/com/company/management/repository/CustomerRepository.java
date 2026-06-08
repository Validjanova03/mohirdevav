package com.company.management.repository;

import com.company.management.entity.Customer;
import com.company.management.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Each employee sees only their own customers
    Page<Customer> findByRegisteredBy(Employee employee, Pageable pageable);

    // Daily count
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.registeredAt >= :start AND c.registeredAt < :end")
    long countByRegisteredAtBetween(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    // Which employee registered most customers — returns [Employee, count]
    @Query("""
            SELECT c.registeredBy, COUNT(c)
            FROM Customer c
            GROUP BY c.registeredBy
            ORDER BY COUNT(c) DESC
            """)
    List<Object[]> findTopRegistrars(Pageable pageable);

    // Customers registered in last N days
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.registeredAt >= :since")
    long countSince(@Param("since") LocalDateTime since);

    // Day with most registrations in last month
    @Query(value = """
            SELECT DATE(registered_at) AS day, COUNT(*) AS cnt
            FROM customers
            WHERE registered_at >= :since
            GROUP BY DATE(registered_at)
            ORDER BY cnt DESC
            LIMIT 1
            """, nativeQuery = true)
    Object[] busiestDaySince(@Param("since") LocalDateTime since);
}
