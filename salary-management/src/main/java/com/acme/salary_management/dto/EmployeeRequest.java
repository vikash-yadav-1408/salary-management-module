package com.acme.salary_management.dto;


import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record EmployeeRequest(

    @NotBlank(message = "Employee code is required")
    @Size(max = 30)
    String employeeCode,

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    String lastName,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    String email,

    @NotBlank(message = "Department is required")
    String department,

    @NotBlank(message = "Job title is required")
    String jobTitle,

    @NotBlank(message = "Country is required")
    String country,

    @NotNull(message = "Joining date is required")
    @PastOrPresent(message = "Joining date cannot be in the future")
    LocalDate joiningDate

) {}