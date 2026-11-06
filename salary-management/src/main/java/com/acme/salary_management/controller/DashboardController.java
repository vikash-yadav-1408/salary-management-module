package com.acme.salary_management.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.acme.salary_management.dto.CountryAnalyticsResponse;
import com.acme.salary_management.dto.DashboardSummaryResponse;
import com.acme.salary_management.dto.DepartmentAnalyticsResponse;
import com.acme.salary_management.dto.EmployeeDistributionResponse;
import com.acme.salary_management.dto.SalaryDistributionResponse;
import com.acme.salary_management.service.DashboardService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

	private final DashboardService dashboardService;

	@GetMapping("/summary")
	public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
		return ResponseEntity.ok(dashboardService.getDashboardSummary(asOf));
	}

	@GetMapping("/department-wise")
	public ResponseEntity<List<DepartmentAnalyticsResponse>> getDepartmentAnalytics(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
		return ResponseEntity.ok(dashboardService.getDepartmentAnalytics(asOf));
	}

	@GetMapping("/country-wise")
	public ResponseEntity<List<CountryAnalyticsResponse>> getCountryAnalytics(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
		return ResponseEntity.ok(dashboardService.getCountryAnalytics(asOf));
	}

	@GetMapping("/employee-distribution")
	public ResponseEntity<EmployeeDistributionResponse> getEmployeeDistribution() {
		return ResponseEntity.ok(dashboardService.getEmployeeDistribution());
	}

	@GetMapping("/salary-distribution")
	public ResponseEntity<List<SalaryDistributionResponse>> getSalaryDistribution(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
		return ResponseEntity.ok(dashboardService.getSalaryDistribution(asOf));
	}
}
