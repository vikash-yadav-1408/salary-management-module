package com.acme.salary_management.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.acme.salary_management.entity.EmploymentStatus;
import com.acme.salary_management.repository.EmployeeRepository;
import com.acme.salary_management.repository.SalaryHistoryRepository;
import com.acme.salary_management.repository.projections.CurrencySalaryProjection;
import com.acme.salary_management.service.imp.DashboardServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private SalaryHistoryRepository salaryRepository;

	@InjectMocks
	private DashboardServiceImpl dashboardService;

	@Test
	void shouldReturnDashboardSummary() {

		when(employeeRepository.count()).thenReturn(10000L);

		when(employeeRepository.countByStatus(EmploymentStatus.ACTIVE)).thenReturn(9000L);

		when(employeeRepository.countByStatus(EmploymentStatus.INACTIVE)).thenReturn(500L);

		when(employeeRepository.countByStatus(EmploymentStatus.ON_LEAVE)).thenReturn(300L);

		when(employeeRepository.countByStatus(EmploymentStatus.TERMINATED)).thenReturn(200L);

		CurrencySalaryProjection projection = mock(CurrencySalaryProjection.class);

		when(projection.getCurrency()).thenReturn("INR");
		when(projection.getEmployeeCount()).thenReturn(100L);

		when(projection.getTotalBaseSalary()).thenReturn(new BigDecimal("100000000"));

		when(projection.getTotalBonus()).thenReturn(new BigDecimal("10000000"));

		when(projection.getAverageSalary()).thenReturn(new BigDecimal("1000000"));

		when(projection.getMinimumSalary()).thenReturn(new BigDecimal("600000"));

		when(projection.getMaximumSalary()).thenReturn(new BigDecimal("2000000"));

		when(salaryRepository.getSalarySummaryByCurrency(eq(EmploymentStatus.ACTIVE), any(LocalDate.class)))
				.thenReturn(List.of(projection));

		var response = dashboardService.getDashboardSummary(LocalDate.of(2026, 11, 6));

		assertEquals(10000L, response.totalEmployees());
		assertEquals(9000L, response.activeEmployees());

		assertEquals(1, response.salarySummaries().size());

		assertEquals(new BigDecimal("110000000"), response.salarySummaries().get(0).totalCompensation());
	}

	@Test
	void shouldNotCombineDifferentCurrencies() {

		when(employeeRepository.count()).thenReturn(100L);

		when(employeeRepository.countByStatus(any())).thenReturn(0L);

		CurrencySalaryProjection inr = mock(CurrencySalaryProjection.class);

		when(inr.getCurrency()).thenReturn("INR");
		when(inr.getEmployeeCount()).thenReturn(10L);
		when(inr.getTotalBaseSalary()).thenReturn(new BigDecimal("1000000"));
		when(inr.getTotalBonus()).thenReturn(new BigDecimal("100000"));
		when(inr.getAverageSalary()).thenReturn(new BigDecimal("100000"));
		when(inr.getMinimumSalary()).thenReturn(new BigDecimal("50000"));
		when(inr.getMaximumSalary()).thenReturn(new BigDecimal("200000"));

		CurrencySalaryProjection usd = mock(CurrencySalaryProjection.class);

		when(usd.getCurrency()).thenReturn("USD");
		when(usd.getEmployeeCount()).thenReturn(5L);
		when(usd.getTotalBaseSalary()).thenReturn(new BigDecimal("500000"));
		when(usd.getTotalBonus()).thenReturn(new BigDecimal("50000"));
		when(usd.getAverageSalary()).thenReturn(new BigDecimal("100000"));
		when(usd.getMinimumSalary()).thenReturn(new BigDecimal("50000"));
		when(usd.getMaximumSalary()).thenReturn(new BigDecimal("200000"));

		when(salaryRepository.getSalarySummaryByCurrency(eq(EmploymentStatus.ACTIVE), any(LocalDate.class)))
				.thenReturn(List.of(inr, usd));

		var response = dashboardService.getDashboardSummary(LocalDate.of(2026, 11, 6));

		assertEquals(2, response.salarySummaries().size());

		assertEquals("INR", response.salarySummaries().get(0).currency());

		assertEquals("USD", response.salarySummaries().get(1).currency());
	}
}