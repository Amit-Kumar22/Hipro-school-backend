package com.hipro.school.backend.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.hipro.school.backend.dtos.TeacherResponse;
import com.hipro.school.backend.dtos.dashboard.RecentNoticeResponse;
import com.hipro.school.backend.dtos.dashboard.TeacherAttendanceOverviewResponse;
import com.hipro.school.backend.dtos.dashboard.TeacherClassResponse;
import com.hipro.school.backend.dtos.dashboard.TeacherDashboardResponse;
import com.hipro.school.backend.dtos.dashboard.TeacherExamSummaryResponse;
import com.hipro.school.backend.dtos.dashboard.TeacherHomeworkSummaryResponse;
import com.hipro.school.backend.dtos.dashboard.TeacherProfileResponse;
import com.hipro.school.backend.dtos.dashboard.TeacherStudentOverviewResponse;
import com.hipro.school.backend.dtos.dashboard.TimetableEntryResponse;
import com.hipro.school.backend.entities.Homework;
import com.hipro.school.backend.entities.NoticeBoard;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.entities.Teacher;
import com.hipro.school.backend.entities.WeeklyTimetable;
import com.hipro.school.backend.enums.AttendanceStatus;
import com.hipro.school.backend.enums.ExamStatus;
import com.hipro.school.backend.enums.NoticeAudience;
import com.hipro.school.backend.enums.NoticeStatus;
import com.hipro.school.backend.repositories.AttendanceRepository;
import com.hipro.school.backend.repositories.ExamRepository;
import com.hipro.school.backend.repositories.HomeworkRepository;
import com.hipro.school.backend.repositories.NoticeBoardRepository;
import com.hipro.school.backend.repositories.StudentRepository;
import com.hipro.school.backend.repositories.TeacherRepository;
import com.hipro.school.backend.repositories.WeeklyTimetableRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherDashboardService {

	private final TeacherRepository teacherRepository;
	private final StudentRepository studentRepository;
	private final AttendanceRepository attendanceRepository;
	private final ExamRepository examRepository;
	private final HomeworkRepository homeworkRepository;
	private final NoticeBoardRepository noticeBoardRepository;
	private final WeeklyTimetableRepository weeklyTimetableRepository;

	public List<TeacherResponse> getAllTeacherBySubjectId(Long subjectId) {
		return teacherRepository.findAllBySubjectId(subjectId).stream().map(TeacherResponse::from).toList();
	}

	public TeacherDashboardResponse getDashboard(String userEmail) {
		log.info("Fetching dashboard for teacher: {}", userEmail);

		Teacher teacher = teacherRepository.findByTeacherUserEmail(userEmail)
				.orElseThrow(() -> new com.hipro.school.backend.exceptions.ResourceNotFoundException(
						"Teacher not found for email: " + userEmail));

		List<SchoolClass> assignedClasses = new ArrayList<>(teacher.getAssignedClasses());
		List<Long> classIds = assignedClasses.stream().map(SchoolClass::getId).toList();
		LocalDate today = LocalDate.now();

		return TeacherDashboardResponse.builder().profile(buildProfile(teacher, assignedClasses))
				.assignedClasses(buildClassList(teacher, assignedClasses))
				.studentOverview(buildStudentOverview(classIds))
				.attendanceOverview(buildAttendanceOverview(classIds, today)).examSummary(buildExamSummary(classIds))
				.todayTimetable(buildTodayTimetable(teacher, today)).recentNotices(buildRecentNotices())
				.homeworkSummary(buildHomeworkSummary(teacher.getId())).build();
	}

	private TeacherProfileResponse buildProfile(Teacher teacher, List<SchoolClass> assignedClasses) {
		List<String> classNames = assignedClasses.stream().map(SchoolClass::getClassName).toList();
		return TeacherProfileResponse.builder().teacherId(teacher.getId())
				.fullName(teacher.getTeacherUser().getFullName()).employeeCode(teacher.getEmployeeCode())
				.email(teacher.getTeacherUser().getEmail()).phone(teacher.getTeacherUser().getPhone())
				.primarySubject(teacher.getSubject() != null ? teacher.getSubject().getSubjectName() : null)
				.qualification(teacher.getQualification()).experienceYears(teacher.getExperienceYears())
				.assignedClassNames(classNames).build();
	}

	private List<TeacherClassResponse> buildClassList(Teacher teacher, List<SchoolClass> assignedClasses) {
		return assignedClasses.stream().map(sc -> {
			List<Student> students = studentRepository.findAllBySchoolClass_Id(sc.getId());
			boolean isClassTeacher = sc.getUser() != null
					&& sc.getUser().getId().equals(teacher.getTeacherUser().getId());
			return TeacherClassResponse.builder().classId(sc.getId()).className(sc.getClassName())
					.studentCount(students.size()).isClassTeacher(isClassTeacher).build();
		}).toList();
	}

	private TeacherStudentOverviewResponse buildStudentOverview(List<Long> classIds) {
		long totalStudents = 0;
		for (Long classId : classIds) {
			totalStudents += studentRepository.findAllBySchoolClass_Id(classId).size();
		}
		return TeacherStudentOverviewResponse.builder().totalStudents(totalStudents).activeStudents(totalStudents)
				.totalClasses(classIds.size()).build();
	}

	private TeacherAttendanceOverviewResponse buildAttendanceOverview(List<Long> classIds, LocalDate today) {
		long presentCount = 0;
		long absentCount = 0;
		long lateCount = 0;

		for (Long classId : classIds) {
			List<Student> students = studentRepository.findAllBySchoolClass_Id(classId);
			for (Student student : students) {
				Long userId = student.getStudentUser().getId();
				presentCount += attendanceRepository.countByUserAndDateRangeAndStatus(userId, today, today,
						AttendanceStatus.PRESENT);
				absentCount += attendanceRepository.countByUserAndDateRangeAndStatus(userId, today, today,
						AttendanceStatus.ABSENT);
				lateCount += attendanceRepository.countByUserAndDateRangeAndStatus(userId, today, today,
						AttendanceStatus.LATE);
			}
		}

		long totalRecords = presentCount + absentCount + lateCount;
		double attendancePercentage = 0;
		if (totalRecords > 0) {
			attendancePercentage = Math.round((double) presentCount * 1000 / totalRecords) / 10.0;
		}

		return TeacherAttendanceOverviewResponse.builder().date(today.toString()).presentCount(presentCount)
				.absentCount(absentCount).lateCount(lateCount).totalRecords(totalRecords)
				.attendancePercentage(attendancePercentage).build();
	}

	private TeacherExamSummaryResponse buildExamSummary(List<Long> classIds) {
		long upcoming = 0;
		long completed = 0;
		for (Long classId : classIds) {
			upcoming += examRepository.findBySchoolClassAndStatus(classId, ExamStatus.PUBLISHED).size();
			upcoming += examRepository.findBySchoolClassAndStatus(classId, ExamStatus.ACTIVE).size();
			completed += examRepository.findBySchoolClassAndStatus(classId, ExamStatus.COMPLETED).size();
		}
		return TeacherExamSummaryResponse.builder().upcomingExams(upcoming).completedExams(completed).pendingResults(0)
				.build();
	}

	private List<TimetableEntryResponse> buildTodayTimetable(Teacher teacher, LocalDate today) {
		DayOfWeek dayOfWeek = today.getDayOfWeek();
		List<TimetableEntryResponse> entries = new ArrayList<>();

		for (SchoolClass sc : teacher.getAssignedClasses()) {
			List<WeeklyTimetable> timetable = weeklyTimetableRepository
					.findAllByPeriod_SchoolClass_IdAndDayOfWeekOrderByPeriod_PeriodOrderAsc(sc.getId(), dayOfWeek);

			for (WeeklyTimetable t : timetable) {
				boolean isMyClass = t.getTeacher() != null && t.getTeacher().getId().equals(teacher.getId());
				if (isMyClass) {
					entries.add(TimetableEntryResponse.builder().periodName(t.getPeriod().getName())
							.subjectName(t.getSubject() != null ? t.getSubject().getSubjectName() : null)
							.teacherName(teacher.getTeacherUser().getFullName())
							.startTime(t.getPeriod().getStartTime() != null ? t.getPeriod().getStartTime().toString()
									: null)
							.endTime(t.getPeriod().getEndTime() != null ? t.getPeriod().getEndTime().toString() : null)
							.dayOfWeek(dayOfWeek.name()).breakPeriod(t.getPeriod().getBreakPeriod()).build());
				}
			}
		}

		return entries;
	}

	private List<RecentNoticeResponse> buildRecentNotices() {
		List<NoticeBoard> notices = noticeBoardRepository.findByAudienceInAndStatus(
				List.of(NoticeAudience.ALL, NoticeAudience.TEACHER), NoticeStatus.PUBLISHED, PageRequest.of(0, 5));

		return notices.stream()
				.map(n -> RecentNoticeResponse.builder().id(n.getId()).title(n.getTitle()).content(n.getContent())
						.audience(n.getAudience()).priority(n.getPriority()).status(n.getStatus())
						.authorName(n.getAuthor() != null ? n.getAuthor().getFullName() : "System")
						.publishDate(n.getPublishDate()).expiryDate(n.getExpiryDate()).build())
				.toList();
	}

	private TeacherHomeworkSummaryResponse buildHomeworkSummary(Long teacherId) {
		List<Homework> allHomework = homeworkRepository.findByTeacherIdOrderByHomeworkDateDesc(teacherId);
		LocalDate weekAgo = LocalDate.now().minusWeeks(1);
		long thisWeek = allHomework.stream()
				.filter(h -> h.getHomeworkDate() != null && h.getHomeworkDate().isAfter(weekAgo)).count();

		return TeacherHomeworkSummaryResponse.builder().totalHomework(allHomework.size()).thisWeekHomework(thisWeek)
				.build();
	}

}
