package com.acme.salary_management.service.imp;


import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acme.salary_management.dto.EmployeeRequest;
import com.acme.salary_management.dto.EmployeeResponse;
import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.entity.EmploymentStatus;
import com.acme.salary_management.exception.DuplicateResourceException;
import com.acme.salary_management.exception.ResourceNotFoundException;
import com.acme.salary_management.mapper.EmployeeMapper;
import com.acme.salary_management.repository.EmployeeRepository;
import com.acme.salary_management.service.EmployeeService;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeResponse createEmployee(
            EmployeeRequest request) {

        if (employeeRepository.existsByEmployeeCode(
                request.employeeCode().trim())) {

            throw new DuplicateResourceException(
                "Employee code already exists"
            );
        }

        String email = request.email().trim().toLowerCase();

        if (employeeRepository.existsByEmail(email)) {

            throw new DuplicateResourceException(
                "Email already exists"
            );
        }

        Employee employee = employeeMapper.toEntity(request);

        Employee savedEmployee =
            employeeRepository.save(employee);

        return employeeMapper.toResponse(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {

        Employee employee = findEmployee(id);

        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(
            Pageable pageable) {

        return employeeRepository.findAll(pageable)
            .map(employeeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> searchEmployees(
            String keyword,
            String department,
            String country,
            String status,
            Pageable pageable) {

        EmploymentStatus employmentStatus = null;

        if (status != null && !status.isBlank()) {
            try {
                employmentStatus = EmploymentStatus.valueOf(
                    status.trim().toUpperCase()
                );
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(
                    "Invalid employment status: " + status
                );
            }
        }

        return employeeRepository.searchEmployees(
            normalize(keyword),
            normalize(department),
            normalize(country),
            employmentStatus,
            pageable
        ).map(employeeMapper::toResponse);
    }

    @Override
    public EmployeeResponse updateEmployee(
            Long id,
            EmployeeRequest request) {

        Employee employee = findEmployee(id);

        String email = request.email().trim().toLowerCase();

        if (employeeRepository.existsByEmailAndIdNot(
                email, id)) {

            throw new DuplicateResourceException(
                "Email already belongs to another employee"
            );
        }

        employeeMapper.updateEntity(employee, request);

        Employee updatedEmployee =
            employeeRepository.save(employee);

        return employeeMapper.toResponse(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {

        Employee employee = findEmployee(id);

        // Soft delete to preserve salary history.
        employee.setStatus(EmploymentStatus.INACTIVE);

        employeeRepository.save(employee);
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Employee not found with ID: " + id
                )
            );
    }

    private String normalize(String value) {
        return value == null || value.isBlank()
            ? null
            : value.trim();
    }
}
