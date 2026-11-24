package com.hipro.school.backend.services.transport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.transport.DriverRequest;
import com.hipro.school.backend.dtos.transport.DriverResponse;
import com.hipro.school.backend.dtos.transport.TransportStaffRequest;
import com.hipro.school.backend.dtos.transport.TransportStaffResponse;
import com.hipro.school.backend.entities.Role;
import com.hipro.school.backend.entities.TransportStaff;
import com.hipro.school.backend.entities.TransportVehicle;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.enums.TransportDesignation;
import com.hipro.school.backend.enums.TransportEmploymentStatus;
import com.hipro.school.backend.enums.UserStatus;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.TransportStaffRepository;
import com.hipro.school.backend.repositories.TransportVehicleRepository;
import com.hipro.school.backend.repositories.UserRepository;
import com.hipro.school.backend.util.Utils;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransportStaffService {

	private final TransportStaffRepository staffRepository;
	private final TransportVehicleRepository vehicleRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public TransportStaffResponse create(TransportStaffRequest request) {
		log.info("Creating transport staff {}", request.getName());
		validateLicenceForDrivers(request.getDesignation(), request.getLicenceExpiry());
		TransportStaff staff = new TransportStaff();
		mapToEntity(request, staff);
		return mapToResponse(staffRepository.save(staff));
	}

	/**
	 * Creates a driver in both tables in one call: a {@code User} login with the
	 * DRIVER role and the linked {@code TransportStaff} profile. Common details
	 * (name, phone, address) are entered once and shared by both rows.
	 */
	public DriverResponse createDriver(DriverRequest request) {
		log.info("Creating driver {} with login {}", request.getName(), request.getEmail());
		if (!TransportRules.isDriverDesignation(request.getDesignation())) {
			throw new ServiceException("Driver login can only be created for driver designations "
					+ "(BUS_DRIVER, TRAVELLER_DRIVER, VAN_DRIVER), not " + request.getDesignation());
		}
		validateLicenceForDrivers(request.getDesignation(), request.getLicenceExpiry());
		String email = request.getEmail().toLowerCase();
		userRepository.findByPhoneOrEmail(email, request.getMobileNumber()).ifPresent(existing -> {
			if (existing.getEmail().equals(email)) {
				throw new ServiceException("Email already registered: " + email);
			}
		});

		String rawPassword = Utils.generateRandomString(8);
		User user = new User();
		user.setFullName(request.getName());
		user.setEmail(email);
		user.setPhone(request.getMobileNumber());
		user.setPassword(passwordEncoder.encode(rawPassword));
		user.setRole(Role.DRIVER);
		user.setStatus(UserStatus.ACTIVE);
		user = userRepository.save(user);

		TransportStaff staff = new TransportStaff();
		mapToEntity(request, staff);
		staff.setUser(user);
		staff = staffRepository.save(staff);

		log.info("Driver created successfully: staffId={}, userId={}", staff.getId(), user.getId());
		return DriverResponse.builder().staffId(staff.getId()).name(staff.getName())
				.mobileNumber(staff.getMobileNumber()).userId(user.getId()).loginEmail(email)
				.temporaryPassword(rawPassword).designation(staff.getDesignation())
				.employmentStatus(staff.getEmploymentStatus())
				.assignedVehicleId(staff.getAssignedVehicle() != null ? staff.getAssignedVehicle().getId() : null)
				.active(staff.isActive()).build();
	}

	@Transactional(readOnly = true)
	public PageResponse<TransportStaffResponse> getAll(TransportDesignation designation,
			TransportEmploymentStatus employmentStatus, String search, Pageable pageable) {
		Page<TransportStaff> page = staffRepository.findAll(byFilters(designation, employmentStatus, search),
				pageable);
		return PageResponse.from(page.map(this::mapToResponse));
	}

	@Transactional(readOnly = true)
	public TransportStaffResponse getById(Long id) {
		return mapToResponse(findStaff(id));
	}

	public TransportStaffResponse update(Long id, TransportStaffRequest request) {
		log.info("Updating transport staff id {}", id);
		TransportStaff staff = findStaff(id);
		validateLicenceForDrivers(request.getDesignation(), request.getLicenceExpiry());
		mapToEntity(request, staff);
		return mapToResponse(staffRepository.save(staff));
	}

	/**
	 * Assigns a vehicle to a staff member. Drivers holding an expired driving
	 * licence cannot be assigned to any vehicle.
	 */
	public TransportStaffResponse assignVehicle(Long staffId, Long vehicleId) {
		log.info("Assigning vehicle {} to transport staff {}", vehicleId, staffId);
		TransportStaff staff = findStaff(staffId);
		TransportVehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(
				() -> new ResourceNotFoundException("Transport vehicle not found: " + vehicleId));
		if (!vehicle.isActive()) {
			throw new ServiceException("Vehicle " + vehicle.getRegistrationNumber() + " is deactivated");
		}
		if (TransportRules.isDriverDesignation(staff.getDesignation())
				&& TransportRules.isLicenceExpired(staff.getLicenceExpiry(), LocalDate.now())) {
			throw new ServiceException("Cannot assign vehicle: driving licence of " + staff.getName()
					+ " expired on " + staff.getLicenceExpiry());
		}
		staff.setAssignedVehicle(vehicle);
		return mapToResponse(staffRepository.save(staff));
	}

	public void unassignVehicle(Long staffId) {
		log.info("Unassigning vehicle from transport staff {}", staffId);
		TransportStaff staff = findStaff(staffId);
		staff.setAssignedVehicle(null);
		staffRepository.save(staff);
	}

	public void deactivate(Long id) {
		log.info("Deactivating transport staff id {}", id);
		TransportStaff staff = findStaff(id);
		staff.setActive(false);
		staff.setEmploymentStatus(TransportEmploymentStatus.RELIEVED);
		if (staff.getUser() != null) {
			staff.getUser().setStatus(UserStatus.INACTIVE);
		}
		staffRepository.save(staff);
	}

	private void validateLicenceForDrivers(TransportDesignation designation, LocalDate licenceExpiry) {
		if (TransportRules.isDriverDesignation(designation)
				&& TransportRules.isLicenceExpired(licenceExpiry, LocalDate.now())) {
			throw new ServiceException("Cannot save driver with expired driving licence (expired on "
					+ licenceExpiry + "). Renew the licence first.");
		}
	}

	public TransportStaff findStaff(Long id) {
		return staffRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transport staff not found: " + id));
	}

	private void mapToEntity(TransportStaffRequest request, TransportStaff staff) {
		staff.setName(request.getName());
		staff.setMobileNumber(request.getMobileNumber());
		staff.setAddress(request.getAddress());
		staff.setJoiningDate(request.getJoiningDate());
		staff.setDesignation(request.getDesignation());
		staff.setLicenceNumber(request.getLicenceNumber());
		staff.setLicenceExpiry(request.getLicenceExpiry());
		staff.setMonthlySalary(request.getMonthlySalary());
		if (request.getEmploymentStatus() != null) {
			staff.setEmploymentStatus(request.getEmploymentStatus());
		}
		if (request.getAssignedVehicleId() != null) {
			TransportVehicle vehicle = vehicleRepository.findById(request.getAssignedVehicleId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Transport vehicle not found: " + request.getAssignedVehicleId()));
			if (TransportRules.isDriverDesignation(request.getDesignation())
					&& TransportRules.isLicenceExpired(request.getLicenceExpiry(), LocalDate.now())) {
				throw new ServiceException("Cannot assign vehicle: driving licence expired on "
						+ request.getLicenceExpiry());
			}
			staff.setAssignedVehicle(vehicle);
		}
	}

	private TransportStaffResponse mapToResponse(TransportStaff staff) {
		return TransportStaffResponse.builder().id(staff.getId())
				.name(staff.getName()).mobileNumber(staff.getMobileNumber()).address(staff.getAddress())
				.joiningDate(staff.getJoiningDate()).designation(staff.getDesignation())
				.licenceNumber(staff.getLicenceNumber()).licenceExpiry(staff.getLicenceExpiry())
				.licenceExpired(TransportRules.isLicenceExpired(staff.getLicenceExpiry(), LocalDate.now()))
				.monthlySalary(staff.getMonthlySalary()).employmentStatus(staff.getEmploymentStatus())
				.assignedVehicleId(staff.getAssignedVehicle() != null ? staff.getAssignedVehicle().getId() : null)
				.assignedVehicleNumber(
						staff.getAssignedVehicle() != null ? staff.getAssignedVehicle().getRegistrationNumber() : null)
				.userId(staff.getUser() != null ? staff.getUser().getId() : null)
				.loginEmail(staff.getUser() != null ? staff.getUser().getEmail() : null)
				.active(staff.isActive()).build();
	}

	private static Specification<TransportStaff> byFilters(TransportDesignation designation,
			TransportEmploymentStatus employmentStatus, String search) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (designation != null) {
				predicates.add(cb.equal(root.get("designation"), designation));
			}
			if (employmentStatus != null) {
				predicates.add(cb.equal(root.get("employmentStatus"), employmentStatus));
			}
			if (search != null && !search.isBlank()) {
				String like = "%" + search.trim().toLowerCase() + "%";
				predicates.add(cb.or(cb.like(cb.lower(root.get("name")), like),
						cb.like(cb.lower(root.get("mobileNumber")), like)));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
