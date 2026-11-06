package com.acme.salary_management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalaryResponse(

    Long id,
    Long employeeId,
    String employeeCode,
    String employeeName,
    BigDecimal baseSalary,
    BigDecimal bonus,
    BigDecimal annualCompensation,
    String currency,
    LocalDate effectiveFrom,
    LocalDate effectiveTo,
    boolean current

) {}
