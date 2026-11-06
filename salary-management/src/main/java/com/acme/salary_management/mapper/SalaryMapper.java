package com.acme.salary_management.mapper;

import org.springframework.stereotype.Component;

import com.acme.salary_management.dto.SalaryResponse;
import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.entity.SalaryHistory;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class SalaryMapper {

	public SalaryHistory toEntity(Employee employee, BigDecimal baseSalary, BigDecimal bonus, String currency,
			LocalDate effectiveFrom) {

		SalaryHistory salary = new SalaryHistory();

		salary.setEmployee(employee);
		salary.setBaseSalary(baseSalary);
		salary.setBonus(bonus);
		salary.setCurrency(currency);
		salary.setEffectiveFrom(effectiveFrom);

		return salary;
	}

	public SalaryResponse toResponse(SalaryHistory salary, LocalDate asOfDate) {

		Employee employee = salary.getEmployee();

		BigDecimal bonus = salary.getBonus() == null ? BigDecimal.ZERO : salary.getBonus();

		BigDecimal annualCompensation = salary.getBaseSalary().add(bonus);

		boolean current = !salary.getEffectiveFrom().isAfter(asOfDate)
				&& (salary.getEffectiveTo() == null || !salary.getEffectiveTo().isBefore(asOfDate));

		return new SalaryResponse(salary.getId(), employee.getId(), employee.getEmployeeCode(),
				employee.getFirstName() + " " + employee.getLastName(), salary.getBaseSalary(), bonus,
				annualCompensation, salary.getCurrency(), salary.getEffectiveFrom(), salary.getEffectiveTo(), current);
	}
}