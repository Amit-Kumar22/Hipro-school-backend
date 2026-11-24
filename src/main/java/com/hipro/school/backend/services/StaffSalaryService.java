package com.hipro.school.backend.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.StaffSalaryRequest;
import com.hipro.school.backend.dtos.StaffSalaryResponse;
import com.hipro.school.backend.entities.School;
import com.hipro.school.backend.entities.StaffSalary;
import com.hipro.school.backend.entities.StaffSalaryTemplate;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.enums.SalaryStatus;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.SchoolRepository;
import com.hipro.school.backend.repositories.StaffSalaryRepository;
import com.hipro.school.backend.repositories.StaffSalaryTemplateRepository;
import com.hipro.school.backend.repositories.UserRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StaffSalaryService {

	private final StaffSalaryRepository staffSalaryRepository;
	private final UserRepository userRepository;
	private final StaffSalaryTemplateRepository templateRepository;
	private final SalaryPdfService salaryPdfService;
	private final SchoolRepository schoolRepository;

	public void create(StaffSalaryRequest request) {
		log.info("Creating staff salary for user id: {} and month: {}", request.getUserId(), request.getSalaryMonth());
		User user = findUser(request.getUserId());

		if (staffSalaryRepository.existsByUserIdAndSalaryMonth(user.getId(), request.getSalaryMonth())) {
			throw new ServiceException("Salary already exists for this user in " + request.getSalaryMonth());
		}

		StaffSalaryTemplate template = resolveTemplate(request.getGlobalTemplateId());
		StaffSalary salary = mapToEntity(request, user, null, template);
		staffSalaryRepository.save(salary);
	}

	@Transactional(readOnly = true)
	public PageResponse<StaffSalaryResponse> getSalaries(Long userId, SalaryStatus status, Pageable pageable) {
		log.info("Fetching staff salaries with userId: {}, status: {}, pageable: {}", userId, status, pageable);
		Page<StaffSalary> page = staffSalaryRepository.findAll(byFilters(userId, status), pageable);
		return PageResponse.from(page.map(this::mapToResponse));
	}

	@Transactional(readOnly = true)
	public StaffSalaryResponse getById(Long id) {
		log.info("Fetching staff salary by id: {}", id);
		return mapToResponse(findStaffSalary(id));
	}

	@Transactional(readOnly = true)
	public PageResponse<StaffSalaryResponse> getSalariesByEmail(String email, SalaryStatus status, Pageable pageable) {
		log.info("Fetching staff salaries for email: {}, status: {}, pageable: {}", email, status, pageable);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
		Page<StaffSalary> page = staffSalaryRepository.findAll(byFilters(user.getId(), status), pageable);
		return PageResponse.from(page.map(this::mapToResponse));
	}

	public void update(Long id, StaffSalaryRequest request) {
		log.info("Updating staff salary id: {} with request: {}", id, request);
		StaffSalary existing = findStaffSalary(id);
		User user = findUser(request.getUserId());

		if (!existing.getUser().getId().equals(user.getId())
				|| !existing.getSalaryMonth().equals(request.getSalaryMonth())) {
			if (staffSalaryRepository.existsByUserIdAndSalaryMonthAndIdNot(user.getId(), request.getSalaryMonth(),
					id)) {
				throw new ServiceException("Salary already exists for this user in " + request.getSalaryMonth());
			}
		}

		StaffSalaryTemplate template = resolveTemplate(request.getGlobalTemplateId());
		mapToEntity(request, user, existing, template);
		staffSalaryRepository.save(existing);
	}

	public void updateStatus(Long id, SalaryStatus status) {
		log.info("Updating status of staff salary id: {} to {}", id, status);
		StaffSalary salary = findStaffSalary(id);
		salary.setStatus(status);
		staffSalaryRepository.save(salary);
	}

	public void delete(Long id) {
		log.info("Deleting staff salary id: {}", id);
		staffSalaryRepository.delete(findStaffSalary(id));
		log.info("Deleted staff salary id: {}", id);
	}

	@Transactional(readOnly = true)
	public byte[] generateSalaryPdf(Long id) {
		log.info("Generating salary PDF for salary id: {}", id);
		StaffSalary salary = findStaffSalary(id);
		School school = schoolRepository.findAll().stream().findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("School not found. Please configure school details first."));
		return salaryPdfService.generateSalaryPdf(salary, school);
	}

	private StaffSalary findStaffSalary(Long id) {
		return staffSalaryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Staff salary not found: " + id));
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
	}

	private StaffSalaryTemplate resolveTemplate(Long templateId) {
		return templateRepository.findById(templateId).orElseThrow(
				() -> new ResourceNotFoundException("No active salary template found. Please create one first."));
	}

	private StaffSalaryResponse mapToResponse(StaffSalary salary) {
		return StaffSalaryResponse.builder().id(salary.getId()).userId(salary.getUser().getId())
				.userName(salary.getUser().getFullName()).userEmail(salary.getUser().getEmail())
				.globalTemplateId(salary.getGlobalTemplate().getId()).salaryMonth(salary.getSalaryMonth())
				.basicSalary(salary.getBasicSalary()).totalAllowances(salary.getTotalAllowances())
				.totalDeductions(salary.getTotalDeductions()).grossSalary(salary.getGrossSalary())
				.netSalary(salary.getNetSalary()).workingDays(salary.getWorkingDays()).paidDays(salary.getPaidDays())
				.unpaidDays(salary.getUnpaidDays()).status(salary.getStatus()).paymentDate(salary.getPaymentDate())
				.paymentReference(salary.getPaymentReference()).remarks(salary.getRemarks())
				.createdAt(salary.getCreated()).updatedAt(salary.getUpdated()).build();
	}

	private StaffSalary mapToEntity(StaffSalaryRequest request, User user, StaffSalary salary,
			StaffSalaryTemplate template) {
		if (salary == null) {
			salary = new StaffSalary();
		}
		salary.setUser(user);
		salary.setGlobalTemplate(template);
		salary.setSalaryMonth(request.getSalaryMonth());
		salary.setBasicSalary(request.getBasicSalary());
		salary.setWorkingDays(request.getWorkingDays());
		salary.setPaidDays(request.getPaidDays());
		salary.setUnpaidDays(request.getUnpaidDays());
		salary.setStatus(request.getStatus() != null ? request.getStatus() : SalaryStatus.DRAFT);
		salary.setPaymentDate(request.getPaymentDate());
		salary.setPaymentReference(request.getPaymentReference());
		salary.setRemarks(request.getRemarks());

		BigDecimal totalAllowances = calculateTotalAllowances(template);
		BigDecimal totalDeductions = calculateTotalDeductions(template);
		BigDecimal grossSalary = request.getBasicSalary().add(totalAllowances);
		BigDecimal netSalary = grossSalary.subtract(totalDeductions);

		salary.setTotalAllowances(totalAllowances);
		salary.setTotalDeductions(totalDeductions);
		salary.setGrossSalary(grossSalary);
		salary.setNetSalary(netSalary);

		return salary;
	}

	private BigDecimal calculateTotalAllowances(StaffSalaryTemplate template) {
		BigDecimal total = BigDecimal.ZERO;
		total = total.add(template.getHra() != null ? template.getHra() : BigDecimal.ZERO);
		total = total
				.add(template.getTransportAllowance() != null ? template.getTransportAllowance() : BigDecimal.ZERO);
		total = total.add(template.getMedicalAllowance() != null ? template.getMedicalAllowance() : BigDecimal.ZERO);
		total = total.add(template.getOtherAllowance() != null ? template.getOtherAllowance() : BigDecimal.ZERO);
		return total;
	}

	private BigDecimal calculateTotalDeductions(StaffSalaryTemplate template) {
		BigDecimal total = BigDecimal.ZERO;
		total = total.add(template.getPf() != null ? template.getPf() : BigDecimal.ZERO);
		total = total.add(template.getProfessionalTax() != null ? template.getProfessionalTax() : BigDecimal.ZERO);
		total = total.add(template.getOtherDeduction() != null ? template.getOtherDeduction() : BigDecimal.ZERO);
		return total;
	}

	public static Specification<StaffSalary> byFilters(Long userId, SalaryStatus status) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (userId != null) {
				predicates.add(cb.equal(root.get("user").get("id"), userId));
			}
			if (status != null) {
				predicates.add(cb.equal(root.get("status"), status));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
