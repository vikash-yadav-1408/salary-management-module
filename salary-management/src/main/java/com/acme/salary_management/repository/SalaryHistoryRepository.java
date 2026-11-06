package com.acme.salary_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acme.salary_management.entity.SalaryHistory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long> {

	List<SalaryHistory> findByEmployeeIdOrderByEffectiveFromDesc(Long employeeId);

	Optional<SalaryHistory> findTopByEmployeeIdOrderByEffectiveFromDesc(Long employeeId);

	@Query("""
			SELECT s FROM SalaryHistory s
			WHERE s.employee.id = :employeeId
			AND s.effectiveFrom <= :date
			AND (s.effectiveTo IS NULL OR s.effectiveTo >= :date)
			""")
	Optional<SalaryHistory> findSalaryAsOfDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);

	boolean existsByEmployeeIdAndEffectiveToIsNull(Long employeeId);
	
	boolean existsByEmployeeId(Long employeeId);
}
