package com.hipro.school.backend.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.StudentLeaveRequest;
import com.hipro.school.backend.dtos.StudentLeaveReviewRequest;
import com.hipro.school.backend.entities.School;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.entities.StudentLeave;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.enums.LeaveStatus;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.SchoolRepository;
import com.hipro.school.backend.repositories.StudentLeaveRepository;
import com.hipro.school.backend.repositories.StudentRepository;
import com.hipro.school.backend.repositories.UserRepository;
import com.hipro.school.backend.security.AuthDetailsProvider;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StudentLeaveService {

	@Autowired
	private StudentLeaveRepository studentLeaveRepository;

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private StudentRepository studentRepository;


	@Autowired
	private UserRepository userRepository;

	public PageResponse<StudentLeave> getAll(String schoolCode, Long studentId, LeaveStatus status, Pageable pageable) {
		log.info("Fetching student leaves for school code: {}, studentId: {}, status: {}", schoolCode, studentId,
				status);
		School school = getSchoolByCode(schoolCode);
		Page<StudentLeave> page = studentLeaveRepository.findAll(byFilters(school, studentId, status), pageable);
		return PageResponse.from(page);
	}

	public StudentLeave getById(Long id) {
		log.info("Fetching student leave by id: {}", id);
		return studentLeaveRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Student leave not found. :: " + id));
	}

	public void create(StudentLeaveRequest request) {
		log.info("Creating student leave for studentId: {}", request.getStudentId());
		School school = getSchoolByCode(request.getSchoolCode());
		Student student = studentRepository.findById(request.getStudentId())
				.orElseThrow(() -> new ResourceNotFoundException("Student not found. :: " + request.getStudentId()));
		validateDateRange(request.getFromDate(), request.getToDate());
		validateOverlappingLeave(student.getId(), request.getFromDate(), request.getToDate(), null);

		StudentLeave leave = new StudentLeave();
		leave.setSchool(school);
		leave.setStudent(student);
		leave.setLeaveType(request.getLeaveType());
		leave.setFromDate(request.getFromDate());
		leave.setToDate(request.getToDate());
		applyDuration(leave, request.getFromDate(), request.getToDate());
		leave.setReason(request.getReason());
		leave.setStatus(LeaveStatus.PENDING);
		studentLeaveRepository.save(leave);
	}

	@Transactional
	public void update(Long id, StudentLeaveRequest request) {
		log.info("Updating student leave id: {}", id);
		StudentLeave leave = getById(id);
		if (leave.getStatus() != LeaveStatus.PENDING) {
			throw new ServiceException("Only pending leaves can be updated. :: " + id);
		}
		validateDateRange(request.getFromDate(), request.getToDate());
		validateOverlappingLeave(leave.getStudent().getId(), request.getFromDate(), request.getToDate(), id);

		leave.setLeaveType(request.getLeaveType());
		leave.setFromDate(request.getFromDate());
		leave.setToDate(request.getToDate());
		applyDuration(leave, request.getFromDate(), request.getToDate());
		leave.setReason(request.getReason());
	}

	@Transactional
	public void review(Long id, StudentLeaveReviewRequest request) {
		log.info("Reviewing student leave id: {} with status: {}", id, request.getStatus());
		if (request.getStatus() != LeaveStatus.APPROVED && request.getStatus() != LeaveStatus.REJECTED) {
			throw new ServiceException("Leave can only be approved or rejected. :: " + request.getStatus());
		}
		StudentLeave leave = getById(id);
		if (leave.getStatus() != LeaveStatus.PENDING) {
			throw new ServiceException("Only pending leaves can be reviewed. :: " + id);
		}
		User reviewer = userRepository.findByEmail(AuthDetailsProvider.getLoggedEmail())
				.orElseThrow(() -> new ResourceNotFoundException("Logged in user not found."));
		leave.setStatus(request.getStatus());
		leave.setRemarks(request.getRemarks());
		leave.setReviewedBy(reviewer);
		leave.setReviewedAt(LocalDateTime.now());
	}

	@Transactional
	public void cancel(Long id) {
		log.info("Cancelling student leave id: {}", id);
		StudentLeave leave = getById(id);
		if (leave.getStatus() == LeaveStatus.CANCELLED || leave.getStatus() == LeaveStatus.REJECTED) {
			throw new ServiceException("Leave is already " + leave.getStatus() + ". :: " + id);
		}
		leave.setStatus(LeaveStatus.CANCELLED);
		leave.setReviewedAt(LocalDateTime.now());
	}

	public void delete(Long id) {
		log.info("Deleting student leave id: {}", id);
		StudentLeave leave = getById(id);
		studentLeaveRepository.delete(leave);
	}

	private void applyDuration(StudentLeave leave, LocalDate fromDate, LocalDate toDate) {
		long days = ChronoUnit.DAYS.between(fromDate, toDate) + 1;
		leave.setNumberOfDays((int) days);
	}

	private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
		if (fromDate.isAfter(toDate)) {
			throw new ServiceException("fromDate must not be after toDate.");
		}
	}

	private void validateOverlappingLeave(Long studentId, LocalDate fromDate, LocalDate toDate, Long excludeId) {
		List<StudentLeave> overlaps = studentLeaveRepository.findOverlappingLeaves(studentId,
				List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED), fromDate, toDate);
		boolean hasOverlap = overlaps.stream().anyMatch(l -> !l.getId().equals(excludeId));
		if (hasOverlap) {
			throw new ServiceException("An active leave already exists for this date range. :: " + studentId);
		}
	}

	private School getSchoolByCode(String schoolCode) {
		return schoolRepository.findBySchoolCode(schoolCode)
				.orElseThrow(() -> new ResourceNotFoundException("School not found. :: " + schoolCode));
	}

	private static Specification<StudentLeave> byFilters(School school, Long studentId, LeaveStatus status) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("school"), school));
			if (studentId != null) {
				predicates.add(cb.equal(root.get("student").get("id"), studentId));
			}
			if (status != null) {
				predicates.add(cb.equal(root.get("status"), status));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

}
