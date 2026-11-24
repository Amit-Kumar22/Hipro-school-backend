package com.hipro.school.backend.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.AuthCodeRequest;
import com.hipro.school.backend.dtos.AuthenticationRequest;
import com.hipro.school.backend.dtos.AuthenticationResponse;
import com.hipro.school.backend.dtos.ForgotPasswordRequest;
import com.hipro.school.backend.dtos.LoggedInUserDetails;
import com.hipro.school.backend.dtos.MailRequest;
import com.hipro.school.backend.dtos.PasswordRequest;
import com.hipro.school.backend.dtos.RegisterRequest;
import com.hipro.school.backend.dtos.ResetPasswordRequest;
import com.hipro.school.backend.dtos.StaffUserRequest;
import com.hipro.school.backend.dtos.StudentFormRequest;
import com.hipro.school.backend.dtos.StudentRequest;
import com.hipro.school.backend.dtos.UserAuthenticationResponse;
import com.hipro.school.backend.dtos.UserResponse;
import com.hipro.school.backend.dtos.VerificationRequest;
import com.hipro.school.backend.entities.Role;
import com.hipro.school.backend.entities.School;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.entities.Subject;
import com.hipro.school.backend.entities.Teacher;
import com.hipro.school.backend.entities.Token;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.enums.BloodGroup;
import com.hipro.school.backend.enums.TokenType;
import com.hipro.school.backend.enums.UserStatus;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.SchoolClassRepository;
import com.hipro.school.backend.repositories.SchoolRepository;
import com.hipro.school.backend.repositories.StudentRepository;
import com.hipro.school.backend.repositories.SubjectRepository;
import com.hipro.school.backend.repositories.TeacherRepository;
import com.hipro.school.backend.repositories.TokenRepository;
import com.hipro.school.backend.repositories.UserRepository;
import com.hipro.school.backend.security.JwtService;
import com.hipro.school.backend.util.RandomUtils;
import com.hipro.school.backend.util.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthenticationService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private EmailService emailService;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private SchoolClassRepository classRepository;

	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private SubjectRepository subjectRepository;

	@Value("${origin.url}")
	private String originUrl;

	// Customer Register
	@Transactional
	public ApiResponse<Void> schoolAdminRegister(RegisterRequest request) {
		validateDuplicateUser(request.getEmail(), request.getPhone());
		User user = createUser(request.getName(), request.getEmail(), request.getPhone(), request.getPassword(),
				Role.ADMIN);
		user.setStatus(UserStatus.ACTIVE);
		String otp = RandomUtils.generateOtp(6, false);
		user.setOtp(otp);
		log.info("OTP generated for registration email: {}", request.getEmail());
		Map<String, Object> model = new HashMap<>();
		model.put("Name", request.getName());
		MailRequest emailrequest = new MailRequest();
		emailrequest.setName(request.getName());
		emailrequest.setSubject("Confirmation Email");
		emailrequest.setTo(request.getEmail());
		emailrequest.setToken(otp);
		// emailService.sendEmail(emailrequest, model);
		user.setOtp(otp);
		repository.save(user);
		return ApiResponse.success(HttpStatus.CREATED,
				"Registration successful. Please check your registered email and spam folder.", null);
	}

	// New Student Registration
	@Transactional
	public void studentRegister(StudentRequest request) {
		// 1. Validate class
		SchoolClass schoolClass = classRepository.findById(request.getClassId())
				.orElseThrow(() -> new ResourceNotFoundException("School class not found: " + request.getClassId()));

		// 2. Generate student email
		String username = Utils.getFirstName(request.getName());
		// Generate Email
		String studentEmail = generateEmail(username);
		// 3. Validate duplicates
		validateDuplicateUser(studentEmail, request.getParentPhone());

		// 4. Generate passwords
		String studentPassword = Utils.generateRandomString(8);

		// 5. Create STUDENT user
		User studentUser = createUserWithPass(request.getName(), studentEmail, request.getParentPhone(), Role.STUDENT,
				studentPassword);
		studentUser.setStatus(UserStatus.ACTIVE);
		studentUser = repository.save(studentUser);
		User parentUser = null;
		String parentRawPassword = Utils.generateRandomString(8);
		if (request.getFatherName() != null && request.getParentEmail() != null
				&& !request.getParentEmail().isBlank()) {
			parentUser = createUserWithPass(request.getFatherName(), request.getParentEmail(), request.getParentPhone(),
					Role.PARENT, parentRawPassword);
			parentUser.setStatus(UserStatus.ACTIVE);
			parentUser = repository.save(parentUser);
		}
		Student student = new Student();
		student.setFatherName(request.getFatherName());
		student.setMotherName(request.getMotherName());
		LocalDate now = LocalDate.now(ZoneId.of("Asia/Kolkata"));
		int startYear = now.getMonthValue() >= 4 ? now.getYear() : now.getYear() - 1;
		student.setAcademicYear(startYear + "-" + String.valueOf(startYear + 1).substring(2));
		student.setAdmissionDate(now);

		student.setStudentUser(studentUser);
		String addNumber = generateAdmissionNumber();
		student.setAdmissionNumber(addNumber);
		student.setStudentCode("STUD-" + LocalDate.now().getYear() + "-" + addNumber);
		if (parentUser != null) {
			student.setParentUser(parentUser);
		}
		student.setSchoolClass(schoolClass);
		student.setAddress(request.getAddress());
		student.setPincode(request.getPincode());
		student.setGender(request.getGender());
		// DO NOT store raw password here
		student.setRawPassword(studentPassword);
		studentRepository.save(student);

		Map<String, Object> model = new HashMap<>();
		model.put("Name", request.getFatherName());
		model.put("studentName", request.getName());
		model.put("loginId", request.getParentEmail());
		model.put("code", parentRawPassword);
		model.put("loginUrl", originUrl);
		model.put("currentYear", LocalDate.now().getYear());
		MailRequest mailRequest = new MailRequest();
		mailRequest.setTo(request.getParentEmail());
		mailRequest.setSubject("Your Parent Portal Account Has Been Created");
		// emailService.sendEmailToParent(mailRequest,model);
		log.info("Student registered successfully: admissionNumber={}, userId={}");
	}

	private String generateAdmissionNumber() {
		return studentRepository.findLastAdmissionNumber().map(student -> {
			long lastNumber = Long.parseLong(student.getAdmissionNumber());
			return String.format("%06d", lastNumber + 1);
		}).orElse("000001");
	}

	// New Student Admission via admission form (all StudentFormRequest fields are
	// persisted)
	@Transactional
	public void studentAdmissionForm(StudentFormRequest request) {
		// 1. Validate class
		SchoolClass schoolClass = classRepository.findById(request.getClassId())
				.orElseThrow(() -> new ResourceNotFoundException("School class not found: " + request.getClassId()));

		// 2. Generate student email
		String username = Utils.getFirstName(request.getName());
		String studentEmail = generateEmail(username);
		// 3. Validate duplicates
		validateDuplicateUser(studentEmail, request.getFatherPhone());

		// 4. Generate passwords
		String studentPassword = Utils.generateRandomString(8);
		String parentRawPassword = Utils.generateRandomString(8);

		// 5. Generate admission number early so the fallback parent email stays unique
		String addNumber = generateAdmissionNumber();

		// 6. Create STUDENT user
		User studentUser = createUserWithPass(request.getName(), studentEmail, request.getFatherPhone(), Role.STUDENT,
				studentPassword);
		studentUser.setStatus(UserStatus.ACTIVE);
		studentUser = repository.save(studentUser);

		// 7. Create PARENT user (Student.parentUser is mandatory, so always create one)
		String parentEmail = StringUtils.hasText(request.getFatherEmail()) ? request.getFatherEmail().toLowerCase()
				: generateEmail("parent." + username + addNumber);
		String parentName = StringUtils.hasText(request.getFatherName()) ? request.getFatherName()
				: "Parent of " + request.getName();
		validateDuplicateUser(parentEmail, request.getFatherPhone());
		User parentUser = createUserWithPass(parentName, parentEmail, request.getFatherPhone(), Role.PARENT,
				parentRawPassword);
		parentUser.setStatus(UserStatus.ACTIVE);
		parentUser = repository.save(parentUser);

		// 8. Persist student with every admission-form field
		Student student = new Student();
		student.setDob(request.getDob());
		student.setGender(request.getGender());
		student.setBloodGroup(parseBloodGroup(request.getBloodGroup()));
		student.setAdharNumber(request.getAdharNumber());
		student.setCaste(request.getCast());
		student.setStudentImage(request.getPhoto());
		student.setFatherName(request.getFatherName());
		student.setFatherOccupation(request.getFatherOccupation());
		student.setMotherName(request.getMotherName());
		student.setMotherEmail(request.getMotherEmail());
		student.setMotherPhone(request.getMotherPhone());
		student.setMotherOccupation(request.getMotherOccupation());
		student.setNationality(request.getNationality());
		student.setParentAnnualIncome(request.getParentAnnualIncome());
		student.setAddress(request.getAddress());
		student.setPincode(request.getPincode());
		LocalDate now = LocalDate.now(ZoneId.of("Asia/Kolkata"));
		int startYear = now.getMonthValue() >= 4 ? now.getYear() : now.getYear() - 1;
		student.setAcademicYear(StringUtils.hasText(request.getSession()) ? request.getSession().trim()
				: (startYear + "-" + String.valueOf(startYear + 1).substring(2)));
		student.setAdmissionDate(now);
		student.setStudentUser(studentUser);
		student.setParentUser(parentUser);
		student.setAdmissionNumber(addNumber);
		student.setStudentCode("STUD-" + LocalDate.now().getYear() + "-" + addNumber);
		student.setSchoolClass(schoolClass);
		// DO NOT store raw password here
		student.setRawPassword(studentPassword);
		studentRepository.save(student);

		Map<String, Object> model = new HashMap<>();
		model.put("Name", parentName);
		model.put("studentName", request.getName());
		model.put("loginId", parentEmail);
		model.put("code", parentRawPassword);
		model.put("loginUrl", originUrl);
		model.put("currentYear", LocalDate.now().getYear());
		MailRequest mailRequest = new MailRequest();
		mailRequest.setTo(parentEmail);
		mailRequest.setSubject("Your Parent Portal Account Has Been Created");
		// emailService.sendEmailToParent(mailRequest,model);
		log.info("Student admission form registered successfully: admissionNumber={}, userId={}", addNumber,
				studentUser.getId());
	}

	// Accepts "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-" (also "A Positive",
	// "A_POSITIVE", "A +ve" style variants, case-insensitive).
	private BloodGroup parseBloodGroup(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		String v = value.trim().toUpperCase().replaceAll("[\\s_]+", "").replaceAll("([+-])VE$", "$1");
		String rh;
		if (v.endsWith("+") || v.endsWith("POSITIVE") || v.endsWith("POS")) {
			rh = "POSITIVE";
			v = v.replaceAll("(\\+|POSITIVE|POS)$", "");
		} else if (v.endsWith("-") || v.endsWith("NEGATIVE") || v.endsWith("NEG")) {
			rh = "NEGATIVE";
			v = v.replaceAll("(-|NEGATIVE|NEG)$", "");
		} else {
			throw new ServiceException(
					"Invalid blood group: " + value + ". Expected one of A+, A-, B+, B-, AB+, AB-, O+, O-.");
		}
		try {
			return BloodGroup.valueOf(v + "_" + rh);
		} catch (IllegalArgumentException ex) {
			throw new ServiceException(
					"Invalid blood group: " + value + ". Expected one of A+, A-, B+, B-, AB+, AB-, O+, O-.");
		}
	}

	public String generateEmail(String name) {
		School school = schoolRepository.findAll().stream().findFirst()
				.orElseThrow(() -> new IllegalStateException("School not found"));
		String normalizedName = name.toLowerCase().trim().replaceAll("\\s+", "");
		int randomNumber = java.util.concurrent.ThreadLocalRandom.current().nextInt(10, 100);
		return normalizedName + randomNumber + "@" + school.getEmailDomain();
	}

	// Delivery Boy Register
	@Transactional
	public void saveTeacherDetails(StaffUserRequest request, String email) {
		// 1. Validate duplicate user
		validateDuplicateUser(request.getEmail(), request.getPhoneNumber());
		String password = Utils.generateRandomString(8);
		User user = createUser(request.getName(), request.getEmail(), request.getPhoneNumber(), password,
				request.getRole());
		user.setStatus(UserStatus.ACTIVE);
		user = repository.save(user);
		if (request.getRole() == Role.TEACHER) {
			List<SchoolClass> schoolClasses = classRepository.findAllById(request.getClassIds());
			Subject subject = subjectRepository.findById(request.getSubjectId())
					.orElseThrow(() -> new ResourceNotFoundException("Subject details not found."));
			if (schoolClasses.size() != request.getClassIds().size()) {
				throw new ResourceNotFoundException("One or more classes not found.");
			}
			Teacher teacher = new Teacher();
			teacher.setTeacherUser(user);
			teacher.setQualification(request.getQualification());
			teacher.setExperienceYears(request.getExperienceYears());
			teacher.setSubject(subject);
			// Generate employee code
			teacher.setEmployeeCode(generateEmployeeCode());
			// Assign classes
			teacher.setAssignedClasses(new HashSet<>(schoolClasses));
			teacherRepository.save(teacher);
		}
		// Email
		Map<String, Object> model = new HashMap<>();
		model.put("Name", request.getName());
		MailRequest emailrequest = new MailRequest();
		emailrequest.setName(request.getName());
		emailrequest.setSubject("Confirmation Email");
		emailrequest.setTo(request.getEmail());
		emailrequest.setToken(null);
		// emailService.sendEmail(emailrequest, model);

	}

	private void validateDuplicateUser(String email, String phone) {
		Optional<User> checkUser = repository.findByPhoneOrEmail(email, phone);
		if (checkUser.isPresent()) {
			if (checkUser.get().getEmail().equals(email)) {
				throw new RuntimeException("Email already registered: " + email);
			}
//			if (checkUser.get().getPhone().equals(phone)) {
//				throw new RuntimeException("Phone already registered: " + phone);
//			}
		}
	}

	private String generateEmployeeCode() {
		return String.format("TEA" + Utils.generateCode());
	}

	private User createUser(String name, String email, String phone, String password, Role role) {
		User user = new User();
		user.setFullName(name);
		user.setEmail(email);
		user.setPhone(phone);
		user.setPassword(passwordEncoder.encode(password));
		user.setRole(role);
		return user;
	}

	private User createUserWithPass(String name, String email, String phone, Role role, String password) {
		User user = new User();
		user.setFullName(name.toUpperCase());
		user.setEmail(email);
		user.setPhone(phone);
		user.setPassword(passwordEncoder.encode(password));
		user.setRole(role);
		return user;
	}

	public UserAuthenticationResponse authenticate(AuthenticationRequest request) {
		log.info("Authenticating user: {}", request.getEmail());
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		var user = repository.findByEmail(request.getEmail()).orElseThrow();
		validateUserStatus(user);
		var jwtToken = jwtService.generateToken(user);
		// revokeAllUserTokens(user);
		// saveUserToken(user, jwtToken);
		return UserAuthenticationResponse.builder().accessToken(jwtToken)
				.data(mapper.map(user, LoggedInUserDetails.class)).build();

	}

	public UserAuthenticationResponse authenticateWithCode(AuthCodeRequest request) {
		log.info("Authenticating user with code for phone: {}", request.getPhone());
		var user = repository.findByPhoneAndOtp(request.getPhone(), request.getOpt())
				.orElseThrow(() -> new RuntimeException(
						"User Not Found By : " + request.getPhone() + " , OR : " + request.getOpt()));
		var jwtToken = jwtService.generateToken(user);
		revokeAllUserTokens(user);
		saveUserToken(user, jwtToken);
		return UserAuthenticationResponse.builder().accessToken(jwtToken)
				.data(mapper.map(user, LoggedInUserDetails.class)).build();
	}

	@Transactional
	public Map<String, Object> verificationUser(VerificationRequest request) {
		log.info("Verifying user with email: {}", request.getEmail());
		User checkUser = repository.findByEmail(request.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found By : " + request.getEmail()));
		if (!checkUser.getOtp().equals(request.getOtp())) {
			throw new ResourceNotFoundException("Invalid OTP provided for email: " + request.getEmail());
		}
		checkUser.setStatus(UserStatus.ACTIVE);
		checkUser.setOtp(null);
		return Map.of("statusCode", HttpStatus.OK.value(), "success", true, "message",
				"Account verified successfully. You can now log in.");
	}

	@Transactional
	public Map<String, Object> setPassword(PasswordRequest passwordRequest) {
		log.info("Setting new password for code: {}", passwordRequest.getCode());
		User ui = repository.findByEmail(passwordRequest.getCode())
				.orElseThrow(() -> new RuntimeException("Getting Error"));
		String password = passwordEncoder.encode(String.valueOf(passwordRequest.getConfirmPassword()));
		ui.setPassword(password);
		return Map.of("statusCode", HttpStatus.CREATED.value() + " ", "isSuccess", Boolean.TRUE, "message",
				"Please login");
	}

	@Transactional
	public void updatePassword(String email, ResetPasswordRequest passwordRequest) {
		log.info("Updating password for code: {}", passwordRequest);
		User ui = repository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Getting error"));
		if (!passwordEncoder.matches(passwordRequest.getOldPassword(), ui.getPassword())) {
			throw new ServiceException("Current password is incorrect");
		}
		String password = passwordEncoder.encode(String.valueOf(passwordRequest.getNewPassword()));
		ui.setPassword(password);
		repository.save(ui);
	}

	@Transactional
	public void forgetPassword(ForgotPasswordRequest forgetPassword) {
		log.info(">>forgetPassword({})", forgetPassword);
		User user = repository.findByEmail(forgetPassword.getEmail())
				.orElseThrow(() -> new RuntimeException("User Not Found By : " + forgetPassword.getEmail()));
		String token = Utils.generateDigitOTP(6);
		// TODO Send email to user with Reset Password Link
		Map<String, Object> model = new HashMap<>();
		model.put("Name", user.getUsername());
		MailRequest request = new MailRequest();
		request.setName(user.getFullName());
		request.setSubject("Set Your Password");
		request.setTo(forgetPassword.getEmail());
		request.setToken(token);
		emailService.sendEmailForget(request, model);
		repository.save(user);
	}

	@Transactional
	public UserResponse getUserById(String email) {
		log.info(">>getUserById({})", email);
		User user = repository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not Found By : " + email));
		return mapper.map(user, UserResponse.class);
	}

	private void saveUserToken(User user, String jwtToken) {
		var token = Token.builder().user(user).token(jwtToken).tokenType(TokenType.BEARER).expired(false).revoked(false)
				.build();
		tokenRepository.save(token);
	}

	private void revokeAllUserTokens(User user) {
		var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
		if (validUserTokens.isEmpty())
			return;
		validUserTokens.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});
		tokenRepository.saveAll(validUserTokens);
	}

	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userEmail;
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}
		refreshToken = authHeader.substring(7);
		userEmail = jwtService.extractUsername(refreshToken);
		if (userEmail != null) {
			var user = this.repository.findByEmail(userEmail).orElseThrow();
			if (jwtService.isTokenValid(refreshToken, user)) {
				var accessToken = jwtService.generateToken(user);
				revokeAllUserTokens(user);
				saveUserToken(user, accessToken);
				var authResponse = AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken)
						.build();
				new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
			}
		}
	}

	public String logout(String email) {
		log.info(">>logout({})", email);
		User user = repository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not Found By : " + email));
		user.getTokens().clear();
		return "Logout Successfully";
	}

	private void validateUserStatus(User user) {
		switch (user.getStatus()) {
		case ACTIVE -> {
			// Allowed to proceed
		}
		case PENDING_VERIFICATION -> throw new ServiceException(
				"Your account is pending verification. Please verify your email or phone number to continue.");
		case INACTIVE -> throw new ServiceException(
				"Your account has been deactivated by the administrator. Please contact support to regain access.");
		case SUSPENDED -> throw new ServiceException(
				"Your account has been temporarily suspended due to policy violations. Please contact support for assistance.");
		case BLOCKED -> throw new ServiceException(
				"Your account has been permanently blocked by the administrator. Please contact support for further clarification.");
		case DELETED -> throw new ServiceException(
				"This account is no longer available. Please contact support if you believe this is an error.");
		}
	}
	// Utility method to get logged in user details

}
