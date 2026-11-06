package com.acme.salary_management.dto;

import java.time.LocalDate;

import com.acme.salary_management.entity.EmploymentStatus;

public record EmployeeResponse(

    Long id,
    String employeeCode,
    String firstName,
    String lastName,
    String fullName,
    String email,
    String department,
    String jobTitle,
    String country,
    LocalDate joiningDate,
    EmploymentStatus status

) {}
