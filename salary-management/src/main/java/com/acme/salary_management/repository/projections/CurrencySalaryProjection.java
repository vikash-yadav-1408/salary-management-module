package com.acme.salary_management.repository.projections; 

import java.math.BigDecimal;

public interface CurrencySalaryProjection {

    String getCurrency();

    Long getEmployeeCount();

    BigDecimal getTotalBaseSalary();

    BigDecimal getTotalBonus();

    BigDecimal getAverageSalary();

    BigDecimal getMinimumSalary();

    BigDecimal getMaximumSalary();
}
