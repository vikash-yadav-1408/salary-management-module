package com.acme.salary_management.config;

import org.springframework.stereotype.Component;

import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.entity.EmploymentStatus;
import com.acme.salary_management.entity.SalaryHistory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Component
public class SeedDataGenerator {

    private static final String[] FIRST_NAMES = {
        "Aarav", "Rahul", "Vikash", "Amit",
        "Priya", "Neha", "Ananya", "Rohit",
        "John", "David", "Michael", "Emma",
        "Olivia", "Daniel", "Sophia", "James"
    };

    private static final String[] LAST_NAMES = {
        "Sharma", "Verma", "Patel", "Yadav",
        "Gupta", "Singh", "Kumar", "Mehta",
        "Smith", "Johnson", "Brown", "Wilson"
    };

    private static final String[] DEPARTMENTS = {
        "Engineering", "Human Resources", "Finance",
        "Marketing", "Sales", "Operations",
        "Customer Support", "Product"
    };

    private static final String[] JOB_TITLES = {
        "Software Engineer",
        "Senior Software Engineer",
        "Team Lead",
        "Engineering Manager",
        "Business Analyst",
        "HR Executive",
        "Financial Analyst",
        "Product Manager",
        "Sales Executive",
        "Operations Manager"
    };

    private static final Country[] COUNTRIES = {
        new Country("India", "INR", 600_000, 2_400_000),
        new Country("USA", "USD", 60_000, 180_000),
        new Country("UK", "GBP", 35_000, 100_000),
        new Country("Germany", "EUR", 40_000, 110_000),
        new Country("Canada", "CAD", 55_000, 140_000),
        new Country("Australia", "AUD", 60_000, 150_000),
        new Country("Singapore", "SGD", 45_000, 130_000)
    };

    public EmployeeSeed generate(int index) {

    	//This ensures that the generated details for a particular employee
    	//remain consistent across runs.
        Random random = new Random(42L + index);

        String firstName =
            FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];

        String lastName =
            LAST_NAMES[random.nextInt(LAST_NAMES.length)];

        Country country =
            COUNTRIES[random.nextInt(COUNTRIES.length)];

        LocalDate joiningDate =
            LocalDate.of(2020, 1, 1)
                .plusDays(random.nextInt(2192));

        Employee employee = new Employee();

        employee.setEmployeeCode(
            "ACME-" + String.format("%05d", index)
        );

        employee.setFirstName(firstName);
        employee.setLastName(lastName);

        employee.setEmail(
            firstName.toLowerCase() + "." +
            lastName.toLowerCase() + "." +
            index + "@acme.example"
        );

        employee.setDepartment(
            DEPARTMENTS[random.nextInt(DEPARTMENTS.length)]
        );

        employee.setJobTitle(
            JOB_TITLES[random.nextInt(JOB_TITLES.length)]
        );

        employee.setCountry(country.name());
        employee.setJoiningDate(joiningDate);
        employee.setStatus(generateStatus(random));

        long salaryAmount = country.minimumSalary()
            + random.nextLong(
                country.maximumSalary()
                    - country.minimumSalary() + 1
            );

        BigDecimal baseSalary =
            BigDecimal.valueOf(salaryAmount);

        int bonusPercentage = random.nextInt(21);

        BigDecimal bonus = baseSalary
            .multiply(BigDecimal.valueOf(bonusPercentage))
            .divide(BigDecimal.valueOf(100), 2,
                    java.math.RoundingMode.HALF_UP);

        SalaryHistory salaryHistory = new SalaryHistory();

        salaryHistory.setEmployee(employee);
        salaryHistory.setBaseSalary(baseSalary);
        salaryHistory.setBonus(bonus);
        salaryHistory.setCurrency(country.currency());
        salaryHistory.setEffectiveFrom(joiningDate);
        salaryHistory.setEffectiveTo(null);

        return new EmployeeSeed(employee, salaryHistory);
    }

    private EmploymentStatus generateStatus(Random random) {

        int value = random.nextInt(100);

        if (value < 90) {
            return EmploymentStatus.ACTIVE;
        } else if (value < 95) {
            return EmploymentStatus.INACTIVE;
        } else if (value < 98) {
            return EmploymentStatus.ON_LEAVE;
        }

        return EmploymentStatus.TERMINATED;
    }

    public record EmployeeSeed(
        Employee employee,
        SalaryHistory salaryHistory
    ) {}

    private record Country(
        String name,
        String currency,
        long minimumSalary,
        long maximumSalary
    ) {}
}
