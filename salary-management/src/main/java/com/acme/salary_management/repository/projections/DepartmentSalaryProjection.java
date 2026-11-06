package com.acme.salary_management.repository.projections;

import java.math.BigDecimal;

public interface DepartmentSalaryProjection {

    String getDepartment();

    String getCurrency();

    Long getEmployeeCount();

    BigDecimal getTotalBaseSalary();

    BigDecimal getTotalBonus();

    BigDecimal getAverageSalary();
}
