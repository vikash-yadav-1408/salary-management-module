package com.acme.salary_management.service;

import java.time.LocalDate;
import java.util.List;

import com.acme.salary_management.dto.CountryAnalyticsResponse;
import com.acme.salary_management.dto.DashboardSummaryResponse;
import com.acme.salary_management.dto.DepartmentAnalyticsResponse;
import com.acme.salary_management.dto.EmployeeDistributionResponse;
import com.acme.salary_management.dto.SalaryDistributionResponse;

public interface DashboardService {

	DashboardSummaryResponse getDashboardSummary(LocalDate asOf);

	List<DepartmentAnalyticsResponse> getDepartmentAnalytics(LocalDate asOf);

	List<CountryAnalyticsResponse> getCountryAnalytics(LocalDate asOf);

	EmployeeDistributionResponse getEmployeeDistribution();

	List<SalaryDistributionResponse> getSalaryDistribution(LocalDate asOf);
}