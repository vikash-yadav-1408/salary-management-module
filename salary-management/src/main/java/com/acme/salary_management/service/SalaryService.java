package com.acme.salary_management.service;

import java.util.List;

import com.acme.salary_management.dto.CompensationResponse;
import com.acme.salary_management.dto.SalaryRequest;
import com.acme.salary_management.dto.SalaryResponse;

public interface SalaryService {

    SalaryResponse assignSalary(SalaryRequest request);

    SalaryResponse reviseSalary(SalaryRequest request);

    SalaryResponse getCurrentSalary(Long employeeId);

    List<SalaryResponse> getSalaryHistory(Long employeeId);

    CompensationResponse getCompensation(Long employeeId);
}
