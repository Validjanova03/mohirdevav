package com.company.management.repository;

import com.company.management.entity.Employee;
import com.company.management.enums.Department;
import com.company.management.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByPinfl(String pinfl);

    boolean existsByPassportNumber(String passportNumber);

    // Active employees paginated
    Page<Employee> findAllByActiveTrue(Pageable pageable);

    // Filter by age range
    Page<Employee> findByActiveTrueAndAgeBetween(int minAge, int maxAge, Pageable pageable);

    // Count per department
    @Query("SELECT e.department, COUNT(e) FROM Employee e WHERE e.active = true GROUP BY e.department")
    List<Object[]> countByDepartment();

    // Total salary
    @Query("SELECT SUM(e.salary) FROM Employee e WHERE e.active = true")
    BigDecimal totalSalary();
}
