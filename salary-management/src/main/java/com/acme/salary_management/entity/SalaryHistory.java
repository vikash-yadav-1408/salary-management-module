package com.acme.salary_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "salary_history",
    indexes = {
        @Index(name = "idx_salary_employee",
               columnList = "employee_id"),
        @Index(name = "idx_salary_effective_from",
               columnList = "effective_from")
    }
)
@Getter
@Setter
public class SalaryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "employee_id",
        nullable = false
    )
    private Employee employee;

    @Column(name = "base_salary",
            nullable = false,
            precision = 19,
            scale = 2)
    private BigDecimal baseSalary;

    @Column(precision = 19, scale = 2)
    private BigDecimal bonus = BigDecimal.ZERO;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "effective_from",
            nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "created_at",
            nullable = false,
            updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}