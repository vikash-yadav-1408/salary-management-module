package com.acme.salary_management.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.acme.salary_management.dto.EmployeeRequest;
import com.acme.salary_management.dto.EmployeeResponse;
import com.acme.salary_management.entity.Employee;
import com.acme.salary_management.entity.EmploymentStatus;
import com.acme.salary_management.exception.DuplicateResourceException;
import com.acme.salary_management.mapper.EmployeeMapper;
import com.acme.salary_management.repository.EmployeeRepository;
import com.acme.salary_management.service.imp.EmployeeServiceImpl;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @Mock
    private EmployeeMapper mapper;

    @InjectMocks
    private EmployeeServiceImpl service;

    private Employee employee;
    private EmployeeRequest request;

    @BeforeEach
    void setUp() {

        employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeCode("EMP001");
        employee.setEmail("john@acme.com");
        employee.setStatus(EmploymentStatus.ACTIVE);

        request = new EmployeeRequest(
            "EMP001",
            "John",
            "Doe",
            "john@acme.com",
            "Engineering",
            "Software Engineer",
            "India",
            LocalDate.of(2024, 1, 15)
        );
    }

    @Test
    void shouldCreateEmployee() {

        when(repository.existsByEmployeeCode("EMP001"))
            .thenReturn(false);

        when(repository.existsByEmail("john@acme.com"))
            .thenReturn(false);

        when(mapper.toEntity(request))
            .thenReturn(employee);

        when(repository.save(employee))
            .thenReturn(employee);

        when(mapper.toResponse(employee))
            .thenReturn(new EmployeeResponse(
                1L, "EMP001", "John", "Doe",
                "John Doe", "john@acme.com",
                "Engineering", "Software Engineer",
                "India", LocalDate.of(2024, 1, 15),
                EmploymentStatus.ACTIVE
            ));

        EmployeeResponse response =
            service.createEmployee(request);

        assertEquals("EMP001", response.employeeCode());

        verify(repository).save(employee);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeCodeExists() {

        when(repository.existsByEmployeeCode("EMP001"))
            .thenReturn(true);

        assertThrows(
            DuplicateResourceException.class,
            () -> service.createEmployee(request)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldDeactivateEmployee() {

        when(repository.findById(1L))
            .thenReturn(Optional.of(employee));

        service.deleteEmployee(1L);

        assertEquals(
            EmploymentStatus.INACTIVE,
            employee.getStatus()
        );

        verify(repository).save(employee);
    }
}
