package com.acme.salary_management.dto;

import java.math.BigDecimal;

public record DepartmentAnalyticsResponse(

    String department,
    String currency,
    Long employeeCount,
    BigDecimal totalBaseSalary,
    BigDecimal totalBonus,
    BigDecimal totalCompensation,
    BigDecimal averageSalary

) {}