package com.acme.salary_management.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "employees",
    indexes = {
        @Index(name = "idx_employee_code",
               columnList = "employee_code"),
        @Index(name = "idx_employee_department",
               columnList = "department"),
        @Index(name = "idx_employee_country",
               columnList = "country")
    }
)
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "employee_code",
        nullable = false,
        unique = true,
        length = 30
    )
    private String employeeCode;

    @Column(name = "first_name",
            nullable = false,
            length = 100)
    private String firstName;

    @Column(name = "last_name",
            nullable = false,
            length = 100)
    private String lastName;

    @Column(nullable = false,
            unique = true,
            length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "job_title",
            nullable = false,
            length = 100)
    private String jobTitle;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(name = "joining_date",
            nullable = false)
    private LocalDate joiningDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmploymentStatus status;
}
