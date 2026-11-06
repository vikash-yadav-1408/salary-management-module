package com.acme.salary_management.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.entity.SalaryHistory;
import com.acme.salary_management.repository.EmployeeRepository;
import com.acme.salary_management.repository.SalaryHistoryRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "app.seed.enabled",
    havingValue = "true"
)
public class DataSeeder implements ApplicationRunner {

    private static final int TOTAL_EMPLOYEES = 10_000;
    private static final int BATCH_SIZE = 500;

    private final EmployeeRepository employeeRepository;
    private final SalaryHistoryRepository salaryHistoryRepository;
    private final SeedDataGenerator seedDataGenerator;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        long existingEmployees = employeeRepository.count();

        if (existingEmployees > 0) {
            log.info(
                "Seeding skipped. Existing employees: {}",
                existingEmployees
            );
            return;
        }

        long startTime = System.currentTimeMillis();

        log.info("Starting employee data seeding...");

        for (int start = 1;
             start <= TOTAL_EMPLOYEES;
             start += BATCH_SIZE) {

            int end = Math.min(
                start + BATCH_SIZE - 1,
                TOTAL_EMPLOYEES
            );

            List<SeedDataGenerator.EmployeeSeed> batch =
                new ArrayList<>();

            for (int i = start; i <= end; i++) {
                batch.add(seedDataGenerator.generate(i));
            }

            List<Employee> employees = batch.stream()
                .map(SeedDataGenerator.EmployeeSeed::employee)
                .toList();

            List<Employee> savedEmployees =
                employeeRepository.saveAllAndFlush(employees);

            List<SalaryHistory> salaryRecords =
                new ArrayList<>();

            for (int i = 0; i < batch.size(); i++) {

                SalaryHistory salary =
                    batch.get(i).salaryHistory();

                salary.setEmployee(savedEmployees.get(i));

                salaryRecords.add(salary);
            }

            salaryHistoryRepository.saveAllAndFlush(
                salaryRecords
            );

            log.info(
                "Seeded employees: {} / {}",
                end,
                TOTAL_EMPLOYEES
            );
        }

        long duration =
            System.currentTimeMillis() - startTime;

        log.info(
            "Seeding completed. Employees: {}, Salary records: {}, Time: {} ms",
            employeeRepository.count(),
            salaryHistoryRepository.count(),
            duration
        );
    }
}
