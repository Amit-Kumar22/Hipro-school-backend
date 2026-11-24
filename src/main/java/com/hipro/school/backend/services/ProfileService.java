package com.hipro.school.backend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.hipro.school.backend.dtos.CustomerResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.StaffUserRequest;
import com.hipro.school.backend.dtos.StudentFormRequest;
import com.hipro.school.backend.dtos.StudentRequest;
import com.hipro.school.backend.dtos.UploadResponse;
import com.hipro.school.backend.entities.Role;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.entities.Teacher;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.enums.UserStatus;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.SchoolClassRepository;
import com.hipro.school.backend.repositories.SchoolRepository;
import com.hipro.school.backend.repositories.StudentRepository;
import com.hipro.school.backend.repositories.TeacherRepository;
import com.hipro.school.backend.repositories.TokenRepository;
import com.hipro.school.backend.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProfileService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private SchoolClassRepository classRepository;

	@Autowired
	private TeacherRepository teacherRepository;

	public PageResponse<CustomerResponse> getAllSchoolStaff(String email, Role role, String search, Pageable pageable) {
		log.info("Fetching all school staff for school code: {}", search);
		Specification<User> specification = staffFilter(role, search);
		Page<User> pageUser = userRepository.findAll(specification, pageable);
		Page<CustomerResponse> userResponsePage = pageUser.map(user -> modelMapper.map(user, CustomerResponse.class));
		return PageResponse.from(userResponsePage);
	}

	public PageResponse<Teacher> getAllTeacher(String email, Role role, String search, Pageable pageable) {
		log.info("Fetching all school staff for school code: {}", search);
		Specification<Teacher> specification = teacherFilter(role, search);
		Page<Teacher> teacherPage = teacherRepository.findAll(specification, pageable);
		return PageResponse.from(teacherPage);
	}

	public PageResponse<Student> getAllSchoolStudent(Long classId, String search, String feeStatus, UserStatus status,
			Pageable pageable) {
		log.info("Fetching all students for school code: {}", classId, search, feeStatus, status);
		Specification<Student> specification = filter(classId, search, feeStatus, status);
		Page<Student> userResponsePage = studentRepository.findAll(specification, pageable);
		return PageResponse.from(userResponsePage);
	}

	public PageResponse<User> getAllSchoolTeacher(String email, Pageable pageable) {
		log.info("Fetching all teachers with pageable: {}", pageable);
		Page<User> userResponsePage = userRepository.findAllByRole(Role.TEACHER, pageable);
		return PageResponse.from(userResponsePage);
	}

	public void addNewStudent(StudentRequest request) {
		log.info("Adding student: name={}", request.getName());
		authenticationService.studentRegister(request);
	}

	public void addAdmissionForm(StudentFormRequest request) {
		log.info("Adding student admission form: name={}", request.getName());
		authenticationService.studentAdmissionForm(request);
	}

	public void saveTeacherDetails(StaffUserRequest request, String email) {
		authenticationService.saveTeacherDetails(request, email);
	}

	public String logout(String email) {
		log.info(">>logout({})", email);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not Found By : " + email));
		tokenRepository.deleteAll(user.getTokens());
		return "Logout Successfully";
	}

	public User getProfile(String email) {
		log.info("Fetching profile for email: {}", email);
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + email));
	}

	@Transactional
	public void deleteStudentDetails(Long studentId) {
		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException("Student not found by Id :: " + studentId));
		User studentUser = student.getStudentUser();
		User parentUser = student.getParentUser();
		studentUser.setStatus(UserStatus.DELETED);
		parentUser.setStatus(UserStatus.DELETED);
		student.setActive(false);
		studentRepository.save(student);
	}

	@Transactional
	public void updateStudentDetails(Long studentId, StudentRequest request) {
		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException("Student not found by Id :: " + studentId));
		User studentUser = student.getStudentUser();
		studentUser.setFullName(request.getName());
		studentUser.setPhone(request.getParentPhone());
		User parentUser = student.getParentUser();
		parentUser.setEmail(request.getParentEmail().toLowerCase());
		parentUser.setPhone(request.getParentPhone());
		parentUser.setFullName(request.getFatherName());
		// Student Details
		student.setFatherName(request.getFatherName());
		student.setMotherName(request.getMotherName());
		student.setDob(request.getDob());
		student.setAddress(request.getAddress());
		student.setPincode(request.getPincode());
		student.setGender(request.getGender());
		SchoolClass schoolClass = classRepository.findById(request.getClassId())
				.orElseThrow(() -> new ResourceNotFoundException("Class not found by Id :: " + request.getClassId()));
		student.setSchoolClass(schoolClass);
		studentRepository.save(student);
	}

	@Transactional
	public UploadResponse uploadNewStudentFile(MultipartFile file, String email, String schoolCode) {
		log.info("Starting student raw upload. user={}, schoolCode={}", email, schoolCode);
		schoolRepository.findBySchoolCode(schoolCode)
				.orElseThrow(() -> new ResourceNotFoundException("School not found with code: " + schoolCode));
		return new UploadResponse();
	}

	public static Specification<User> staffFilter(Role role, String search) {
		return (root, query, cb) -> {
			var predicates = cb.conjunction();
			// Role filter
			if (role != null) {

				predicates = cb.and(predicates, cb.equal(root.get("role"), role));
			}
			// Search by name, email, phone
			if (StringUtils.hasText(search)) {

				String searchValue = "%" + search.trim().toLowerCase() + "%";

				var searchPredicate = cb.or(cb.like(cb.lower(root.get("fullName")), searchValue),
						cb.like(cb.lower(root.get("email")), searchValue),
						cb.like(cb.lower(root.get("phone")), searchValue));
				predicates = cb.and(predicates, searchPredicate);
			}
			return predicates;
		};
	}

	public static Specification<Student> filter(Long classId, String search, String feeStatus, UserStatus status) {

		return (root, query, cb) -> {
			var predicates = cb.conjunction();
			// StudentUser join
			var studentUser = root.join("studentUser");

			// Class filter
			if (classId != null) {
				predicates = cb.and(predicates, cb.equal(root.get("schoolClass").get("id"), classId));
			}

			// Search by name, phone, email, roll number
			if (StringUtils.hasText(search)) {
				String searchValue = "%" + search.trim().toLowerCase() + "%";
				var searchPredicate = cb.or(
						// Student name
						cb.like(cb.lower(studentUser.get("fullName")), searchValue),
						// Student email
						cb.like(cb.lower(studentUser.get("email")), searchValue),
						// Student phone
						cb.like(cb.lower(studentUser.get("phone")), searchValue),
						// Roll number
						cb.like(cb.lower(root.get("rollNumber")), searchValue));
				predicates = cb.and(predicates, searchPredicate);
			}

			// Fee status
			if (StringUtils.hasText(feeStatus)) {
				predicates = cb.and(predicates, cb.equal(root.get("feeStatus"), feeStatus));
			}

			// User status
			if (status != null) {
				predicates = cb.and(predicates, cb.equal(studentUser.get("status"), status));
			}

			return predicates;
		};
	}

	public static Specification<Teacher> teacherFilter(Role role, String search) {
		return (root, query, cb) -> {
			var predicates = cb.conjunction();
			// Join Teacher -> User
			var user = root.join("teacherUser");

			// Role filter
			if (role != null) {
				predicates = cb.and(predicates, cb.equal(user.get("role"), role));
			}
			// Search by name, email, phone, employee code
			if (StringUtils.hasText(search)) {
				String searchValue = "%" + search.trim().toLowerCase() + "%";
				var searchPredicate = cb.or(cb.like(cb.lower(user.get("fullName")), searchValue),
						cb.like(cb.lower(user.get("email")), searchValue),
						cb.like(cb.lower(user.get("phone")), searchValue),
						cb.like(cb.lower(root.get("employeeCode")), searchValue));
				predicates = cb.and(predicates, searchPredicate);
			}
			return predicates;
		};
	}

}