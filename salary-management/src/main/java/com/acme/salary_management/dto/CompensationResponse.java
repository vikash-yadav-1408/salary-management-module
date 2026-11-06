package com.acme.salary_management.dto;

import java.math.BigDecimal;

public record CompensationResponse(

    Long employeeId,
    String employeeCode,
    String employeeName,
    BigDecimal baseSalary,
    BigDecimal bonus,
    BigDecimal annualCompensation,
    String currency

) {}
