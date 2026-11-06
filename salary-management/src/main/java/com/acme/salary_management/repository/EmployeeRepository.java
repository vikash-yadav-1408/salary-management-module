package com.acme.salary_management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.entity.EmploymentStatus;

import java.util.Optional;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeCode(
        String employeeCode
    );

    boolean existsByEmployeeCode(
        String employeeCode
    );

    boolean existsByEmail(String email);

    Page<Employee> findByDepartment(
        String department,
        Pageable pageable
    );

    Page<Employee> findByCountry(
        String country,
        Pageable pageable
    );

    long countByStatus(EmploymentStatus status);
    
    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("""
        SELECT e FROM Employee e
        WHERE
            (:keyword IS NULL OR
                LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:department IS NULL OR e.department = :department)
        AND (:country IS NULL OR e.country = :country)
        AND (:status IS NULL OR e.status = :status)
        """)
    Page<Employee> searchEmployees(
        @Param("keyword") String keyword,
        @Param("department") String department,
        @Param("country") String country,
        @Param("status") EmploymentStatus status,
        Pageable pageable
    );
}
