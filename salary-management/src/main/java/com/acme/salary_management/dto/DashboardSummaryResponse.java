package com.acme.salary_management.dto;

import java.time.LocalDate;
import java.util.List;

public record DashboardSummaryResponse(

    LocalDate asOfDate,
    long totalEmployees,
    long activeEmployees,
    long inactiveEmployees,
    long employeesOnLeave,
    long terminatedEmployees,
    List<CurrencySalarySummary> salarySummaries

) {}