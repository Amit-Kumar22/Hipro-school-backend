package com.hipro.school.backend.controllers.transport;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.transport.DocumentExpiryAlert;
import com.hipro.school.backend.dtos.transport.TransportDocumentRuleRequest;
import com.hipro.school.backend.dtos.transport.TransportDocumentRuleResponse;
import com.hipro.school.backend.dtos.transport.TransportVehicleRequest;
import com.hipro.school.backend.dtos.transport.TransportVehicleResponse;
import com.hipro.school.backend.enums.VehicleType;
import com.hipro.school.backend.services.transport.TransportVehicleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Transport Vehicle APIs", description = "APIs for managing school transport vehicles and document expiries")
@RestController
@RequestMapping("/v1/transport/vehicles")
@RequiredArgsConstructor
@Slf4j
public class TransportVehicleController {

	private final TransportVehicleService vehicleService;

	@Operation(summary = "List vehicles", description = "Paginated vehicle list with optional type, status and registration/model search filters.")
	@GetMapping
	public ResponseEntity<PageResponse<TransportVehicleResponse>> getAll(
			@RequestParam(required = false) VehicleType vehicleType,
			@RequestParam(required = false) Boolean active,
			@RequestParam(required = false) String search, Pageable pageable) {
		return ResponseEntity.ok(vehicleService.getAll(vehicleType, active, search, pageable));
	}

	@Operation(summary = "Get a vehicle by id", description = "Fetches a single vehicle by id.")
	@GetMapping("/{id}")
	public ResponseEntity<TransportVehicleResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(vehicleService.getById(id));
	}

	@Operation(summary = "Add a vehicle", description = "Registers a new vehicle with registration number, type, capacity and document expiries.")
	@PostMapping
	public ResponseEntity<TransportVehicleResponse> create(@Valid @RequestBody TransportVehicleRequest request) {
		log.info("Creating transport vehicle {}", request.getRegistrationNumber());
		return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(request));
	}

	@Operation(summary = "Update a vehicle", description = "Updates registration, type, capacity and document expiries of an existing vehicle.")
	@PutMapping("/{id}")
	public ResponseEntity<TransportVehicleResponse> update(@PathVariable Long id,
			@Valid @RequestBody TransportVehicleRequest request) {
		return ResponseEntity.ok(vehicleService.update(id, request));
	}

	@Operation(summary = "Deactivate a vehicle", description = "Soft-deactivates a vehicle to preserve transport history.")
	@PutMapping("/{id}/deactivate")
	public ResponseEntity<String> deactivate(@PathVariable Long id) {
		vehicleService.deactivate(id);
		return ResponseEntity.ok("Vehicle deactivated successfully");
	}

	@Operation(summary = "Reactivate a vehicle", description = "Reactivates a previously deactivated vehicle.")
	@PutMapping("/{id}/reactivate")
	public ResponseEntity<String> reactivate(@PathVariable Long id) {
		vehicleService.reactivate(id);
		return ResponseEntity.ok("Vehicle reactivated successfully");
	}

	@Operation(summary = "Upcoming document expiries", description = "Lists insurance, fitness, permit and PUC expiries due within the given days (default 30), honouring per-vehicle-type document rules.")
	@GetMapping("/document-expiries")
	public ResponseEntity<List<DocumentExpiryAlert>> getUpcomingExpiries(
			@RequestParam(required = false, defaultValue = "30") Integer withinDays) {
		return ResponseEntity.ok(vehicleService.getUpcomingExpiries(withinDays));
	}

	@Operation(summary = "Get document rules", description = "Returns the configurable per-vehicle-type document requirements (insurance, fitness, permit, PUC) and alert windows.")
	@GetMapping("/document-rules")
	public ResponseEntity<List<TransportDocumentRuleResponse>> getDocumentRules() {
		return ResponseEntity.ok(vehicleService.getDocumentRules());
	}

	@Operation(summary = "Configure document rule", description = "Creates or updates the document requirements for a vehicle type.")
	@PutMapping("/document-rules")
	public ResponseEntity<TransportDocumentRuleResponse> upsertDocumentRule(
			@Valid @RequestBody TransportDocumentRuleRequest request) {
		return ResponseEntity.ok(vehicleService.upsertDocumentRule(request));
	}
}
