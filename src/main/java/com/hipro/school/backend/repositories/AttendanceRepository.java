package com.hipro.school.backend.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.Attendance;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.enums.AttendanceStatus;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

	@Query("""
			SELECT a FROM Attendance a	WHERE a.user.id = :userId AND a.attendanceDate = :date
			""")
	Optional<Attendance> findByUserIdAndAttendanceDate(@Param("userId") Long userId, @Param("date") LocalDate date);

	List<Attendance> findAllByUserOrderByAttendanceDateDesc(User user);

	List<Attendance> findAllByAttendanceDate(LocalDate date);
	
	List<Attendance> findByUserAndAttendanceDateBetween(User userId, LocalDate start, LocalDate end);

	List<Attendance> findByUserIdAndAttendanceDateBetween(Long userId, LocalDate start, LocalDate end);

	Page<Attendance> findAllByUser(User user, Pageable pageable);

	@Query("""
			SELECT a
			FROM Attendance a
			WHERE
			 (:empCode IS NULL OR a.user.id= :id)
			AND (:startDate IS NULL OR a.attendanceDate >= :startDate)
			AND (:endDate IS NULL OR a.attendanceDate <= :endDate)
			""")
	Page<Attendance> findAttendance(@Param("id") Long id, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate, Pageable pageable);

	@Query("""
			SELECT a FROM Attendance a WHERE a.user = :user AND(:startDate IS NULL OR a.attendanceDate >= :startDate)
			AND (:endDate IS NULL OR a.attendanceDate <= :endDate)
			""")
	Page<Attendance> findAttendanceByStaff(@Param("user") User user, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate, Pageable pageable);

	@Query("""
			SELECT a
			FROM Attendance a
			WHERE (:todayDate IS NULL OR a.attendanceDate = :todayDate)
			""")
	Page<Attendance> findTodayAttendanceForAdmin(@Param("todayDate") LocalDate todayDate, Pageable pageable);

	boolean existsByUserAndAttendanceDate(User user, LocalDate attendanceDate);

	@Query("""
			    SELECT a FROM Attendance a   WHERE a.user.id IN :userIds AND a.attendanceDate IN :dates
			""")
	List<Attendance> findBatchAttendance(List<Integer> userIds, List<LocalDate> dates);

	@Query("SELECT COUNT(a) FROM Attendance a WHERE a.attendanceDate = :date AND a.status = :status")
	long countByAttendanceDateAndStatus(@Param("date") LocalDate date, @Param("status") AttendanceStatus status);

	@Query("SELECT COUNT(a) FROM Attendance a WHERE a.attendanceDate = :date")
	long countByAttendanceDate(@Param("date") LocalDate date);

	@Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.id = :userId AND a.attendanceDate BETWEEN :startDate AND :endDate")
	long countByUserAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.id = :userId AND a.attendanceDate BETWEEN :startDate AND :endDate AND a.status = :status")
	long countByUserAndDateRangeAndStatus(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") AttendanceStatus status);

	@Query("SELECT a.status as status, COUNT(a) as count FROM Attendance a WHERE a.attendanceDate = :date GROUP BY a.status")
	List<Object[]> countGroupByStatusForDate(@Param("date") LocalDate date);
}
