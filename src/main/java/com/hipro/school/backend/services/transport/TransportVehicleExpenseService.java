package com.hipro.school.backend.services.transport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.transport.TransportVehicleExpenseRequest;
import com.hipro.school.backend.dtos.transport.TransportVehicleExpenseResponse;
import com.hipro.school.backend.entities.TransportVehicle;
import com.hipro.school.backend.entities.TransportVehicleExpense;
import com.hipro.school.backend.enums.VehicleExpenseType;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.TransportVehicleExpenseRepository;
import com.hipro.school.backend.repositories.TransportVehicleRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransportVehicleExpenseService {

	private final TransportVehicleExpenseRepository expenseRepository;
	private final TransportVehicleRepository vehicleRepository;

	public TransportVehicleExpenseResponse create(TransportVehicleExpenseRequest request) {
		log.info("Recording {} expense for vehicle {}", request.getExpenseType(), request.getVehicleId());
		TransportVehicle vehicle = vehicleRepository.findById(request.getVehicleId()).orElseThrow(
				() -> new ResourceNotFoundException("Transport vehicle not found: " + request.getVehicleId()));
		TransportVehicleExpense expense = new TransportVehicleExpense();
		expense.setVehicle(vehicle);
		mapToEntity(request, expense);
		return mapToResponse(expenseRepository.save(expense));
	}

	@Transactional(readOnly = true)
	public PageResponse<TransportVehicleExpenseResponse> getAll(Long vehicleId, VehicleExpenseType expenseType,
			LocalDate from, LocalDate to, Pageable pageable) {
		Page<TransportVehicleExpense> page = expenseRepository.findAll(byFilters(vehicleId, expenseType, from, to),
				pageable);
		return PageResponse.from(page.map(this::mapToResponse));
	}

	public TransportVehicleExpenseResponse update(Long id, TransportVehicleExpenseRequest request) {
		log.info("Updating vehicle expense id {}", id);
		TransportVehicleExpense expense = findExpense(id);
		TransportVehicle vehicle = vehicleRepository.findById(request.getVehicleId()).orElseThrow(
				() -> new ResourceNotFoundException("Transport vehicle not found: " + request.getVehicleId()));
		expense.setVehicle(vehicle);
		mapToEntity(request, expense);
		return mapToResponse(expenseRepository.save(expense));
	}

	public void delete(Long id) {
		log.info("Deleting vehicle expense id {}", id);
		expenseRepository.delete(findExpense(id));
	}

	/** Month-wise total. Month is normalised to first/last day. */
	@Transactional(readOnly = true)
	public BigDecimal getMonthlyTotal(LocalDate month) {
		LocalDate first = month.withDayOfMonth(1);
		LocalDate last = first.plusMonths(1).minusDays(1);
		return expenseRepository.sumAmountForPeriod(first, last);
	}

	/** Vehicle-wise totals for a date range, highest spend first. */
	@Transactional(readOnly = true)
	public List<VehicleExpenseSummary> getVehicleWiseReport(LocalDate from, LocalDate to) {
		List<VehicleExpenseSummary> report = new ArrayList<>();
		for (Object[] row : expenseRepository.sumGroupedByVehicleForPeriod(from, to)) {
			report.add(new VehicleExpenseSummary((Long) row[0], (String) row[1], (BigDecimal) row[2],
					(Long) row[3]));
		}
		return report;
	}

	/** Expense-type-wise totals for a date range. */
	@Transactional(readOnly = true)
	public List<ExpenseTypeSummary> getTypeWiseReport(LocalDate from, LocalDate to) {
		List<ExpenseTypeSummary> report = new ArrayList<>();
		for (Object[] row : expenseRepository.sumGroupedByTypeForPeriod(from, to)) {
			report.add(new ExpenseTypeSummary((VehicleExpenseType) row[0], (BigDecimal) row[1], (Long) row[2]));
		}
		return report;
	}

	private TransportVehicleExpense findExpense(Long id) {
		return expenseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Vehicle expense not found: " + id));
	}

	private void mapToEntity(TransportVehicleExpenseRequest request, TransportVehicleExpense expense) {
		expense.setExpenseDate(request.getExpenseDate());
		expense.setExpenseType(request.getExpenseType());
		expense.setAmount(request.getAmount());
		expense.setFuelLitres(request.getFuelLitres());
		expense.setOdometerReading(request.getOdometerReading());
		expense.setVendor(request.getVendor());
		expense.setBillNumber(request.getBillNumber());
		expense.setPaymentMode(request.getPaymentMode());
		expense.setRemarks(request.getRemarks());
	}

	private TransportVehicleExpenseResponse mapToResponse(TransportVehicleExpense expense) {
		return TransportVehicleExpenseResponse.builder().id(expense.getId())
				.vehicleId(expense.getVehicle().getId())
				.registrationNumber(expense.getVehicle().getRegistrationNumber())
				.expenseDate(expense.getExpenseDate()).expenseType(expense.getExpenseType())
				.amount(expense.getAmount()).fuelLitres(expense.getFuelLitres())
				.odometerReading(expense.getOdometerReading()).vendor(expense.getVendor())
				.billNumber(expense.getBillNumber()).paymentMode(expense.getPaymentMode())
				.remarks(expense.getRemarks()).build();
	}

	private static Specification<TransportVehicleExpense> byFilters(Long vehicleId, VehicleExpenseType expenseType,
			LocalDate from, LocalDate to) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (vehicleId != null) {
				predicates.add(cb.equal(root.get("vehicle").get("id"), vehicleId));
			}
			if (expenseType != null) {
				predicates.add(cb.equal(root.get("expenseType"), expenseType));
			}
			if (from != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("expenseDate"), from));
			}
			if (to != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("expenseDate"), to));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public record VehicleExpenseSummary(Long vehicleId, String registrationNumber, BigDecimal totalAmount,
			Long expenseCount) {
	}

	public record ExpenseTypeSummary(VehicleExpenseType expenseType, BigDecimal totalAmount, Long expenseCount) {
	}
}
