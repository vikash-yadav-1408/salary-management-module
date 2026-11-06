package com.acme.salary_management.dto;

import java.util.List;

public record EmployeeDistributionResponse(

    List<DistributionItem> departmentDistribution,
    List<DistributionItem> countryDistribution

) {

    public record DistributionItem(
        String name,
        Long employeeCount
    ) {}
}
