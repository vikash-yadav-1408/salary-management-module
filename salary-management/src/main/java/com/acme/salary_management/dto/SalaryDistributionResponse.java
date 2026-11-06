package com.acme.salary_management.dto;

import java.math.BigDecimal;
import java.util.List;

public record SalaryDistributionResponse(

    String currency,
    BigDecimal minimumSalary,
    BigDecimal maximumSalary,
    List<SalaryBand> bands

) {

    public record SalaryBand(
        String label,
        BigDecimal fromSalary,
        BigDecimal toSalary,
        long employeeCount
    ) {}
}