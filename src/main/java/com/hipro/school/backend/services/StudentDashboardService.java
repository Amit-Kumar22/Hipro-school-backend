package com.hipro.school.backend.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.hipro.school.backend.dtos.dashboard.AttendanceRecordResponse;
import com.hipro.school.backend.dtos.dashboard.ExamBriefResponse;
import com.hipro.school.backend.dtos.dashboard.ExamResultResponse;
import com.hipro.school.backend.dtos.dashboard.LeaveRecordResponse;
import com.hipro.school.backend.dtos.dashboard.RecentNoticeResponse;
import com.hipro.school.backend.dtos.dashboard.StudentAttendanceSummaryResponse;
import com.hipro.school.backend.dtos.dashboard.StudentDashboardResponse;
import com.hipro.school.backend.dtos.dashboard.StudentExamSummaryResponse;
import com.hipro.school.backend.dtos.dashboard.StudentFeeSummaryResponse;
import com.hipro.school.backend.dtos.dashboard.StudentLeaveSummaryResponse;
import com.hipro.school.backend.dtos.dashboard.StudentProfileResponse;
import com.hipro.school.backend.dtos.dashboard.TimetableEntryResponse;
import com.hipro.school.backend.entities.Attendance;
import com.hipro.school.backend.entities.Exam;
import com.hipro.school.backend.entities.ExamResult;
import com.hipro.school.backend.entities.NoticeBoard;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.entities.StudentFee;
import com.hipro.school.backend.entities.StudentLeave;
import com.hipro.school.backend.entities.WeeklyTimetable;
import com.hipro.school.backend.enums.AttendanceStatus;
import com.hipro.school.backend.enums.ExamStatus;
import com.hipro.school.backend.enums.FeeStatus;
import com.hipro.school.backend.enums.LeaveStatus;
import com.hipro.school.backend.enums.NoticeAudience;
import com.hipro.school.backend.enums.NoticeStatus;
import com.hipro.school.backend.repositories.AttendanceRepository;
import com.hipro.school.backend.repositories.ExamRepository;
import com.hipro.school.backend.repositories.ExamResultRepository;
import com.hipro.school.backend.repositories.NoticeBoardRepository;
import com.hipro.school.backend.repositories.PaymentRepository;
import com.hipro.school.backend.repositories.StudentFeeRepository;
import com.hipro.school.backend.repositories.StudentLeaveRepository;
import com.hipro.school.backend.repositories.StudentRepository;
import com.hipro.school.backend.repositories.WeeklyTimetableRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentDashboardService {

	private final StudentRepository studentRepository;
	private final AttendanceRepository attendanceRepository;
	private final StudentFeeRepository studentFeeRepository;
	private final ExamRepository examRepository;
	private final ExamResultRepository examResultRepository;
	private final NoticeBoardRepository noticeBoardRepository;
	private final WeeklyTimetableRepository weeklyTimetableRepository;
	private final StudentLeaveRepository studentLeaveRepository;

	public StudentDashboardResponse getDashboard(String userEmail) {
		log.info("Fetching dashboard for student: {}", userEmail);

		Student student = studentRepository.findByStudentUserEmail(userEmail)
				.orElseThrow(() -> new com.hipro.school.backend.exceptions.ResourceNotFoundException(
						"Student not found for email: " + userEmail));

		Long studentId = student.getId();
		Long classId = student.getSchoolClass().getId();
		LocalDate today = LocalDate.now();
		LocalDate monthStart = today.withDayOfMonth(1);
		LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

		return StudentDashboardResponse.builder().profile(buildProfile(student))
				.attendance(buildAttendanceSummary(studentId, today)).fees(buildFeeSummary(studentId))
				.exams(buildExamSummary(studentId, classId, today)).todayTimetable(buildTodayTimetable(classId, today))
				.recentNotices(buildRecentNotices()).leave(buildLeaveSummary(studentId))
				.recentResults(buildRecentResults(studentId)).build();
	}

	private StudentProfileResponse buildProfile(Student student) {
		return StudentProfileResponse.builder().studentId(student.getId())
				.fullName(student.getStudentUser().getFullName()).admissionNumber(student.getAdmissionNumber())
				.rollNumber(student.getRollNumber()).className(student.getSchoolClass().getClassName())
				.academicYear(student.getAcademicYear())
				.dateOfBirth(student.getDob() != null ? student.getDob().toString() : null)
				.gender(student.getGender() != null ? student.getGender().name() : null)
				.fatherName(student.getFatherName()).motherName(student.getMotherName())
				.parentEmail(student.getParentUser().getEmail()).studentEmail(student.getStudentUser().getEmail())
				.phone(student.getStudentUser().getPhone()).build();
	}

	private StudentAttendanceSummaryResponse buildAttendanceSummary(Long studentId, LocalDate today) {
		LocalDate monthStart = today.withDayOfMonth(1);
		LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

		long totalDays = attendanceRepository.countByUserAndDateRange(studentId, monthStart, monthEnd);
		long presentDays = attendanceRepository.countByUserAndDateRangeAndStatus(studentId, monthStart, monthEnd,
				AttendanceStatus.PRESENT);
		long absentDays = attendanceRepository.countByUserAndDateRangeAndStatus(studentId, monthStart, monthEnd,
				AttendanceStatus.ABSENT);
		long lateDays = attendanceRepository.countByUserAndDateRangeAndStatus(studentId, monthStart, monthEnd,
				AttendanceStatus.LATE);
		long halfDays = attendanceRepository.countByUserAndDateRangeAndStatus(studentId, monthStart, monthEnd,
				AttendanceStatus.HALF_DAY);

		double attendancePercentage = 0;
		if (totalDays > 0) {
			attendancePercentage = Math.round((double) presentDays * 1000 / totalDays) / 10.0;
		}

		List<Attendance> recentRecords = attendanceRepository
				.findByUserIdAndAttendanceDateBetween(studentId, today.minusDays(7), today);

		List<AttendanceRecordResponse> recentAttendance = new ArrayList<>();
		if (!recentRecords.isEmpty()) {
			recentAttendance = recentRecords.stream().limit(7)
					.map(a -> AttendanceRecordResponse.builder().date(a.getAttendanceDate().toString())
							.status(a.getStatus())
							.loginTime(a.getLoginTime() != null ? a.getLoginTime().toString() : null)
							.logoutTime(a.getLogoutTime() != null ? a.getLogoutTime().toString() : null)
							.totalWorkingMinutes(a.getTotalWorkingMinutes()).build())
					.toList();
		}

		return StudentAttendanceSummaryResponse.builder().totalDays(totalDays).presentDays(presentDays)
				.absentDays(absentDays).lateDays(lateDays).halfDays(halfDays).attendancePercentage(attendancePercentage)
				.recentAttendance(recentAttendance).build();
	}

	private StudentFeeSummaryResponse buildFeeSummary(Long studentId) {
		BigDecimal totalFees = studentFeeRepository.sumAmountByStudent(studentId);
		BigDecimal paidAmount = studentFeeRepository.sumPaidByStudent(studentId);
		BigDecimal pendingAmount = studentFeeRepository.sumDueByStudentAndStatuses(studentId,
				List.of(FeeStatus.PENDING, FeeStatus.PARTIAL, FeeStatus.OVERDUE));

		BigDecimal safeTotal = totalFees != null ? totalFees : BigDecimal.ZERO;
		BigDecimal safePaid = paidAmount != null ? paidAmount : BigDecimal.ZERO;
		BigDecimal safePending = pendingAmount != null ? pendingAmount : BigDecimal.ZERO;

		double collectionPercentage = 0;
		if (safeTotal.compareTo(BigDecimal.ZERO) > 0) {
			collectionPercentage = safePaid.multiply(BigDecimal.valueOf(100)).divide(safeTotal, 1, RoundingMode.HALF_UP)
					.doubleValue();
		}

		List<StudentFee> fees = studentFeeRepository.findByStudent_IdOrderByFeeDateDesc(studentId);
		long paidCount = fees.stream().filter(f -> f.getStatus() == FeeStatus.PAID).count();
		long pendingCount = fees.stream().filter(f -> f.getStatus() == FeeStatus.PENDING).count();
		long overdueCount = fees.stream().filter(f -> f.getStatus() == FeeStatus.OVERDUE).count();

		return StudentFeeSummaryResponse.builder().totalFees(safeTotal).paidAmount(safePaid).pendingAmount(safePending)
				.paidCount(paidCount).pendingCount(pendingCount).overdueCount(overdueCount)
				.collectionPercentage(collectionPercentage).build();
	}

	private StudentExamSummaryResponse buildExamSummary(Long studentId, Long classId, LocalDate today) {
		List<Exam> upcomingExams = examRepository.findBySchoolClassAndStatus(classId, ExamStatus.PUBLISHED);
		List<Exam> activeExams = examRepository.findBySchoolClassAndStatus(classId, ExamStatus.ACTIVE);
		List<Exam> allUpcoming = new ArrayList<>();
		allUpcoming.addAll(upcomingExams);
		allUpcoming.addAll(activeExams);

		List<ExamResult> results = examResultRepository.findByStudent_IdOrderByCreatedDesc(studentId);
		long passed = examResultRepository.countPassedByStudent(studentId);
		long total = examResultRepository.countByStudent(studentId);

		double averagePercentage = 0;
		if (!results.isEmpty()) {
			BigDecimal sum = results.stream().filter(r -> r.getPercentage() != null).map(ExamResult::getPercentage)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			long count = results.stream().filter(r -> r.getPercentage() != null).count();
			if (count > 0) {
				averagePercentage = sum.divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP).doubleValue();
			}
		}

		List<ExamBriefResponse> upcomingList = allUpcoming.stream().limit(5)
				.map(e -> ExamBriefResponse.builder().examId(e.getId()).title(e.getTitle())
						.examType(e.getExamType().name())
						.startDate(e.getStartDate() != null ? e.getStartDate().toString() : null)
						.endDate(e.getEndDate() != null ? e.getEndDate().toString() : null).status(e.getStatus().name())
						.className(e.getSchoolClass().getClassName()).build())
				.toList();

		return StudentExamSummaryResponse.builder().totalExams(total).passedExams(passed).failedExams(total - passed)
				.upcomingExams(allUpcoming.size()).averagePercentage(averagePercentage).upcomingExamList(upcomingList)
				.build();
	}

	private List<TimetableEntryResponse> buildTodayTimetable(Long classId, LocalDate today) {
		DayOfWeek dayOfWeek = today.getDayOfWeek();
		List<WeeklyTimetable> timetable = weeklyTimetableRepository
				.findAllByPeriod_SchoolClass_IdAndDayOfWeekOrderByPeriod_PeriodOrderAsc(classId, dayOfWeek);

		return timetable.stream().map(t -> TimetableEntryResponse.builder().periodName(t.getPeriod().getName())
				.subjectName(t.getSubject() != null ? t.getSubject().getSubjectName() : null)
				.teacherName(t.getTeacher() != null ? t.getTeacher().getTeacherUser().getFullName()
						: t.getOtherTeacherName())
				.startTime(t.getPeriod().getStartTime() != null ? t.getPeriod().getStartTime().toString() : null)
				.endTime(t.getPeriod().getEndTime() != null ? t.getPeriod().getEndTime().toString() : null)
				.dayOfWeek(dayOfWeek.name()).breakPeriod(t.getPeriod().getBreakPeriod()).build()).toList();
	}

	private List<RecentNoticeResponse> buildRecentNotices() {
		List<NoticeBoard> notices = noticeBoardRepository.findByAudienceInAndStatus(
				List.of(NoticeAudience.ALL, NoticeAudience.STUDENT), NoticeStatus.PUBLISHED, PageRequest.of(0, 5));

		return notices.stream()
				.map(n -> RecentNoticeResponse.builder().id(n.getId()).title(n.getTitle()).content(n.getContent())
						.audience(n.getAudience()).priority(n.getPriority()).status(n.getStatus())
						.authorName(n.getAuthor() != null ? n.getAuthor().getFullName() : "System")
						.publishDate(n.getPublishDate()).expiryDate(n.getExpiryDate()).build())
				.toList();
	}

	private StudentLeaveSummaryResponse buildLeaveSummary(Long studentId) {
		int approvedDays = studentLeaveRepository.sumDaysByStudentAndStatus(studentId, LeaveStatus.APPROVED);
		long pendingCount = studentLeaveRepository.countByStudentAndStatus(studentId, LeaveStatus.PENDING);
		long rejectedCount = studentLeaveRepository.countByStudentAndStatus(studentId, LeaveStatus.REJECTED);

		List<StudentLeave> recentLeaves = studentLeaveRepository.findByStudent_IdOrderByFromDateDesc(studentId);
		List<LeaveRecordResponse> leaveRecords = recentLeaves.stream().limit(5)
				.map(l -> LeaveRecordResponse.builder().leaveId(l.getId()).leaveType(l.getLeaveType())
						.status(l.getStatus()).fromDate(l.getFromDate().toString()).toDate(l.getToDate().toString())
						.numberOfDays(l.getNumberOfDays()).reason(l.getReason()).build())
				.toList();

		return StudentLeaveSummaryResponse.builder().approvedDays(approvedDays).pendingCount(pendingCount)
				.rejectedCount(rejectedCount).recentLeaves(leaveRecords).build();
	}

	private List<ExamResultResponse> buildRecentResults(Long studentId) {
		List<ExamResult> results = examResultRepository.findByStudent_IdOrderByCreatedDesc(studentId);
		return results.stream().limit(5)
				.map(r -> ExamResultResponse.builder().resultId(r.getId())
						.examTitle(r.getExam() != null ? r.getExam().getTitle() : null)
						.subjectName(r.getExamSubject() != null && r.getExamSubject().getSubject() != null
								? r.getExamSubject().getSubject().getSubjectName()
								: null)
						.totalMarks(r.getTotalMarks()).obtainedMarks(r.getObtainedMarks()).percentage(r.getPercentage())
						.grade(r.getGrade()).passed(r.getPassed()).remarks(r.getRemarks()).build())
				.toList();
	}
}
