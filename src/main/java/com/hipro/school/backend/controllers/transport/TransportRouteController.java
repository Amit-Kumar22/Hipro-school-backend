package com.hipro.school.backend.controllers.transport;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.hipro.school.backend.dtos.transport.StudentTransportAssignRequest;
import com.hipro.school.backend.dtos.transport.StudentTransportResponse;
import com.hipro.school.backend.dtos.transport.TransportRouteRequest;
import com.hipro.school.backend.dtos.transport.TransportRouteResponse;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.transport.TransportRouteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Transport Route APIs", description = "APIs for routes, stops and student route assignments")
@RestController
@RequestMapping("/v1/transport/routes")
@Slf4j
public class TransportRouteController {

	@Autowired
	private  TransportRouteService routeService;

	@Operation(summary = "List routes", description = "Paginated route list with stops, vehicle/driver and student counts.")
	@GetMapping
	public ResponseEntity<PageResponse<TransportRouteResponse>> getAll(
			@RequestParam(required = false) Boolean active, Pageable pageable) {
		return ResponseEntity.ok(routeService.getAll(active, pageable));
	}

	@Operation(summary = "Get a route by id", description = "Fetches a single route with its stops.")
	@GetMapping("/{id}")
	public ResponseEntity<TransportRouteResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(routeService.getById(id));
	}

	@Operation(summary = "My assigned routes", description = "Returns the active routes assigned to the currently logged-in driver (DRIVER role only).")
	//@PreAuthorize("hasRole('DRIVER')")
	@GetMapping("/my-routes")
	public ResponseEntity<List<TransportRouteResponse>> getMyRoutes() {
		String email = AuthDetailsProvider.getLoggedEmail();
		log.info("API: Fetch assigned routes for driver {}", email);
		return ResponseEntity.ok(routeService.getMyRoutes(email));
	}

	@Operation(summary = "Create a route", description = "Creates a route with stops, timings and transport fee. Vehicle/driver conflicts and expired driver licences are rejected.")
	@PostMapping
	public ResponseEntity<TransportRouteResponse> create(@Valid @RequestBody TransportRouteRequest request) {
		log.info("Creating transport route {}", request.getRouteName());
		return ResponseEntity.status(HttpStatus.CREATED).body(routeService.create(request));
	}

	@Operation(summary = "Update a route", description = "Updates a route and replaces its stop list.")
	@PutMapping("/{id}")
	public ResponseEntity<TransportRouteResponse> update(@PathVariable Long id,
			@Valid @RequestBody TransportRouteRequest request) {
		return ResponseEntity.ok(routeService.update(id, request));
	}

	@Operation(summary = "Assign vehicle and driver", description = "Assigns a vehicle and/or driver to a route. Conflicting assignments and expired licences are rejected.")
	@PutMapping("/{id}/assignment")
	public ResponseEntity<TransportRouteResponse> assignVehicleAndDriver(@PathVariable Long id,
			@RequestParam(required = false) Long vehicleId,
			@RequestParam(required = false) Long driverId) {
		return ResponseEntity.ok(routeService.assignVehicleAndDriver(id, vehicleId, driverId));
	}

	@Operation(summary = "Deactivate a route", description = "Soft-deactivates a route.")
	@PutMapping("/{id}/deactivate")
	public ResponseEntity<String> deactivate(@PathVariable Long id) {
		routeService.deactivate(id);
		return ResponseEntity.ok("Route deactivated successfully");
	}

	@Operation(summary = "Assign student to route", description = "Assigns an existing student to a route and stop. Enforces one active assignment per student and vehicle seating capacity.")
	@PostMapping("/assignments")
	public ResponseEntity<StudentTransportResponse> assignStudent(
			@Valid @RequestBody StudentTransportAssignRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(routeService.assignStudent(request));
	}

	@Operation(summary = "Update student assignment", description = "Moves a student assignment to another route/stop.")
	@PutMapping("/assignments/{assignmentId}")
	public ResponseEntity<StudentTransportResponse> updateStudentAssignment(@PathVariable Long assignmentId,
			@Valid @RequestBody StudentTransportAssignRequest request) {
		return ResponseEntity.ok(routeService.updateStudentAssignment(assignmentId, request));
	}

	@Operation(summary = "Route-wise student list", description = "Lists all students actively assigned to a route.")
	@GetMapping("/{id}/students")
	public ResponseEntity<List<StudentTransportResponse>> getRouteStudents(@PathVariable Long id) {
		return ResponseEntity.ok(routeService.getRouteStudents(id));
	}

	@Operation(summary = "Student-wise transport details", description = "Fetches the active transport assignment of a student.")
	@GetMapping("/assignments/student/{studentId}")
	public ResponseEntity<StudentTransportResponse> getStudentAssignment(@PathVariable Long studentId) {
		return ResponseEntity.ok(routeService.getStudentAssignment(studentId));
	}

	@Operation(summary = "Remove student assignment", description = "Ends a student's active transport assignment.")
	@DeleteMapping("/assignments/{assignmentId}")
	public ResponseEntity<Void> unassignStudent(@PathVariable Long assignmentId) {
		routeService.unassignStudent(assignmentId);
		return ResponseEntity.noContent().build();
	}
}
