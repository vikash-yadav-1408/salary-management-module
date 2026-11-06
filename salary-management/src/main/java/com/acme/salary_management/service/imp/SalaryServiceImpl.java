package com.acme.salary_management.service.imp;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acme.salary_management.dto.CompensationResponse;
import com.acme.salary_management.dto.SalaryRequest;
import com.acme.salary_management.dto.SalaryResponse;
import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.entity.SalaryHistory;
import com.acme.salary_management.exception.DuplicateResourceException;
import com.acme.salary_management.exception.ResourceNotFoundException;
import com.acme.salary_management.mapper.SalaryMapper;
import com.acme.salary_management.repository.EmployeeRepository;
import com.acme.salary_management.repository.SalaryHistoryRepository;
import com.acme.salary_management.service.SalaryService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryServiceImpl implements SalaryService {

	private final EmployeeRepository employeeRepository;
	private final SalaryHistoryRepository salaryRepository;
	private final SalaryMapper salaryMapper;

	@Override
	public SalaryResponse assignSalary(SalaryRequest request) {

		Employee employee = findEmployee(request.employeeId());

		if (salaryRepository.existsByEmployeeId(employee.getId())) {

			throw new DuplicateResourceException(
					"Employee already has a salary record. " + "Use salary revision instead.");
		}

		validateCurrency(request.currency());

		SalaryHistory salary = salaryMapper.toEntity(employee, request.baseSalary(), request.bonus(),
				request.currency().toUpperCase(Locale.ROOT), request.effectiveFrom());

		SalaryHistory saved = salaryRepository.save(salary);

		return salaryMapper.toResponse(saved, LocalDate.now());
	}

	@Override
	public SalaryResponse reviseSalary(SalaryRequest request) {

		Employee employee = findEmployee(request.employeeId());

		validateCurrency(request.currency());

		SalaryHistory previousSalary = salaryRepository.findTopByEmployeeIdOrderByEffectiveFromDesc(employee.getId())
				.orElseThrow(() -> new ResourceNotFoundException("No salary record found for employee"));

		LocalDate newEffectiveDate = request.effectiveFrom();

		if (!newEffectiveDate.isAfter(previousSalary.getEffectiveFrom())) {

			throw new IllegalArgumentException(
					"New salary effective date must be after " + "the latest salary effective date");
		}

		LocalDate previousEndDate = newEffectiveDate.minusDays(1);

		previousSalary.setEffectiveTo(previousEndDate);

		salaryRepository.save(previousSalary);

		SalaryHistory newSalary = salaryMapper.toEntity(employee, request.baseSalary(), request.bonus(),
				request.currency().toUpperCase(Locale.ROOT), newEffectiveDate);

		SalaryHistory savedSalary = salaryRepository.save(newSalary);

		return salaryMapper.toResponse(savedSalary, LocalDate.now());
	}

	@Override
	@Transactional(readOnly = true)
	public SalaryResponse getCurrentSalary(Long employeeId) {

		findEmployee(employeeId);

		SalaryHistory salary = salaryRepository.findSalaryAsOfDate(employeeId, LocalDate.now()).orElseThrow(
				() -> new ResourceNotFoundException("No effective salary found for employee: " + employeeId));

		return salaryMapper.toResponse(salary, LocalDate.now());
	}

	@Override
	@Transactional(readOnly = true)
	public List<SalaryResponse> getSalaryHistory(Long employeeId) {

		findEmployee(employeeId);

		return salaryRepository.findByEmployeeIdOrderByEffectiveFromDesc(employeeId).stream()
				.map(salary -> salaryMapper.toResponse(salary, LocalDate.now())).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public CompensationResponse getCompensation(Long employeeId) {

		SalaryResponse salary = getCurrentSalary(employeeId);

		return new CompensationResponse(salary.employeeId(), salary.employeeCode(), salary.employeeName(),
				salary.baseSalary(), salary.bonus(), salary.annualCompensation(), salary.currency());
	}

	private Employee findEmployee(Long employeeId) {

		return employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
	}

	private void validateCurrency(String currencyCode) {

		try {
			Currency.getInstance(currencyCode.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException | NullPointerException ex) {
			throw new IllegalArgumentException("Unsupported currency code: " + currencyCode);
		}
	}
}
