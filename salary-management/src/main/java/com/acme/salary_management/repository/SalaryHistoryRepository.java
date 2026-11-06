package com.acme.salary_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acme.salary_management.entity.EmploymentStatus;
import com.acme.salary_management.entity.SalaryHistory;
import com.acme.salary_management.repository.projections.CountrySalaryProjection;
import com.acme.salary_management.repository.projections.CurrencySalaryProjection;
import com.acme.salary_management.repository.projections.DepartmentSalaryProjection;
import com.acme.salary_management.repository.projections.SalaryAmountProjection;

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

	@Query("""
			SELECT
			    s.currency AS currency,
			    COUNT(s.id) AS employeeCount,
			    SUM(s.baseSalary) AS totalBaseSalary,
			    SUM(s.bonus) AS totalBonus,
			    AVG(s.baseSalary) AS averageSalary,
			    MIN(s.baseSalary) AS minimumSalary,
			    MAX(s.baseSalary) AS maximumSalary
			FROM SalaryHistory s
			JOIN s.employee e
			WHERE e.status = :status
			  AND s.effectiveFrom <= :asOf
			  AND (s.effectiveTo IS NULL OR s.effectiveTo >= :asOf)
			GROUP BY s.currency
			ORDER BY s.currency
			""")
	List<CurrencySalaryProjection> getSalarySummaryByCurrency(@Param("status") EmploymentStatus status,
			@Param("asOf") LocalDate asOf);

	@Query("""
			SELECT
			    e.department AS department,
			    s.currency AS currency,
			    COUNT(s.id) AS employeeCount,
			    SUM(s.baseSalary) AS totalBaseSalary,
			    SUM(s.bonus) AS totalBonus,
			    AVG(s.baseSalary) AS averageSalary
			FROM SalaryHistory s
			JOIN s.employee e
			WHERE e.status = :status
			  AND s.effectiveFrom <= :asOf
			  AND (s.effectiveTo IS NULL OR s.effectiveTo >= :asOf)
			GROUP BY e.department, s.currency
			ORDER BY e.department, s.currency
			""")
	List<DepartmentSalaryProjection> getDepartmentSalaryAnalytics(@Param("status") EmploymentStatus status,
			@Param("asOf") LocalDate asOf);

	@Query("""
			SELECT
			    e.country AS country,
			    s.currency AS currency,
			    COUNT(s.id) AS employeeCount,
			    SUM(s.baseSalary) AS totalBaseSalary,
			    SUM(s.bonus) AS totalBonus,
			    AVG(s.baseSalary) AS averageSalary
			FROM SalaryHistory s
			JOIN s.employee e
			WHERE e.status = :status
			  AND s.effectiveFrom <= :asOf
			  AND (s.effectiveTo IS NULL OR s.effectiveTo >= :asOf)
			GROUP BY e.country, s.currency
			ORDER BY e.country, s.currency
			""")
	List<CountrySalaryProjection> getCountrySalaryAnalytics(@Param("status") EmploymentStatus status,
			@Param("asOf") LocalDate asOf);

	@Query("""
			SELECT
			    s.currency AS currency,
			    s.baseSalary AS baseSalary
			FROM SalaryHistory s
			JOIN s.employee e
			WHERE e.status = :status
			  AND s.effectiveFrom <= :asOf
			  AND (s.effectiveTo IS NULL OR s.effectiveTo >= :asOf)
			""")
	List<SalaryAmountProjection> getCurrentSalaryAmounts(@Param("status") EmploymentStatus status,
			@Param("asOf") LocalDate asOf);
}
