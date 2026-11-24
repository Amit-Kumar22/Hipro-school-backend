package com.hipro.school.backend.services.transport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.transport.DocumentExpiryAlert;
import com.hipro.school.backend.dtos.transport.TransportDashboardResponse;
import com.hipro.school.backend.enums.SalaryStatus;
import com.hipro.school.backend.enums.TransportEmploymentStatus;
import com.hipro.school.backend.repositories.StaffSalaryRepository;
import com.hipro.school.backend.repositories.StudentTransportAssignmentRepository;
import com.hipro.school.backend.repositories.TransportRouteRepository;
import com.hipro.school.backend.repositories.TransportStaffRepository;
import com.hipro.school.backend.repositories.TransportVehicleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TransportDashboardService {

	private final TransportVehicleRepository vehicleRepository;
	private final TransportStaffRepository staffRepository;
	private final TransportRouteRepository routeRepository;
	private final StudentTransportAssignmentRepository assignmentRepository;
	private final StaffSalaryRepository staffSalaryRepository;
	private final TransportVehicleExpenseService expenseService;
	private final TransportVehicleService vehicleService;
	private final TransportFeeService feeService;

	public TransportDashboardResponse getDashboard(Integer expiryWithinDays) {
		log.info("Building transport dashboard");
		int days = expiryWithinDays == null ? 30 : expiryWithinDays;
		LocalDate today = LocalDate.now();
		LocalDate monthStart = today.withDayOfMonth(1);
		LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

		List<DocumentExpiryAlert> expiries = vehicleService.getUpcomingExpiries(days);
		long expiredCount = expiries.stream().filter(DocumentExpiryAlert::isExpired).count();

		BigDecimal salaryExpense = staffSalaryRepository.sumNetSalaryForTransportStaff(monthStart, monthEnd,
				List.of(SalaryStatus.APPROVED, SalaryStatus.PAID, SalaryStatus.PENDING));
		BigDecimal vehicleExpense = expenseService.getMonthlyTotal(monthStart);

		return TransportDashboardResponse.builder()
				.totalVehicles(vehicleRepository.count())
				.activeVehicles(vehicleRepository.countByIsActive(true))
				.activeDrivers(staffRepository.countByEmploymentStatus(TransportEmploymentStatus.ACTIVE))
				.activeRoutes(routeRepository.countByIsActive(true))
				.transportStudents(assignmentRepository.countByIsActive(true))
				.monthlySalaryExpense(salaryExpense != null ? salaryExpense : BigDecimal.ZERO)
				.monthlyVehicleExpense(vehicleExpense != null ? vehicleExpense : BigDecimal.ZERO)
				.outstandingTransportFees(feeService.getTotalOutstanding())
				.expiredDocumentCount(expiredCount).upcomingExpiries(expiries).build();
	}
}
