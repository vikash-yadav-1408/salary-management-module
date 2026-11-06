package com.acme.salary_management.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalaryRequest(

    @NotNull(message = "Employee ID is required")
    @Positive(message = "Employee ID must be positive")
    Long employeeId,

    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0.01",
        message = "Base salary must be greater than zero")
    @Digits(integer = 17, fraction = 2)
    BigDecimal baseSalary,

    @NotNull(message = "Bonus is required")
    @DecimalMin(value = "0.00")
    @Digits(integer = 17, fraction = 2)
    BigDecimal bonus,

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Za-z]{3}$",
        message = "Currency must be a three-letter code")
    String currency,

    @NotNull(message = "Effective date is required")
    LocalDate effectiveFrom

) {}