package com.hipro.school.backend.services.transport;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.transport.DocumentExpiryAlert;
import com.hipro.school.backend.dtos.transport.TransportDocumentRuleRequest;
import com.hipro.school.backend.dtos.transport.TransportDocumentRuleResponse;
import com.hipro.school.backend.dtos.transport.TransportVehicleRequest;
import com.hipro.school.backend.dtos.transport.TransportVehicleResponse;
import com.hipro.school.backend.entities.TransportDocumentRule;
import com.hipro.school.backend.entities.TransportVehicle;
import com.hipro.school.backend.enums.TransportDocumentType;
import com.hipro.school.backend.enums.VehicleType;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.TransportDocumentRuleRepository;
import com.hipro.school.backend.repositories.TransportVehicleRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransportVehicleService {

	private final TransportVehicleRepository vehicleRepository;
	private final TransportDocumentRuleRepository documentRuleRepository;

	public TransportVehicleResponse create(TransportVehicleRequest request) {
		log.info("Creating transport vehicle {}", request.getRegistrationNumber());
		String regNo = request.getRegistrationNumber().trim().toUpperCase();
		if (vehicleRepository.existsByRegistrationNumberIgnoreCase(regNo)) {
			throw new ServiceException("Vehicle with registration number " + regNo + " already exists");
		}
		TransportVehicle vehicle = new TransportVehicle();
		mapToEntity(request, vehicle, regNo);
		return mapToResponse(vehicleRepository.save(vehicle));
	}

	@Transactional(readOnly = true)
	public PageResponse<TransportVehicleResponse> getAll(VehicleType vehicleType, Boolean active, String search,
			Pageable pageable) {
		Page<TransportVehicle> page = vehicleRepository.findAll(byFilters(vehicleType, active, search), pageable);
		return PageResponse.from(page.map(this::mapToResponse));
	}

	@Transactional(readOnly = true)
	public TransportVehicleResponse getById(Long id) {
		return mapToResponse(findVehicle(id));
	}

	public TransportVehicleResponse update(Long id, TransportVehicleRequest request) {
		log.info("Updating transport vehicle id {}", id);
		TransportVehicle vehicle = findVehicle(id);
		String regNo = request.getRegistrationNumber().trim().toUpperCase();
		if (vehicleRepository.existsByRegistrationNumberIgnoreCaseAndIdNot(regNo, id)) {
			throw new ServiceException("Vehicle with registration number " + regNo + " already exists");
		}
		mapToEntity(request, vehicle, regNo);
		return mapToResponse(vehicleRepository.save(vehicle));
	}

	/** Soft-deactivates a vehicle instead of hard-deleting it to preserve history. */
	public void deactivate(Long id) {
		log.info("Deactivating transport vehicle id {}", id);
		TransportVehicle vehicle = findVehicle(id);
		vehicle.setActive(false);
		vehicleRepository.save(vehicle);
	}

	public void reactivate(Long id) {
		log.info("Reactivating transport vehicle id {}", id);
		TransportVehicle vehicle = findVehicle(id);
		vehicle.setActive(true);
		vehicleRepository.save(vehicle);
	}

	@Transactional(readOnly = true)
	public List<DocumentExpiryAlert> getUpcomingExpiries(Integer withinDays) {
		int days = withinDays == null ? 30 : withinDays;
		LocalDate cutoff = LocalDate.now().plusDays(days);
		List<TransportVehicle> vehicles = vehicleRepository.findWithUpcomingDocumentExpiry(cutoff);
		List<DocumentExpiryAlert> alerts = new ArrayList<>();
		for (TransportVehicle vehicle : vehicles) {
			TransportDocumentRule rule = getEffectiveRule(vehicle.getVehicleType());
			addAlert(alerts, vehicle, TransportDocumentType.INSURANCE, vehicle.getInsuranceExpiry(),
					rule.isRequireInsurance(), rule.getAlertDaysBefore(), days);
			addAlert(alerts, vehicle, TransportDocumentType.FITNESS, vehicle.getFitnessExpiry(),
					rule.isRequireFitness(), rule.getAlertDaysBefore(), days);
			addAlert(alerts, vehicle, TransportDocumentType.PERMIT, vehicle.getPermitExpiry(),
					rule.isRequirePermit(), rule.getAlertDaysBefore(), days);
			addAlert(alerts, vehicle, TransportDocumentType.PUC, vehicle.getPucExpiry(), rule.isRequirePuc(),
					rule.getAlertDaysBefore(), days);
		}
		alerts.sort((a, b) -> a.getExpiryDate().compareTo(b.getExpiryDate()));
		return alerts;
	}

	@Transactional(readOnly = true)
	public List<TransportDocumentRuleResponse> getDocumentRules() {
		List<TransportDocumentRuleResponse> responses = new ArrayList<>();
		for (VehicleType type : VehicleType.values()) {
			responses.add(mapRuleToResponse(getEffectiveRule(type)));
		}
		return responses;
	}

	public TransportDocumentRuleResponse upsertDocumentRule(TransportDocumentRuleRequest request) {
		log.info("Upserting document rule for vehicle type {}", request.getVehicleType());
		TransportDocumentRule rule = documentRuleRepository.findByVehicleType(request.getVehicleType())
				.orElseGet(() -> {
					TransportDocumentRule created = new TransportDocumentRule();
					created.setVehicleType(request.getVehicleType());
					return created;
				});
		rule.setRequireInsurance(request.isRequireInsurance());
		rule.setRequireFitness(request.isRequireFitness());
		rule.setRequirePermit(request.isRequirePermit());
		rule.setRequirePuc(request.isRequirePuc());
		rule.setAlertDaysBefore(request.getAlertDaysBefore());
		return mapRuleToResponse(documentRuleRepository.save(rule));
	}

	public TransportDocumentRule getEffectiveRule(VehicleType vehicleType) {
		return documentRuleRepository.findByVehicleType(vehicleType).orElseGet(() -> {
			TransportDocumentRule defaults = new TransportDocumentRule();
			defaults.setVehicleType(vehicleType);
			defaults.setRequireInsurance(true);
			defaults.setRequireFitness(true);
			defaults.setRequirePermit(true);
			defaults.setRequirePuc(true);
			defaults.setAlertDaysBefore(30);
			return defaults;
		});
	}

	private void addAlert(List<DocumentExpiryAlert> alerts, TransportVehicle vehicle,
			TransportDocumentType documentType, LocalDate expiryDate, boolean required, int ruleAlertDays,
			int requestedDays) {
		if (!required || expiryDate == null) {
			return;
		}
		int effectiveDays = Math.min(ruleAlertDays, requestedDays);
		LocalDate today = LocalDate.now();
		if (!expiryDate.isAfter(today.plusDays(effectiveDays))) {
			long daysRemaining = ChronoUnit.DAYS.between(today, expiryDate);
			alerts.add(DocumentExpiryAlert.builder().vehicleId(vehicle.getId())
					.registrationNumber(vehicle.getRegistrationNumber()).documentType(documentType)
					.expiryDate(expiryDate).daysRemaining(daysRemaining).expired(daysRemaining < 0).build());
		}
	}

	public TransportVehicle findVehicle(Long id) {
		return vehicleRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transport vehicle not found: " + id));
	}

	private void mapToEntity(TransportVehicleRequest request, TransportVehicle vehicle, String regNo) {
		vehicle.setRegistrationNumber(regNo);
		vehicle.setVehicleType(request.getVehicleType());
		vehicle.setModel(request.getModel());
		vehicle.setSeatingCapacity(request.getSeatingCapacity());
		vehicle.setInsuranceExpiry(request.getInsuranceExpiry());
		vehicle.setFitnessExpiry(request.getFitnessExpiry());
		vehicle.setPermitExpiry(request.getPermitExpiry());
		vehicle.setPucExpiry(request.getPucExpiry());
		vehicle.setRemarks(request.getRemarks());
	}

	private TransportVehicleResponse mapToResponse(TransportVehicle vehicle) {
		return TransportVehicleResponse.builder().id(vehicle.getId())
				.registrationNumber(vehicle.getRegistrationNumber()).vehicleType(vehicle.getVehicleType())
				.model(vehicle.getModel()).seatingCapacity(vehicle.getSeatingCapacity())
				.insuranceExpiry(vehicle.getInsuranceExpiry()).fitnessExpiry(vehicle.getFitnessExpiry())
				.permitExpiry(vehicle.getPermitExpiry()).pucExpiry(vehicle.getPucExpiry())
				.remarks(vehicle.getRemarks()).active(vehicle.isActive()).build();
	}

	private TransportDocumentRuleResponse mapRuleToResponse(TransportDocumentRule rule) {
		return TransportDocumentRuleResponse.builder().id(rule.getId()).vehicleType(rule.getVehicleType())
				.requireInsurance(rule.isRequireInsurance()).requireFitness(rule.isRequireFitness())
				.requirePermit(rule.isRequirePermit()).requirePuc(rule.isRequirePuc())
				.alertDaysBefore(rule.getAlertDaysBefore()).build();
	}

	private static Specification<TransportVehicle> byFilters(VehicleType vehicleType, Boolean active, String search) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (vehicleType != null) {
				predicates.add(cb.equal(root.get("vehicleType"), vehicleType));
			}
			if (active != null) {
				predicates.add(cb.equal(root.get("isActive"), active));
			}
			if (search != null && !search.isBlank()) {
				String like = "%" + search.trim().toUpperCase() + "%";
				predicates.add(cb.or(cb.like(cb.upper(root.get("registrationNumber")), like),
						cb.like(cb.upper(root.get("model")), like)));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
