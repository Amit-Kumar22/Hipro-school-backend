package com.hipro.school.backend.services.transport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.transport.StudentTransportAssignRequest;
import com.hipro.school.backend.dtos.transport.StudentTransportResponse;
import com.hipro.school.backend.dtos.transport.TransportRouteRequest;
import com.hipro.school.backend.dtos.transport.TransportRouteResponse;
import com.hipro.school.backend.dtos.transport.TransportRouteStopRequest;
import com.hipro.school.backend.dtos.transport.TransportRouteStopResponse;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.entities.StudentTransportAssignment;
import com.hipro.school.backend.entities.TransportRoute;
import com.hipro.school.backend.entities.TransportRouteStop;
import com.hipro.school.backend.entities.TransportStaff;
import com.hipro.school.backend.entities.TransportVehicle;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.entities.Role;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.StudentRepository;
import com.hipro.school.backend.repositories.StudentTransportAssignmentRepository;
import com.hipro.school.backend.repositories.TransportRouteRepository;
import com.hipro.school.backend.repositories.TransportRouteStopRepository;
import com.hipro.school.backend.repositories.TransportStaffRepository;
import com.hipro.school.backend.repositories.TransportVehicleRepository;
import com.hipro.school.backend.repositories.UserRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransportRouteService {

	private final TransportRouteRepository routeRepository;
	private final TransportRouteStopRepository stopRepository;
	private final StudentTransportAssignmentRepository assignmentRepository;
	private final TransportVehicleRepository vehicleRepository;
	private final TransportStaffRepository staffRepository;
	private final StudentRepository studentRepository;
	private final UserRepository userRepository;

	public TransportRouteResponse create(TransportRouteRequest request) {
		log.info("Creating transport route {}", request.getRouteName());
		if (routeRepository.existsByRouteNameIgnoreCase(request.getRouteName().trim())) {
			throw new ServiceException("Route '" + request.getRouteName() + "' already exists");
		}
		TransportRoute route = new TransportRoute();
		applyRouteFields(request, route, null);
		TransportRoute saved = routeRepository.save(route);
		saveStops(saved, request.getStops());
		return mapToResponse(saved);
	}

	@Transactional(readOnly = true)
	public PageResponse<TransportRouteResponse> getAll(Boolean active, Pageable pageable) {
		Page<TransportRoute> page = routeRepository.findAll(byFilters(active), pageable);
		return PageResponse.from(page.map(this::mapToResponse));
	}

	@Transactional(readOnly = true)
	public TransportRouteResponse getById(Long id) {
		return mapToResponse(findRoute(id));
	}

	public TransportRouteResponse update(Long id, TransportRouteRequest request) {
		log.info("Updating transport route id {}", id);
		TransportRoute route = findRoute(id);
		if (routeRepository.existsByRouteNameIgnoreCaseAndIdNot(request.getRouteName().trim(), id)) {
			throw new ServiceException("Route '" + request.getRouteName() + "' already exists");
		}
		applyRouteFields(request, route, id);
		TransportRoute saved = routeRepository.save(route);
		stopRepository.deleteByRoute_Id(saved.getId());
		saveStops(saved, request.getStops());
		return mapToResponse(saved);
	}

	/**
	 * Assigns a vehicle and driver to a route. A vehicle or driver already serving
	 * another active route is rejected to prevent conflicting assignments, and
	 * drivers with expired licences are rejected.
	 */
	public TransportRouteResponse assignVehicleAndDriver(Long routeId, Long vehicleId, Long driverId) {
		log.info("Assigning vehicle {} and driver {} to route {}", vehicleId, driverId, routeId);
		TransportRoute route = findRoute(routeId);
		if (vehicleId != null) {
			TransportVehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(
					() -> new ResourceNotFoundException("Transport vehicle not found: " + vehicleId));
			if (!vehicle.isActive()) {
				throw new ServiceException("Vehicle " + vehicle.getRegistrationNumber() + " is deactivated");
			}
			if (routeRepository.existsByVehicle_IdAndIsActiveAndIdNot(vehicleId, true, routeId)) {
				throw new ServiceException("Vehicle " + vehicle.getRegistrationNumber()
						+ " is already assigned to another active route");
			}
			route.setVehicle(vehicle);
		}
		if (driverId != null) {
			TransportStaff driver = staffRepository.findById(driverId).orElseThrow(
					() -> new ResourceNotFoundException("Transport staff not found: " + driverId));
			if (TransportRules.isDriverDesignation(driver.getDesignation())
					&& TransportRules.isLicenceExpired(driver.getLicenceExpiry(), LocalDate.now())) {
				throw new ServiceException("Cannot assign driver " + driver.getName()
						+ ": driving licence expired on " + driver.getLicenceExpiry());
			}
			if (routeRepository.existsByDriver_IdAndIsActiveAndIdNot(driverId, true, routeId)) {
				throw new ServiceException("Driver " + driver.getName()
						+ " is already assigned to another active route");
			}
			route.setDriver(driver);
		}
		return mapToResponse(routeRepository.save(route));
	}

	public void deactivate(Long id) {
		log.info("Deactivating transport route id {}", id);
		TransportRoute route = findRoute(id);
		route.setActive(false);
		routeRepository.save(route);
	}

	// ---------------- Student assignment ----------------

	/**
	 * Assigns an existing student to a route and stop. Enforces one active
	 * assignment per student and the vehicle's seating capacity.
	 */
	public StudentTransportResponse assignStudent(StudentTransportAssignRequest request) {
		log.info("Assigning student {} to route {}", request.getStudentId(), request.getRouteId());
		TransportRoute route = findRoute(request.getRouteId());
		if (!route.isActive()) {
			throw new ServiceException("Route '" + route.getRouteName() + "' is deactivated");
		}
		Student student = studentRepository.findById(request.getStudentId()).orElseThrow(
				() -> new ResourceNotFoundException("Student not found: " + request.getStudentId()));
		if (assignmentRepository.existsByStudent_IdAndIsActive(student.getId(), true)) {
			throw new ServiceException(
					"Student " + student.getAdmissionNumber() + " already has an active transport assignment");
		}
		TransportRouteStop stop = null;
		if (request.getStopId() != null) {
			stop = stopRepository.findByIdAndRoute_Id(request.getStopId(), route.getId()).orElseThrow(
					() -> new ResourceNotFoundException("Stop not found on this route: " + request.getStopId()));
		}
		validateCapacity(route);
		StudentTransportAssignment assignment = new StudentTransportAssignment();
		assignment.setStudent(student);
		assignment.setRoute(route);
		assignment.setStop(stop);
		assignment.setEffectiveFrom(request.getEffectiveFrom() != null ? request.getEffectiveFrom() : LocalDate.now());
		assignment.setBoardingPoint(request.getBoardingPoint());
		return mapAssignmentToResponse(assignmentRepository.save(assignment));
	}

	public StudentTransportResponse updateStudentAssignment(Long assignmentId,
			StudentTransportAssignRequest request) {
		log.info("Updating student transport assignment {}", assignmentId);
		StudentTransportAssignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(
				() -> new ResourceNotFoundException("Student transport assignment not found: " + assignmentId));
		TransportRoute route = findRoute(request.getRouteId());
		if (!assignment.getRoute().getId().equals(route.getId())) {
			validateCapacity(route);
		}
		TransportRouteStop stop = null;
		if (request.getStopId() != null) {
			stop = stopRepository.findByIdAndRoute_Id(request.getStopId(), route.getId()).orElseThrow(
					() -> new ResourceNotFoundException("Stop not found on this route: " + request.getStopId()));
		}
		assignment.setRoute(route);
		assignment.setStop(stop);
		if (request.getEffectiveFrom() != null) {
			assignment.setEffectiveFrom(request.getEffectiveFrom());
		}
		assignment.setBoardingPoint(request.getBoardingPoint());
		return mapAssignmentToResponse(assignmentRepository.save(assignment));
	}

	@Transactional(readOnly = true)
	public List<StudentTransportResponse> getRouteStudents(Long routeId) {
		findRoute(routeId);
		return assignmentRepository.findByRoute_IdAndIsActive(routeId, true).stream()
				.map(this::mapAssignmentToResponse).toList();
	}

	@Transactional(readOnly = true)
	public StudentTransportResponse getStudentAssignment(Long studentId) {
		StudentTransportAssignment assignment = assignmentRepository.findByStudent_IdAndIsActive(studentId, true)
				.orElseThrow(() -> new ResourceNotFoundException(
						"No active transport assignment for student: " + studentId));
		return mapAssignmentToResponse(assignment);
	}

	public void unassignStudent(Long assignmentId) {
		log.info("Removing student transport assignment {}", assignmentId);
		StudentTransportAssignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(
				() -> new ResourceNotFoundException("Student transport assignment not found: " + assignmentId));
		assignment.setActive(false);
		assignmentRepository.save(assignment);
	}

	// ---------------- internals ----------------

	private void validateCapacity(TransportRoute route) {
		if (route.getVehicle() == null) {
			throw new ServiceException("Route '" + route.getRouteName()
					+ "' has no vehicle assigned; assign a vehicle before adding students");
		}
		long occupied = assignmentRepository.countByRoute_IdAndIsActive(route.getId(), true);
		if (!TransportRules.hasSeatingCapacity(route.getVehicle().getSeatingCapacity(), occupied)) {
			throw new ServiceException("Vehicle " + route.getVehicle().getRegistrationNumber()
					+ " has no free seats (capacity " + route.getVehicle().getSeatingCapacity() + ")");
		}
	}

	private void applyRouteFields(TransportRouteRequest request, TransportRoute route, Long excludeRouteId) {
		route.setRouteName(request.getRouteName().trim());
		route.setTransportFee(request.getTransportFee());
		route.setPickupTime(request.getPickupTime());
		route.setDropTime(request.getDropTime());
		route.setDescription(request.getDescription());
		if (request.getVehicleId() != null) {
			TransportVehicle vehicle = vehicleRepository.findById(request.getVehicleId()).orElseThrow(
					() -> new ResourceNotFoundException("Transport vehicle not found: " + request.getVehicleId()));
			if (!vehicle.isActive()) {
				throw new ServiceException("Vehicle " + vehicle.getRegistrationNumber() + " is deactivated");
			}
			boolean conflict = excludeRouteId == null
					? routeRepository.existsByVehicle_IdAndIsActive(vehicle.getId(), true)
					: routeRepository.existsByVehicle_IdAndIsActiveAndIdNot(vehicle.getId(), true, excludeRouteId);
			if (conflict) {
				throw new ServiceException("Vehicle " + vehicle.getRegistrationNumber()
						+ " is already assigned to another active route");
			}
			route.setVehicle(vehicle);
		} else {
			route.setVehicle(null);
		}
		if (request.getDriverId() != null) {
			TransportStaff driver = staffRepository.findById(request.getDriverId()).orElseThrow(
					() -> new ResourceNotFoundException("Transport staff not found: " + request.getDriverId()));
			if (TransportRules.isDriverDesignation(driver.getDesignation())
					&& TransportRules.isLicenceExpired(driver.getLicenceExpiry(), LocalDate.now())) {
				throw new ServiceException("Cannot assign driver " + driver.getName()
						+ ": driving licence expired on " + driver.getLicenceExpiry());
			}
			boolean conflict = excludeRouteId == null
					? routeRepository.existsByDriver_IdAndIsActive(driver.getId(), true)
					: routeRepository.existsByDriver_IdAndIsActiveAndIdNot(driver.getId(), true, excludeRouteId);
			if (conflict) {
				throw new ServiceException(
						"Driver " + driver.getName() + " is already assigned to another active route");
			}
			route.setDriver(driver);
		} else {
			route.setDriver(null);
		}
	}

	private void saveStops(TransportRoute route, List<TransportRouteStopRequest> stops) {
		if (stops == null || stops.isEmpty()) {
			return;
		}
		List<TransportRouteStopRequest> ordered = stops.stream()
				.sorted(Comparator.comparingInt(s -> s.getStopOrder() == null ? 0 : s.getStopOrder())).toList();
		for (TransportRouteStopRequest stopRequest : ordered) {
			TransportRouteStop stop = new TransportRouteStop();
			stop.setRoute(route);
			stop.setStopName(stopRequest.getStopName().trim());
			stop.setStopOrder(stopRequest.getStopOrder() == null ? 0 : stopRequest.getStopOrder());
			stop.setPickupTime(stopRequest.getPickupTime());
			stopRepository.save(stop);
		}
	}

	public TransportRoute findRoute(Long id) {
		return routeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transport route not found: " + id));
	}

	/**
	 * Returns the active routes assigned to the currently logged-in driver. The
	 * driver is resolved through the User -&gt; TransportStaff link.
	 */
	@Transactional(readOnly = true)
	public List<TransportRouteResponse> getMyRoutes(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
		if (user.getRole() != Role.DRIVER) {
			throw new ServiceException("Only drivers can access assigned routes");
		}
		TransportStaff staff = staffRepository.findByUser_Id(user.getId()).orElseThrow(
				() -> new ResourceNotFoundException("No driver profile is linked to this login: " + email));
		return routeRepository.findByDriver_IdAndIsActive(staff.getId(), true).stream().map(this::mapToResponse)
				.toList();
	}

	private TransportRouteResponse mapToResponse(TransportRoute route) {
		List<TransportRouteStopResponse> stops = stopRepository.findByRoute_IdOrderByStopOrderAsc(route.getId())
				.stream().map(s -> TransportRouteStopResponse.builder().id(s.getId()).stopName(s.getStopName())
						.stopOrder(s.getStopOrder()).pickupTime(s.getPickupTime()).build())
				.toList();
		return TransportRouteResponse.builder().id(route.getId())
				.routeName(route.getRouteName())
				.transportFee(route.getTransportFee()).pickupTime(route.getPickupTime()).dropTime(route.getDropTime())
				.vehicleId(route.getVehicle() != null ? route.getVehicle().getId() : null)
				.vehicleNumber(route.getVehicle() != null ? route.getVehicle().getRegistrationNumber() : null)
				.seatingCapacity(route.getVehicle() != null ? route.getVehicle().getSeatingCapacity() : null)
				.driverId(route.getDriver() != null ? route.getDriver().getId() : null)
				.driverName(route.getDriver() != null ? route.getDriver().getName() : null)
				.description(route.getDescription())
				.assignedStudentCount(assignmentRepository.countByRoute_IdAndIsActive(route.getId(), true))
				.active(route.isActive()).stops(stops).build();
	}

	private StudentTransportResponse mapAssignmentToResponse(StudentTransportAssignment assignment) {
		return StudentTransportResponse.builder().id(assignment.getId()).studentId(assignment.getStudent().getId())
				.studentName(assignment.getStudent().getStudentUser() != null
						? assignment.getStudent().getStudentUser().getFullName()
						: null)
				.admissionNumber(assignment.getStudent().getAdmissionNumber()).routeId(assignment.getRoute().getId())
				.routeName(assignment.getRoute().getRouteName())
				.stopId(assignment.getStop() != null ? assignment.getStop().getId() : null)
				.stopName(assignment.getStop() != null ? assignment.getStop().getStopName() : null)
				.effectiveFrom(assignment.getEffectiveFrom()).boardingPoint(assignment.getBoardingPoint())
				.active(assignment.isActive()).build();
	}

	private static Specification<TransportRoute> byFilters(Boolean active) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (active != null) {
				predicates.add(cb.equal(root.get("isActive"), active));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
