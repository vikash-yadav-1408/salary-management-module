package com.acme.salary_management.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeedDataGeneratorTest {

    private final SeedDataGenerator generator =
        new SeedDataGenerator();

    @Test
    void shouldGenerateDeterministicEmployeeData() {

        var first = generator.generate(1);
        var second = generator.generate(1);

        assertEquals(
            first.employee().getEmployeeCode(),
            second.employee().getEmployeeCode()
        );

        assertEquals(
            first.employee().getEmail(),
            second.employee().getEmail()
        );

        assertEquals(
            first.salaryHistory().getBaseSalary(),
            second.salaryHistory().getBaseSalary()
        );

        assertEquals(
            first.salaryHistory().getCurrency(),
            second.salaryHistory().getCurrency()
        );
    }

    @Test
    void shouldGenerateUniqueEmployeeCodes() {

        var first = generator.generate(1);
        var second = generator.generate(2);

        assertNotEquals(
            first.employee().getEmployeeCode(),
            second.employee().getEmployeeCode()
        );
    }

    @Test
    void shouldGenerateSalaryForEmployee() {

        var data = generator.generate(1);

        assertNotNull(data.salaryHistory().getBaseSalary());

        assertTrue(
            data.salaryHistory().getBaseSalary()
                .signum() > 0
        );

        assertNotNull(data.salaryHistory().getCurrency());

        assertEquals(
            data.employee(),
            data.salaryHistory().getEmployee()
        );
    }
}
