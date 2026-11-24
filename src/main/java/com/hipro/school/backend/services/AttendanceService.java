package com.hipro.school.backend.services;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.AttendanceVerifyResponse;
import com.hipro.school.backend.dtos.MarkAttendanceRequest;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.UserResponse;
import com.hipro.school.backend.entities.Attendance;
import com.hipro.school.backend.entities.Holiday;
import com.hipro.school.backend.entities.Role;
import com.hipro.school.backend.entities.School;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.enums.AttendanceSource;
import com.hipro.school.backend.enums.AttendanceStatus;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.AttendanceRepository;
import com.hipro.school.backend.repositories.SchoolClassRepository;
import com.hipro.school.backend.repositories.SchoolRepository;
import com.hipro.school.backend.repositories.StudentRepository;
import com.hipro.school.backend.repositories.UserRepository;
import com.hipro.school.backend.util.DateTimeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class AttendanceService {

	@Autowired
	private AttendanceRepository attendanceRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private SchoolClassRepository classRepository;

	@Autowired
	private StudentRepository studentRepository;

	public AttendanceVerifyResponse studentCheckIn(Long studentUserId, MarkAttendanceRequest request, String email) {
		User studentUser = getUserById(studentUserId);
		return CheckIn(request, studentUser.getEmail(), email);
	}

	public AttendanceVerifyResponse teacherCheckIn(MarkAttendanceRequest request, String email) {
		return CheckIn(request, email, null);
	}

	public AttendanceVerifyResponse CheckIn(MarkAttendanceRequest request, String email, String updatedby) {
		log.info("Check-in request for user: {}", email);
		LocalDateTime currentDateTime = DateTimeUtils.todayDateTime();
		LocalDate today = currentDateTime.toLocalDate();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

		AttendanceVerifyResponse response = new AttendanceVerifyResponse();
		response.setCheckInOutTime(currentDateTime.format(formatter));

		// 1. Get User
		User user = getUser(email);

		// Find School Detail
		School school = schoolRepository.findAll().stream().findFirst()
				.orElseThrow(() -> new RuntimeException("School not found"));

		// 3. Holiday Check
		Optional<Holiday> holidayOpt = holidayService.checkHoliday(today);
		if (holidayOpt.isPresent()) {
			response.setAllowed(false);
			response.setMessage("Today is holiday: " + holidayOpt.get().getHolidayName());
			return response;
		}

		// 4. Weekend Check
		/*
		 * if (isWeekend(today)) { response.setAllowed(false);
		 * response.setMessage("Today is weekend. Attendance not required."); return
		 * response; }
		 */

		// 5. Get today's attendance
		Attendance attendance = markAttendance(user, today, updatedby);

		if (attendance.getLoginTime() != null && attendance.getStatus() == AttendanceStatus.PRESENT) {
			response.setAllowed(false);
			response.setCheckInOutTime(attendance.getLoginTime().format(formatter));
			response.setDistance(attendance.getCheckIndistanceFromOffice());
			response.setMessage("You have already checked in today.");
			return response;
		}

		attendance.setAttendanceSource(request.getAttendanceSource());

		Boolean allowed = null;
		String message = "";

		// 6. GPS Attendance
		if (request.getAttendanceSource() == AttendanceSource.GPS) {
			AttendanceVerifyResponse locationResponse = validateLocation(request, school);
			attendance.setCheckinScanLatitude(request.getLatitude());
			attendance.setCheckinScanLongitude(request.getLongitude());
			attendance.setCheckIndistanceFromOffice(locationResponse.getDistance());
			attendance.setRemarks(locationResponse.getMessage());
			allowed = locationResponse.getAllowed();
			message = locationResponse.getMessage();
			if (Boolean.TRUE.equals(allowed)) {
				if (AttendanceStatus.LOGOUT.equals(attendance.getStatus())) {
					attendance.setTotalWorkingMinutes(null);
					attendance.setCheckoutScanLongitude(null);
					attendance.setCheckoutScanLatitude(null);
					attendance.setCheckOutdistanceFromOffice(null);
					attendance.setLogoutTime(null);
				} else {
					attendance.setLoginTime(currentDateTime);
				}
				attendance.setStatus(AttendanceStatus.PRESENT);
			} else if (Boolean.FALSE.equals(allowed) && attendance.getStatus() == null
					|| !AttendanceStatus.LOGOUT.equals(attendance.getStatus())) {
				attendance.setLoginTime(null);
				attendance.setStatus(AttendanceStatus.ABSENT);
			}
		}

		// 7. QR Attendance
		else if (request.getAttendanceSource() == AttendanceSource.QR_CODE) {

			if (school.getQrCodeValue() == null) {
				throw new IllegalStateException("Office QR code not configured");
			}

			attendance.setLoginTime(currentDateTime);
			attendance.setAttendanceQRCode(request.getQrCodeValue());

			if (school.getQrCodeValue().equals(request.getQrCodeValue())) {
				attendance.setStatus(AttendanceStatus.PRESENT);
				allowed = true;
				message = "Check-in successful via QR code.";
			} else {
				attendance.setStatus(AttendanceStatus.ABSENT);
				allowed = false;
				message = "Invalid QR code.";
			}
		}

		// Invalid Source
		else {
			attendance.setStatus(AttendanceStatus.ABSENT);
			allowed = false;
			message = "Invalid attendance source.";
		}

		attendanceRepository.save(attendance);
		response.setAllowed(allowed);
		response.setMessage(message);
		response.setDistance(attendance.getCheckIndistanceFromOffice());
		return response;
	}

	public AttendanceVerifyResponse checkOut(MarkAttendanceRequest request, String email) {
		LocalDateTime currentDateTime = DateTimeUtils.todayDateTime();
		LocalDate today = currentDateTime.toLocalDate();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

		AttendanceVerifyResponse res = new AttendanceVerifyResponse();
		res.setCheckInOutTime(currentDateTime.format(formatter));

		// Finding User
		User user = getUser(email);
		Attendance attendance = attendanceRepository.findByUserIdAndAttendanceDate(user.getId(), today)
				.orElseThrow(() -> new ResourceNotFoundException("Attendance not found."));

		School school = schoolRepository.findAll().stream().findFirst()
				.orElseThrow(() -> new RuntimeException("School not found"));

		Boolean isAllowed = false;
		String distance = null;
		String message = null;

		switch (request.getAttendanceSource()) {
		case GPS -> {
			AttendanceVerifyResponse locationRes = validateLocation(request, school);
			distance = locationRes.getDistance();
			isAllowed = locationRes.getAllowed();

			if (isAllowed) {
				message = "You have successfully checked out for today.";
				attendance.setCheckoutScanLatitude(request.getLatitude());
				attendance.setCheckoutScanLongitude(request.getLongitude());
				attendance.setCheckOutdistanceFromOffice(distance);
				attendance.setRemarks(message);
			} else {
				return buildFailureResponse(res, distance, "You are outside the office check-out range.");
			}
		}
		case QR_CODE -> {
			if (!school.getQrCodeValue().equals(request.getQrCodeValue())) {
				return buildFailureResponse(res, null, "Invalid QR code.");
			}
			attendance.setCheckoutScanLatitude(null);
			attendance.setCheckoutScanLongitude(null);
			isAllowed = true;
		}
		default -> {
			return buildFailureResponse(res, null, "Invalid attendance source.");
		}
		}
		long totalMinutes = Duration.between(attendance.getLoginTime(), currentDateTime).toMinutes();
		attendance.setLogoutTime(currentDateTime);
		attendance.setRemarks(message);
		attendance.setTotalWorkingMinutes(totalMinutes);
		attendance.setStatus(AttendanceStatus.LOGOUT);
		attendanceRepository.save(attendance);
		res.setDistance(distance);
		res.setAllowed(true);
		res.setMessage(message);
		return res;
	}

	private AttendanceVerifyResponse buildFailureResponse(AttendanceVerifyResponse res, String distance,
			String message) {
		res.setAllowed(false);
		res.setDistance(distance);
		res.setMessage(message);
		return res;
	}

	public AttendanceVerifyResponse checkOutOld(MarkAttendanceRequest request, String email) {
		LocalDateTime currentDateTime = DateTimeUtils.todayDateTime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
		String formattedTime = currentDateTime.format(formatter);

		// Response
		AttendanceVerifyResponse res = new AttendanceVerifyResponse();
		res.setCheckInOutTime(formattedTime);
		User user = getUser(email);
		LocalDate today = DateTimeUtils.todayDate();
		Attendance attendance = attendanceRepository.findByUserIdAndAttendanceDate(user.getId(), today)
				.orElseThrow(() -> new ResourceNotFoundException("Attendance not found."));

		School school = schoolRepository.findAll().stream().findFirst()
				.orElseThrow(() -> new RuntimeException("School not found"));

		if (request.getAttendanceSource() == AttendanceSource.GPS) {
			attendance.setCheckoutScanLatitude(request.getLatitude());
			attendance.setCheckoutScanLongitude(request.getLongitude());

			AttendanceVerifyResponse locationRes = validateLocation(request, school);
			res.setDistance(locationRes.getDistance());
			res.setAllowed(locationRes.getAllowed());
			if (!locationRes.getAllowed()) {
				res.setMessage("You are outside the office check-out range.");
				return res;
			}
			attendance.setCheckOutdistanceFromOffice(res.getDistance());

		} else if (request.getAttendanceSource() == AttendanceSource.QR_CODE) {
			if (!school.getQrCodeValue().equals(request.getQrCodeValue())) {
				res.setAllowed(false);
				res.setMessage("Invalid QR code.");
				return res;
			}
			attendance.setCheckoutScanLatitude(null);
			attendance.setCheckoutScanLongitude(null);
		} else {
			res.setAllowed(false);
			res.setMessage("Invalid attendance source.");
			return res;
		}
		long totalMinutes = Duration.between(attendance.getLoginTime(), currentDateTime).toMinutes();
		attendance.setTotalWorkingMinutes(totalMinutes);
		attendance.setLogoutTime(currentDateTime);
		attendance.setStatus(AttendanceStatus.LOGOUT);

		res.setAllowed(true);
		res.setMessage("You have successfully checked out for today.");
		return res;
	}

	@Transactional
	public void markAbsentByOffice() {
		LocalDate today = DateTimeUtils.todayDate();
		log.info("Starting absent scheduler for date {}", today);
		List<Attendance> attendanceBatch = new ArrayList<>();
		List<User> employees = userRepository.findAllByRole(Role.TEACHER);
		// MasterHoliday check (per office)
		Optional<Holiday> holidayOpt = holidayService.checkHoliday(today);
		boolean isWeekend = isWeekend(today);
		for (User user : employees) {
			boolean alreadyMarked = attendanceRepository.existsByUserAndAttendanceDate(user, today);
			if (alreadyMarked) {
				continue;
			}
			Attendance attendance = new Attendance();
			attendance.setUser(user);
			attendance.setName(user.getFullName());
			attendance.setAttendanceDate(today);
			attendance.setAttendanceSource(null);
			if (holidayOpt.isPresent()) {
				attendance.setStatus(AttendanceStatus.HOLIDAY);
				attendance.setRemarks("MasterHoliday: " + holidayOpt.get().getHolidayName());
			} else if (isWeekend) {
				attendance.setStatus(AttendanceStatus.WEEKEND);
				attendance.setRemarks("Weekend (Office Closed)");
			} else {
				attendance.setStatus(AttendanceStatus.ABSENT);
				attendance.setRemarks("System Marked absent as no check-in was recorded today");
			}
			attendanceBatch.add(attendance);
		}
		if (!attendanceBatch.isEmpty()) {
			attendanceRepository.saveAll(attendanceBatch);
			log.info("Attendance scheduler processed {} records", attendanceBatch.size());
		} else {
			log.info("No attendance updates required");
		}
		log.info("Attendance scheduler completed");
	}

	private boolean isWeekend(LocalDate date) {
		DayOfWeek day = date.getDayOfWeek();
		// return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
		return day == DayOfWeek.SUNDAY;
	}

	private AttendanceVerifyResponse validateLocation(MarkAttendanceRequest request, School school) {
		AttendanceVerifyResponse response = new AttendanceVerifyResponse();
		double companyLat = school.getLatitude();
		double companyLon = school.getLongitude();

		double userLat = request.getLatitude();
		double userLon = request.getLongitude();

		double allowedRadius = school.getAllowedRadiusMeters();
		if (allowedRadius == 0) {
			allowedRadius = 30;
		}
		double distance = calculateDistance(userLat, userLon, companyLat, companyLon);
		distance = Math.round(distance * 100.0) / 100.0;
		if (distance > allowedRadius) {
			response.setAllowed(false);
			response.setMessage("You are outside the allowed location radius");
			response.setDistance(distance + " Meters");
			return response;
		}
		response.setAllowed(true);
		response.setMessage("Check-in recorded successfully. Have a great day!");
		response.setDistance(distance + " Meters");
		return response;
	}

	public PageResponse<Attendance> getAllAttendenceForStaff(Pageable pageable, String email, LocalDate startDate,
			LocalDate endDate) {
		User user = getUser(email);
		Page<Attendance> page = attendanceRepository.findAttendanceByStaff(user, startDate, endDate, pageable);
		return PageResponse.from(page);
	}

	public PageResponse<Attendance> getAllAttendenceByUser(Pageable pageable, Long userId, LocalDate startDate,
			LocalDate endDate) {
		User user = getUserById(userId);
		Page<Attendance> page = attendanceRepository.findAttendanceByStaff(user, startDate, endDate, pageable);
		return PageResponse.from(page);
	}

	public PageResponse<Attendance> getTodayAttendenceForAdmin(Pageable pageable, String email, String branchCode) {
		ZoneId indiaZone = ZoneId.of("Asia/Kolkata");
		LocalDate today = LocalDate.now(indiaZone);
		Page<Attendance> page = attendanceRepository.findTodayAttendanceForAdmin(today, pageable);
		return PageResponse.from(page);
	}

	public PageResponse<Attendance> getAllEmployeeAttendence(Pageable pageable, String email, String branchCode,
			String empCode, LocalDate startDate, LocalDate endDate) {
		User user = getUser(email);
		if (Role.ADMIN.equals(user.getRole()) || Role.SUPERADMIN.equals(user.getRole())) {
			Page<Attendance> page = attendanceRepository.findAttendance(user.getId(), startDate, endDate, pageable);
			return PageResponse.from(page);
		}
		return null;
	}

	public User getUser(String email) {
		log.info("Fetching getUser by email: {}", email);
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + email));
	}

	public User getUserById(Long userId) {
		log.info("Fetching getUser by email: {}", userId);
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
	}

	public Attendance markAttendance(User user, LocalDate today, String updateBy) {
		log.info("Marking attendance for user: {}", user);
		Optional<Attendance> existingAttendance = attendanceRepository.findByUserIdAndAttendanceDate(user.getId(),
				today);
		if (existingAttendance.isPresent()) {
			return existingAttendance.get();
		}
		Attendance attendance = new Attendance();
		attendance.setUser(user);
		attendance.setName(user.getFullName());
		attendance.setAttendanceDate(today);
		attendance.setActive(true);
		attendance.setUpdateBy(updateBy);
		Attendance savedAttendance = attendanceRepository.save(attendance);
		log.info("New attendance record created for user {} on {}", user.getEmail(), today);
		return savedAttendance;
	}

	private double calculateDistance(double userLat, double userlong, double CompLat, double CompLon) {
		final int R = 6371000; // Earth radius in meters
		double latDistance = Math.toRadians(CompLat - userLat);
		double lonDistance = Math.toRadians(CompLon - userlong);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(userLat))
				* Math.cos(Math.toRadians(CompLat)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return R * c;
	}

	public Attendance getById(Long id) {
		return attendanceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Attendance not found :: " + id));
	}

	public Attendance getTodayByEmployeeAttendence(String email) {
		log.info("Fetching today's attendance for user: {}", email);
		LocalDate today = DateTimeUtils.todayDate();
		LocalDateTime currentDateTime = DateTimeUtils.todayDateTime();
		log.info("=*=*=* timezone: {}", ZoneId.systemDefault());
		User user = getUser(email);
		return attendanceRepository.findByUserIdAndAttendanceDate(user.getId(), today).orElseThrow(
				() -> new ResourceNotFoundException("Attendance not found for user: " + email + " on date: " + today));
	}

	public Attendance getTodayByStudentAttendence(Long studentUserId) {
		log.info("Fetching today's attendance for user: {}", studentUserId);
		LocalDate today = DateTimeUtils.todayDate();
		log.info("=*=*=* timezone: {}", ZoneId.systemDefault());
		User user = getUserById(studentUserId);
		return attendanceRepository.findByUserIdAndAttendanceDate(user.getId(), today)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Attendance not found for user: " + studentUserId + " on date: " + today));
	}

	@Transactional(readOnly = true)
	public PageResponse<UserResponse> getAllStudentByClassTeacher(String email, Pageable pageable) {
		log.info("Fetching students for class teacher: {}", email);
		User teacherUser = getUser(email);
		SchoolClass schoolClass = classRepository.findByClassTeacherId(teacherUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Class detail not found."));

		Page<Student> students = studentRepository.findAllBySchoolClass_Id(schoolClass.getId(), pageable);
		Page<UserResponse> responsePage = students.map(this::mapStudentToUserResponse);
		return PageResponse.from(responsePage);
	}

	public List<Attendance> getByEmployee(String email) {
		User user = getUser(email);
		return attendanceRepository.findAllByUserOrderByAttendanceDateDesc(user);
	}

	public List<Attendance> getByDate(String date) {
		LocalDate localDate = LocalDate.parse(date);
		return attendanceRepository.findAllByAttendanceDate(localDate);
	}

	public Map<String, Object> getMonthlySummary(String email, int month, int year) {
		User user = getUser(email);
		LocalDate start = LocalDate.of(year, month, 1);
		LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
		List<Attendance> records = attendanceRepository.findByUserIdAndAttendanceDateBetween(user.getId(), start, end);
		long totalMinutes = records.stream().filter(r -> r.getTotalWorkingMinutes() != null)
				.mapToLong(Attendance::getTotalWorkingMinutes).sum();
		Map<String, Object> summary = new HashMap<>();
		summary.put("employeeName", user.getFullName());
		summary.put("month", month);
		summary.put("year", year);
		summary.put("totalDaysPresent", records.size());
		summary.put("totalWorkingHours", totalMinutes / 60);
		summary.put("details", records);
		return summary;
	}

	private UserResponse mapStudentToUserResponse(Student student) {
		User user = student.getStudentUser();
		UserResponse response = new UserResponse();
		response.setId(user.getId());
		response.setFullName(user.getFullName());
		response.setEmail(user.getEmail());
		response.setPhone(user.getPhone());
		response.setRole(user.getRole());
		return response;
	}

}
