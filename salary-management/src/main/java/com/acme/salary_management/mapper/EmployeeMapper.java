package com.acme.salary_management.mapper;


import org.springframework.stereotype.Component;

import com.acme.salary_management.dto.EmployeeRequest;
import com.acme.salary_management.dto.EmployeeResponse;
import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.entity.EmploymentStatus;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequest request) {

        Employee employee = new Employee();

        employee.setEmployeeCode(
            request.employeeCode().trim()
        );

        employee.setFirstName(
            request.firstName().trim()
        );

        employee.setLastName(
            request.lastName().trim()
        );

        employee.setEmail(
            request.email().trim().toLowerCase()
        );

        employee.setDepartment(
            request.department().trim()
        );

        employee.setJobTitle(
            request.jobTitle().trim()
        );

        employee.setCountry(
            request.country().trim()
        );

        employee.setJoiningDate(
            request.joiningDate()
        );

        employee.setStatus(EmploymentStatus.ACTIVE);

        return employee;
    }

    public EmployeeResponse toResponse(Employee employee) {

        String fullName =
            employee.getFirstName() + " " +
            employee.getLastName();

        return new EmployeeResponse(
            employee.getId(),
            employee.getEmployeeCode(),
            employee.getFirstName(),
            employee.getLastName(),
            fullName,
            employee.getEmail(),
            employee.getDepartment(),
            employee.getJobTitle(),
            employee.getCountry(),
            employee.getJoiningDate(),
            employee.getStatus()
        );
    }

    public void updateEntity(
            Employee employee,
            EmployeeRequest request) {

        employee.setFirstName(
            request.firstName().trim()
        );

        employee.setLastName(
            request.lastName().trim()
        );

        employee.setEmail(
            request.email().trim().toLowerCase()
        );

        employee.setDepartment(
            request.department().trim()
        );

        employee.setJobTitle(
            request.jobTitle().trim()
        );

        employee.setCountry(
            request.country().trim()
        );

        employee.setJoiningDate(
            request.joiningDate()
        );
    }
}
