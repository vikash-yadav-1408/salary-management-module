package com.acme.salary_management.repository.projections;

import java.math.BigDecimal;

public interface SalaryAmountProjection {

    String getCurrency();

    BigDecimal getBaseSalary();
}
