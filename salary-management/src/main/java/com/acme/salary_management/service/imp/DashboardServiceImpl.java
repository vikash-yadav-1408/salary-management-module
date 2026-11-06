package com.acme.salary_management.service.imp;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acme.salary_management.dto.CountryAnalyticsResponse;
import com.acme.salary_management.dto.CurrencySalarySummary;
import com.acme.salary_management.dto.DashboardSummaryResponse;
import com.acme.salary_management.dto.DepartmentAnalyticsResponse;
import com.acme.salary_management.dto.EmployeeDistributionResponse;
import com.acme.salary_management.dto.SalaryDistributionResponse;
import com.acme.salary_management.entity.EmploymentStatus;
import com.acme.salary_management.repository.EmployeeRepository;
import com.acme.salary_management.repository.SalaryHistoryRepository;
import com.acme.salary_management.repository.projections.SalaryAmountProjection;
import com.acme.salary_management.service.DashboardService;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

	private static final int SALARY_BAND_COUNT = 5;

	private final EmployeeRepository employeeRepository;
	private final SalaryHistoryRepository salaryRepository;

	@Override
	public DashboardSummaryResponse getDashboardSummary(LocalDate asOf) {

		LocalDate date = normalizeDate(asOf);

		long totalEmployees = employeeRepository.count();

		long activeEmployees = employeeRepository.countByStatus(EmploymentStatus.ACTIVE);

		long inactiveEmployees = employeeRepository.countByStatus(EmploymentStatus.INACTIVE);

		long employeesOnLeave = employeeRepository.countByStatus(EmploymentStatus.ON_LEAVE);

		long terminatedEmployees = employeeRepository.countByStatus(EmploymentStatus.TERMINATED);

		List<CurrencySalarySummary> salarySummaries = salaryRepository
				.getSalarySummaryByCurrency(EmploymentStatus.ACTIVE, date).stream().map(projection -> {

					BigDecimal totalCompensation = projection.getTotalBaseSalary().add(projection.getTotalBonus());

					return new CurrencySalarySummary(projection.getCurrency(), projection.getEmployeeCount(),
							projection.getTotalBaseSalary(), projection.getTotalBonus(), totalCompensation,
							projection.getAverageSalary().setScale(2, RoundingMode.HALF_UP),
							projection.getMinimumSalary(), projection.getMaximumSalary());
				}).toList();

		return new DashboardSummaryResponse(date, totalEmployees, activeEmployees, inactiveEmployees, employeesOnLeave,
				terminatedEmployees, salarySummaries);
	}

	@Override
	public List<DepartmentAnalyticsResponse> getDepartmentAnalytics(LocalDate asOf) {

		LocalDate date = normalizeDate(asOf);

		return salaryRepository.getDepartmentSalaryAnalytics(EmploymentStatus.ACTIVE, date).stream().map(projection -> {

			BigDecimal totalCompensation = projection.getTotalBaseSalary().add(projection.getTotalBonus());

			return new DepartmentAnalyticsResponse(projection.getDepartment(), projection.getCurrency(),
					projection.getEmployeeCount(), projection.getTotalBaseSalary(), projection.getTotalBonus(),
					totalCompensation, projection.getAverageSalary().setScale(2, RoundingMode.HALF_UP));
		}).toList();
	}

	@Override
	public List<CountryAnalyticsResponse> getCountryAnalytics(LocalDate asOf) {

		LocalDate date = normalizeDate(asOf);

		return salaryRepository.getCountrySalaryAnalytics(EmploymentStatus.ACTIVE, date).stream().map(projection -> {

			BigDecimal totalCompensation = projection.getTotalBaseSalary().add(projection.getTotalBonus());

			return new CountryAnalyticsResponse(projection.getCountry(), projection.getCurrency(),
					projection.getEmployeeCount(), projection.getTotalBaseSalary(), projection.getTotalBonus(),
					totalCompensation, projection.getAverageSalary().setScale(2, RoundingMode.HALF_UP));
		}).toList();
	}

	@Override
	public EmployeeDistributionResponse getEmployeeDistribution() {

		List<EmployeeDistributionResponse.DistributionItem> departmentDistribution = employeeRepository
				.getEmployeeCountByDepartment(EmploymentStatus.ACTIVE).stream()
				.map(projection -> new EmployeeDistributionResponse.DistributionItem(projection.getName(),
						projection.getEmployeeCount()))
				.toList();

		List<EmployeeDistributionResponse.DistributionItem> countryDistribution = employeeRepository
				.getEmployeeCountByCountry(EmploymentStatus.ACTIVE).stream()
				.map(projection -> new EmployeeDistributionResponse.DistributionItem(projection.getName(),
						projection.getEmployeeCount()))
				.toList();

		return new EmployeeDistributionResponse(departmentDistribution, countryDistribution);
	}

	@Override
	public List<SalaryDistributionResponse> getSalaryDistribution(LocalDate asOf) {

		LocalDate date = normalizeDate(asOf);

		List<SalaryAmountProjection> salaries = salaryRepository.getCurrentSalaryAmounts(EmploymentStatus.ACTIVE, date);

		Map<String, List<BigDecimal>> salariesByCurrency = salaries.stream()
				.collect(Collectors.groupingBy(SalaryAmountProjection::getCurrency, LinkedHashMap::new,
						Collectors.mapping(SalaryAmountProjection::getBaseSalary, Collectors.toList())));

		return salariesByCurrency.entrySet().stream()
				.map(entry -> createSalaryDistribution(entry.getKey(), entry.getValue())).toList();
	}

	private SalaryDistributionResponse createSalaryDistribution(String currency, List<BigDecimal> amounts) {

		BigDecimal minimum = amounts.stream().min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);

		BigDecimal maximum = amounts.stream().max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);

		BigDecimal range = maximum.subtract(minimum);

		BigDecimal width = range.divide(BigDecimal.valueOf(SALARY_BAND_COUNT), 8, RoundingMode.HALF_UP);

		long[] counts = new long[SALARY_BAND_COUNT];

		for (BigDecimal amount : amounts) {

			int index;

			if (range.signum() == 0) {
				index = 0;
			} else {
				index = amount.subtract(minimum).divide(width, 0, RoundingMode.DOWN).intValue();

				index = Math.min(index, SALARY_BAND_COUNT - 1);
			}

			counts[index]++;
		}

		List<SalaryDistributionResponse.SalaryBand> bands = new ArrayList<>();

		for (int i = 0; i < SALARY_BAND_COUNT; i++) {

			BigDecimal from = minimum.add(width.multiply(BigDecimal.valueOf(i)));

			BigDecimal to = i == SALARY_BAND_COUNT - 1 ? maximum
					: minimum.add(width.multiply(BigDecimal.valueOf(i + 1)));

			String label = from.setScale(2, RoundingMode.HALF_UP) + " - " + to.setScale(2, RoundingMode.HALF_UP);

			bands.add(new SalaryDistributionResponse.SalaryBand(label, from.setScale(2, RoundingMode.HALF_UP),
					to.setScale(2, RoundingMode.HALF_UP), counts[i]));
		}

		return new SalaryDistributionResponse(currency, minimum, maximum, bands);
	}

	private LocalDate normalizeDate(LocalDate date) {
		return date == null ? LocalDate.now() : date;
	}
}