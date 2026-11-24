package com.hipro.school.backend.services.transport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.StudentFeeResponse;
import com.hipro.school.backend.dtos.transport.TransportFeeGenerateRequest;
import com.hipro.school.backend.entities.FeeStructure;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.entities.StudentFee;
import com.hipro.school.backend.entities.TransportRoute;
import com.hipro.school.backend.enums.FeeFrequency;
import com.hipro.school.backend.enums.FeeStatus;
import com.hipro.school.backend.enums.FeeType;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.FeeStructureRepository;
import com.hipro.school.backend.repositories.StudentFeeRepository;
import com.hipro.school.backend.repositories.StudentRepository;
import com.hipro.school.backend.repositories.TransportRouteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Integrates transport charges with the existing student fee system. Transport
 * fees are stored as {@link StudentFee} rows linked to a {@link FeeStructure}
 * of type {@code TRANSPORT_FEE}, so collection, receipts and payment history
 * reuse {@code StudentFeeService} instead of duplicating payment logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransportFeeService {

	private final StudentFeeRepository studentFeeRepository;
	private final FeeStructureRepository feeStructureRepository;
	private final StudentRepository studentRepository;
	private final TransportRouteRepository routeRepository;

	/**
	 * Generates a transport {@link StudentFee} for a student from the route's
	 * transport fee. Reuses (or creates) the class-level TRANSPORT_FEE structure.
	 */
	public StudentFeeResponse generateTransportFee(TransportFeeGenerateRequest request) {
		log.info("Generating transport fee for student {} on route {}", request.getStudentId(), request.getRouteId());
		Student student = studentRepository.findById(request.getStudentId()).orElseThrow(
				() -> new ResourceNotFoundException("Student not found: " + request.getStudentId()));
		TransportRoute route = routeRepository.findById(request.getRouteId()).orElseThrow(
				() -> new ResourceNotFoundException("Transport route not found: " + request.getRouteId()));
		FeeStructure structure = findOrCreateTransportStructure(student, route,
				request.getAcademicYear());
		if (studentFeeRepository.existsByStudent_IdAndFeeStructure_Id(student.getId(), structure.getId())) {
			throw new ServiceException("Transport fee already generated for student "
					+ student.getAdmissionNumber() + " for " + structure.getTitle());
		}
		StudentFee fee = new StudentFee();
		fee.setStudent(student);
		fee.setFeeStructure(structure);
		fee.setAmount(route.getTransportFee());
		fee.setPaidAmount(BigDecimal.ZERO);
		fee.setDueAmount(route.getTransportFee());
		fee.setFeeDate(request.getFeeDate() != null ? request.getFeeDate() : LocalDate.now());
		fee.setDueDate(request.getDueDate() != null ? request.getDueDate()
				: LocalDate.now().plusMonths(1).withDayOfMonth(10));
		fee.setStatus(FeeStatus.PENDING);
		StudentFee saved = studentFeeRepository.save(fee);
		return mapToResponse(saved);
	}

	/** Outstanding (pending/partial/overdue) transport fees across students. */
	@Transactional(readOnly = true)
	public PageResponse<StudentFeeResponse> getOutstandingTransportFees(Long studentId, Pageable pageable) {
		Page<StudentFee> page = studentFeeRepository.findAll((root, query, cb) -> {
			var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
			predicates.add(cb.equal(root.get("feeStructure").get("feeType"), FeeType.TRANSPORT_FEE));
			predicates.add(root.get("status").in(List.of(FeeStatus.PENDING, FeeStatus.PARTIAL, FeeStatus.OVERDUE)));
			if (studentId != null) {
				predicates.add(cb.equal(root.get("student").get("id"), studentId));
			}
			return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
		}, pageable);
		return PageResponse.from(page.map(this::mapToResponse));
	}

	/** Total outstanding transport dues (used by the transport dashboard). */
	@Transactional(readOnly = true)
	public BigDecimal getTotalOutstanding() {
		return studentFeeRepository.sumDueAmountsByStatuses(
				List.of(FeeStatus.PENDING, FeeStatus.PARTIAL, FeeStatus.OVERDUE)) != null
						? studentFeeRepository.sumDueAmountsByStatuses(
								List.of(FeeStatus.PENDING, FeeStatus.PARTIAL, FeeStatus.OVERDUE))
						: BigDecimal.ZERO;
	}

	private FeeStructure findOrCreateTransportStructure(Student student, TransportRoute route, String academicYear) {
		List<FeeStructure> candidates = feeStructureRepository
				.findAllByClassRoom_Id(student.getSchoolClass().getId()).stream()
				.filter(fs -> fs.getFeeType() == FeeType.TRANSPORT_FEE
						&& fs.getAmount().compareTo(route.getTransportFee()) == 0)
				.toList();
		if (!candidates.isEmpty()) {
			return candidates.get(0);
		}
		FeeStructure structure = FeeStructure.builder()
				.title("Transport Fee - " + route.getRouteName() + " (" + route.getTransportFee().toPlainString()
						+ ")")
				.feeType(FeeType.TRANSPORT_FEE).frequency(FeeFrequency.MONTHLY).classRoom(student.getSchoolClass())
				.amount(route.getTransportFee()).academicYear(academicYear).description(
						"Auto-created transport fee structure for route " + route.getRouteName())
				.build();
		return feeStructureRepository.save(structure);
	}

	private StudentFeeResponse mapToResponse(StudentFee fee) {
		return StudentFeeResponse.builder().id(fee.getId()).studentId(fee.getStudent().getId())
				.studentName(fee.getStudent().getStudentUser() != null
						? fee.getStudent().getStudentUser().getFullName()
						: null)
				.classId(fee.getFeeStructure().getClassRoom().getId())
				.className(fee.getFeeStructure().getClassRoom().getClassName())
				.feeStructureId(fee.getFeeStructure().getId()).feeType(fee.getFeeStructure().getFeeType())
				.amount(fee.getAmount()).paidAmount(fee.getPaidAmount()).dueAmount(fee.getDueAmount())
				.dueDate(fee.getDueDate()).status(fee.getStatus()).build();
	}
}
