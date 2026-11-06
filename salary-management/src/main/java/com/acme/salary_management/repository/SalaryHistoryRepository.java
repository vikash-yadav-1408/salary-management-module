package com.acme.salary_management.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.acme.salary_management.entity.SalaryHistory;

import java.util.List;
import java.util.Optional;

public interface SalaryHistoryRepository
        extends JpaRepository<SalaryHistory, Long> {

    List<SalaryHistory>
        findByEmployeeIdOrderByEffectiveFromDesc(
            Long employeeId
        );

    Optional<SalaryHistory>
        findByEmployeeIdAndEffectiveToIsNull(
            Long employeeId
        );

    boolean existsByEmployeeIdAndEffectiveToIsNull(
        Long employeeId
    );
}
