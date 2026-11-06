package com.acme.salary_management.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.acme.salary_management.dto.CompensationResponse;
import com.acme.salary_management.dto.SalaryRequest;
import com.acme.salary_management.dto.SalaryResponse;
import com.acme.salary_management.service.SalaryService;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

	private final SalaryService salaryService;

	// Assign initial salary
	@PostMapping
	public ResponseEntity<SalaryResponse> assignSalary(@Valid @RequestBody SalaryRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(salaryService.assignSalary(request));
	}

	// Create immediate or future-dated salary revision
	@PostMapping("/revisions")
	public ResponseEntity<SalaryResponse> reviseSalary(@Valid @RequestBody SalaryRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(salaryService.reviseSalary(request));
	}

	// Retrieve current effective salary
	@GetMapping("/{employeeId}")
	public ResponseEntity<SalaryResponse> getCurrentSalary(@PathVariable Long employeeId) {

		return ResponseEntity.ok(salaryService.getCurrentSalary(employeeId));
	}

	// Retrieve complete salary history
	@GetMapping("/history/{employeeId}")
	public ResponseEntity<List<SalaryResponse>> getSalaryHistory(@PathVariable Long employeeId) {

		return ResponseEntity.ok(salaryService.getSalaryHistory(employeeId));
	}

	// Retrieve annual compensation
	@GetMapping("/{employeeId}/compensation")
	public ResponseEntity<CompensationResponse> getCompensation(@PathVariable Long employeeId) {

		return ResponseEntity.ok(salaryService.getCompensation(employeeId));
	}
}
