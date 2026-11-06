package com.acme.salary_management.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.acme.salary_management.dto.SalaryRequest;
import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.entity.SalaryHistory;
import com.acme.salary_management.exception.ResourceNotFoundException;
import com.acme.salary_management.mapper.SalaryMapper;
import com.acme.salary_management.repository.EmployeeRepository;
import com.acme.salary_management.repository.SalaryHistoryRepository;
import com.acme.salary_management.service.imp.SalaryServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaryServiceTest {

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private SalaryHistoryRepository salaryRepository;

	@Mock
	private SalaryMapper salaryMapper;

	@InjectMocks
	private SalaryServiceImpl salaryService;

	private Employee employee;
	private SalaryHistory previousSalary;

	@BeforeEach
	void setUp() {

		employee = new Employee();
		employee.setId(1L);
		employee.setEmployeeCode("EMP001");

		previousSalary = new SalaryHistory();
		previousSalary.setId(1L);
		previousSalary.setEmployee(employee);
		previousSalary.setBaseSalary(new BigDecimal("600000"));
		previousSalary.setBonus(new BigDecimal("50000"));
		previousSalary.setCurrency("INR");
		previousSalary.setEffectiveFrom(LocalDate.of(2025, 1, 1));
	}

	@Test
	void shouldCreateFutureSalaryRevision() {

		SalaryRequest request = new SalaryRequest(1L, new BigDecimal("750000"), new BigDecimal("75000"), "INR",
				LocalDate.of(2027, 1, 1));

		SalaryHistory newSalary = new SalaryHistory();

		newSalary.setEmployee(employee);
		newSalary.setBaseSalary(new BigDecimal("750000"));
		newSalary.setBonus(new BigDecimal("75000"));
		newSalary.setCurrency("INR");
		newSalary.setEffectiveFrom(LocalDate.of(2027, 1, 1));

		when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

		when(salaryRepository.findTopByEmployeeIdOrderByEffectiveFromDesc(1L)).thenReturn(Optional.of(previousSalary));

		when(salaryMapper.toEntity(employee, request.baseSalary(), request.bonus(), "INR", request.effectiveFrom()))
				.thenReturn(newSalary);

		when(salaryRepository.save(newSalary)).thenReturn(newSalary);

		var response = salaryService.reviseSalary(request);

		assertEquals(LocalDate.of(2026, 12, 31), previousSalary.getEffectiveTo());

		assertEquals(LocalDate.of(2027, 1, 1), response.effectiveFrom());

		verify(salaryRepository, times(2)).save(any());
	}

	@Test
	void shouldRejectRevisionWithInvalidEffectiveDate() {

		SalaryRequest request = new SalaryRequest(1L, new BigDecimal("750000"), BigDecimal.ZERO, "INR",
				LocalDate.of(2024, 1, 1));

		when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

		when(salaryRepository.findTopByEmployeeIdOrderByEffectiveFromDesc(1L)).thenReturn(Optional.of(previousSalary));

		assertThrows(IllegalArgumentException.class, () -> salaryService.reviseSalary(request));

		verify(salaryRepository, never()).save(any());
	}

	@Test
	void shouldThrowExceptionWhenEmployeeNotFound() {

		when(employeeRepository.findById(100L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> salaryService.getCurrentSalary(100L));
	}
}
