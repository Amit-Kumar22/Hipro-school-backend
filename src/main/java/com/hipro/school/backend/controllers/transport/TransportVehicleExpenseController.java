package com.hipro.school.backend.controllers.transport;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.transport.TransportVehicleExpenseRequest;
import com.hipro.school.backend.dtos.transport.TransportVehicleExpenseResponse;
import com.hipro.school.backend.enums.VehicleExpenseType;
import com.hipro.school.backend.services.transport.TransportVehicleExpenseService;
import com.hipro.school.backend.services.transport.TransportVehicleExpenseService.ExpenseTypeSummary;
import com.hipro.school.backend.services.transport.TransportVehicleExpenseService.VehicleExpenseSummary;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Transport Expense APIs", description = "APIs for fuel, servicing, repair and other vehicle expenses with reports")
@RestController
@RequestMapping("/v1/transport/expenses")
@RequiredArgsConstructor
@Slf4j
public class TransportVehicleExpenseController {

	private final TransportVehicleExpenseService expenseService;

	@Operation(summary = "List expenses", description = "Paginated vehicle expenses with optional vehicle, type and date-range filters.")
	@GetMapping
	public ResponseEntity<PageResponse<TransportVehicleExpenseResponse>> getAll(
			@RequestParam(required = false) Long vehicleId,
			@RequestParam(required = false) VehicleExpenseType expenseType,
			@RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to,
			Pageable pageable) {
		return ResponseEntity.ok(expenseService.getAll(vehicleId, expenseType, from, to, pageable));
	}

	@Operation(summary = "Record an expense", description = "Records fuel, servicing, repair, insurance or other vehicle expenses.")
	@PostMapping
	public ResponseEntity<TransportVehicleExpenseResponse> create(
			@Valid @RequestBody TransportVehicleExpenseRequest request) {
		log.info("Recording vehicle expense for vehicle {}", request.getVehicleId());
		return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.create(request));
	}

	@Operation(summary = "Update an expense", description = "Updates a recorded vehicle expense.")
	@PutMapping("/{id}")
	public ResponseEntity<TransportVehicleExpenseResponse> update(@PathVariable Long id,
			@Valid @RequestBody TransportVehicleExpenseRequest request) {
		return ResponseEntity.ok(expenseService.update(id, request));
	}

	@Operation(summary = "Delete an expense", description = "Deletes a recorded vehicle expense.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		expenseService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Vehicle-wise expense report", description = "Totals grouped by vehicle for a date range, highest spend first.")
	@GetMapping("/reports/by-vehicle")
	public ResponseEntity<List<VehicleExpenseSummary>> getVehicleWiseReport(@RequestParam LocalDate from,
			@RequestParam LocalDate to) {
		return ResponseEntity.ok(expenseService.getVehicleWiseReport(from, to));
	}

	@Operation(summary = "Month/type-wise expense report", description = "Totals grouped by expense type (fuel, servicing, repair, insurance, other) for a date range.")
	@GetMapping("/reports/by-type")
	public ResponseEntity<List<ExpenseTypeSummary>> getTypeWiseReport(@RequestParam LocalDate from,
			@RequestParam LocalDate to) {
		return ResponseEntity.ok(expenseService.getTypeWiseReport(from, to));
	}
}
