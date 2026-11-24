package com.hipro.school.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.StaffSalaryTemplateRequest;
import com.hipro.school.backend.dtos.StaffSalaryTemplateResponse;
import com.hipro.school.backend.entities.StaffSalaryTemplate;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.StaffSalaryTemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StaffSalaryTemplateService {

	private final StaffSalaryTemplateRepository templateRepository;

	public void create(StaffSalaryTemplateRequest request) {
		log.info("Creating global staff salary template");
		StaffSalaryTemplate template = mapToEntity(request, null);
		template.setActive(true);
		templateRepository.save(template);
	}

	@Transactional(readOnly = true)
	public List<StaffSalaryTemplateResponse> getAll() {
		log.info("Fetching all staff salary templates");
		return templateRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Transactional(readOnly = true)
	public StaffSalaryTemplateResponse getById(Long id) {
		log.info("Fetching staff salary template by id: {}", id);
		return mapToResponse(findTemplate(id));
	}

	@Transactional(readOnly = true)
	public StaffSalaryTemplateResponse getActive() {
		log.info("Fetching active staff salary template");
		StaffSalaryTemplate template = templateRepository.findByActiveTrue()
				.orElseThrow(() -> new ResourceNotFoundException("No active salary template found"));
		return mapToResponse(template);
	}

	public void update(Long id, StaffSalaryTemplateRequest request) {
		log.info("Updating staff salary template id: {} with request: {}", id, request);
		StaffSalaryTemplate existing = findTemplate(id);

		if (Boolean.TRUE.equals(request.getActive()) && !existing.getActive()) {
			templateRepository.findByActiveTrue().ifPresent(active -> {
				active.setActive(false);
				templateRepository.save(active);
			});
		}

		mapToEntity(request, existing);
		templateRepository.save(existing);
	}

	public void activate(Long id) {
		log.info("Activating staff salary template id: {}", id);
		StaffSalaryTemplate template = findTemplate(id);

		templateRepository.findByActiveTrue().ifPresent(active -> {
			active.setActive(false);
			templateRepository.save(active);
		});

		template.setActive(true);
		templateRepository.save(template);
	}

	public void delete(Long id) {
		log.info("Deleting staff salary template id: {}", id);
		StaffSalaryTemplate template = findTemplate(id);
		if (Boolean.TRUE.equals(template.getActive())) {
			throw new ServiceException("Cannot delete the active template. Activate another template first.");
		}
		templateRepository.delete(template);
		log.info("Deleted staff salary template id: {}", id);
	}

	private StaffSalaryTemplate findTemplate(Long id) {
		return templateRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Staff salary template not found: " + id));
	}

	private StaffSalaryTemplateResponse mapToResponse(StaffSalaryTemplate template) {
		return StaffSalaryTemplateResponse.builder().id(template.getId()).basicSalary(template.getBasicSalary())
				.hra(template.getHra()).transportAllowance(template.getTransportAllowance())
				.medicalAllowance(template.getMedicalAllowance()).otherAllowance(template.getOtherAllowance())
				.pf(template.getPf()).professionalTax(template.getProfessionalTax())
				.otherDeduction(template.getOtherDeduction()).active(template.getActive())
				.createdAt(template.getCreated()).updatedAt(template.getUpdated()).build();
	}

	private StaffSalaryTemplate mapToEntity(StaffSalaryTemplateRequest request, StaffSalaryTemplate template) {
		if (template == null) {
			template = new StaffSalaryTemplate();
		}
		template.setBasicSalary(request.getBasicSalary());
		template.setHra(request.getHra());
		template.setTransportAllowance(request.getTransportAllowance());
		template.setMedicalAllowance(request.getMedicalAllowance());
		template.setOtherAllowance(request.getOtherAllowance());
		template.setPf(request.getPf());
		template.setProfessionalTax(request.getProfessionalTax());
		template.setOtherDeduction(request.getOtherDeduction());
		template.setActive(request.getActive() != null ? request.getActive() : true);
		return template;
	}
}
