package com.hipro.school.backend.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.dashboard.AttendanceOverviewResponse;
import com.hipro.school.backend.dtos.dashboard.ClassStatsProjection;
import com.hipro.school.backend.dtos.dashboard.ClassStatsResponse;
import com.hipro.school.backend.dtos.dashboard.FeeOverviewResponse;
import com.hipro.school.backend.dtos.dashboard.RecentNoticeResponse;
import com.hipro.school.backend.dtos.dashboard.RevenueTrendResponse;
import com.hipro.school.backend.dtos.dashboard.SummaryResponse;
import com.hipro.school.backend.enums.AttendanceStatus;
import com.hipro.school.backend.enums.FeeStatus;
import com.hipro.school.backend.enums.NoticeStatus;
import com.hipro.school.backend.enums.UserStatus;
import com.hipro.school.backend.repositories.AttendanceRepository;
import com.hipro.school.backend.repositories.ExamRepository;
import com.hipro.school.backend.repositories.NoticeBoardRepository;
import com.hipro.school.backend.repositories.PaymentRepository;
import com.hipro.school.backend.repositories.SchoolClassRepository;
import com.hipro.school.backend.repositories.StudentFeeRepository;
import com.hipro.school.backend.repositories.StudentRepository;
import com.hipro.school.backend.repositories.SubjectRepository;
import com.hipro.school.backend.repositories.TeacherRepository;
import com.hipro.school.backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminDashboardService {

	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final SchoolClassRepository schoolClassRepository;
	private final SubjectRepository subjectRepository;
	private final UserRepository userRepository;
	private final AttendanceRepository attendanceRepository;
	private final StudentFeeRepository studentFeeRepository;
	private final PaymentRepository paymentRepository;
	private final ExamRepository examRepository;
	private final NoticeBoardRepository noticeBoardRepository;

	public SummaryResponse getSummary() {
		log.info("Fetching dashboard summary");
		return SummaryResponse.builder().totalStudents(studentRepository.count())
				.totalTeachers(teacherRepository.count()).totalClasses(schoolClassRepository.count())
				.totalSubjects(subjectRepository.count())
				.totalActiveUsers(userRepository.countByStatus(UserStatus.ACTIVE))
				.totalNotices(noticeBoardRepository.count()).totalExams(examRepository.count()).build();
	}

	public FeeOverviewResponse getFeeOverview() {
		log.info("Fetching fee overview");

		// BEFORE: 8 separate queries (2 SUM + 2 SUM-by-status + 4 COUNT-by-status)
		// AFTER: 2 queries (1 aggregated SUM + 1 GROUP BY status)

		Object[] sums = studentFeeRepository.sumAmountsAggregated();
		BigDecimal totalFeeAmount = sums[0] != null ? (BigDecimal) sums[0] : BigDecimal.ZERO;
		BigDecimal totalCollectedAmount = sums[1] != null ? (BigDecimal) sums[1] : BigDecimal.ZERO;
		BigDecimal totalPendingAmount = sums[2] != null ? (BigDecimal) sums[2] : BigDecimal.ZERO;

		List<Object[]> statusCounts = studentFeeRepository.countGroupByStatus();
		Map<FeeStatus, Long> countMap = new HashMap<>();
		for (Object[] row : statusCounts) {
			countMap.put((FeeStatus) row[0], (Long) row[1]);
		}

		BigDecimal totalOverdueAmount = studentFeeRepository.sumDueAmountsByStatuses(List.of(FeeStatus.OVERDUE));

		double collectionPercentage = 0;
		if (totalFeeAmount.compareTo(BigDecimal.ZERO) > 0) {
			collectionPercentage = totalCollectedAmount.multiply(BigDecimal.valueOf(100))
					.divide(totalFeeAmount, 1, RoundingMode.HALF_UP).doubleValue();
		}

		return FeeOverviewResponse.builder()
				.totalFeeAmount(totalFeeAmount)
				.totalCollectedAmount(totalCollectedAmount)
				.totalPendingAmount(totalPendingAmount)
				.totalOverdueAmount(totalOverdueAmount != null ? totalOverdueAmount : BigDecimal.ZERO)
				.paidCount(countMap.getOrDefault(FeeStatus.PAID, 0L))
				.pendingCount(countMap.getOrDefault(FeeStatus.PENDING, 0L))
				.partialCount(countMap.getOrDefault(FeeStatus.PARTIAL, 0L))
				.overdueCount(countMap.getOrDefault(FeeStatus.OVERDUE, 0L))
				.collectionPercentage(collectionPercentage)
				.build();
	}

	public AttendanceOverviewResponse getAttendanceOverview() {
		log.info("Fetching attendance overview for today");

		// BEFORE: 5 separate queries (4 COUNT-by-status + 1 COUNT-total)
		// AFTER: 1 GROUP BY query

		LocalDate today = LocalDate.now();
		List<Object[]> statusCounts = attendanceRepository.countGroupByStatusForDate(today);

		long presentCount = 0;
		long absentCount = 0;
		long halfDayCount = 0;
		long lateCount = 0;
		long totalRecords = 0;

		for (Object[] row : statusCounts) {
			AttendanceStatus status = (AttendanceStatus) row[0];
			long count = (Long) row[1];
			totalRecords += count;
			switch (status) {
				case PRESENT -> presentCount = count;
				case ABSENT -> absentCount = count;
				case HALF_DAY -> halfDayCount = count;
				case LATE -> lateCount = count;
				default -> { /* other statuses contribute to total but not specific counts */ }
			}
		}

		double attendancePercentage = 0;
		if (totalRecords > 0) {
			attendancePercentage = (double) presentCount * 100 / totalRecords;
		}

		return AttendanceOverviewResponse.builder().date(today.toString()).presentCount(presentCount)
				.absentCount(absentCount).halfDayCount(halfDayCount).lateCount(lateCount).totalRecords(totalRecords)
				.attendancePercentage(Math.round(attendancePercentage * 10.0) / 10.0).build();
	}

	public List<RecentNoticeResponse> getRecentNotices(int limit) {
		log.info("Fetching {} recent notices", limit);

		List<com.hipro.school.backend.entities.NoticeBoard> notices = noticeBoardRepository
				.findTopByStatus(NoticeStatus.PUBLISHED, PageRequest.of(0, limit));

		return notices.stream().map(this::mapToNoticeResponse).toList();
	}

	public List<ClassStatsResponse> getClassStats() {
		log.info("Fetching class-wise statistics");

		// BEFORE: N+1 problem — findAll() + N queries for student counts
		// AFTER: 1 GROUP BY query with projection

		List<ClassStatsProjection> projections = schoolClassRepository.findClassStatsWithStudentCount();

		return projections.stream().map(p -> ClassStatsResponse.builder()
				.classId(p.getClassId())
				.className(p.getClassName())
				.studentCount(p.getStudentCount())
				.teacherCount(0)
				.feeStructureCount(0)
				.build()).toList();
	}

	public List<RevenueTrendResponse> getRevenueTrend(int months) {
		log.info("Fetching revenue trend for last {} months", months);

		// BEFORE: N queries (1 per month loop)
		// AFTER: 1 GROUP BY year/month query

		YearMonth currentMonth = YearMonth.now();
		LocalDateTime startDate = currentMonth.minusMonths(months - 1).atDay(1).atStartOfDay();

		List<Object[]> dbResults = paymentRepository.sumRevenueGroupByMonth(startDate);

		Map<String, RevenueTrendResponse> trendMap = new HashMap<>();
		for (int i = months - 1; i >= 0; i--) {
			YearMonth month = currentMonth.minusMonths(i);
			String key = month.getYear() + "-" + month.getMonthValue();
			trendMap.put(key, RevenueTrendResponse.builder()
					.month(month.getMonthValue())
					.year(month.getYear())
					.monthName(month.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH))
					.revenue(BigDecimal.ZERO)
					.paymentCount(0)
					.build());
		}

		for (Object[] row : dbResults) {
			int year = ((Number) row[0]).intValue();
			int month = ((Number) row[1]).intValue();
			BigDecimal revenue = row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO;
			long paymentCount = row[3] != null ? (Long) row[3] : 0L;

			String key = year + "-" + month;
			RevenueTrendResponse existing = trendMap.get(key);
			if (existing != null) {
				existing.setRevenue(revenue);
				existing.setPaymentCount(paymentCount);
			}
		}

		return new ArrayList<>(trendMap.values());
	}

	private RecentNoticeResponse mapToNoticeResponse(com.hipro.school.backend.entities.NoticeBoard notice) {
		return RecentNoticeResponse.builder().id(notice.getId()).title(notice.getTitle()).content(notice.getContent())
				.audience(notice.getAudience()).priority(notice.getPriority()).status(notice.getStatus())
				.authorName(notice.getAuthor() != null ? notice.getAuthor().getFullName() : "System")
				.publishDate(notice.getPublishDate()).expiryDate(notice.getExpiryDate()).build();
	}
}
