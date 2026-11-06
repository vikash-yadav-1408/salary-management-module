package com.acme.salary_management.service;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.acme.salary_management.dto.EmployeeRequest;
import com.acme.salary_management.dto.EmployeeResponse;

public interface EmployeeService {

    EmployeeResponse createEmployee(
        EmployeeRequest request
    );

    EmployeeResponse getEmployeeById(Long id);

    Page<EmployeeResponse> getAllEmployees(
        Pageable pageable
    );

    Page<EmployeeResponse> searchEmployees(
        String keyword,
        String department,
        String country,
        String status,
        Pageable pageable
    );

    EmployeeResponse updateEmployee(
        Long id,
        EmployeeRequest request
    );

    void deleteEmployee(Long id);
}
