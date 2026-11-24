package com.hipro.school.backend.repositories;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hipro.school.backend.entities.WeeklyTimetable;

public interface WeeklyTimetableRepository
		extends JpaRepository<WeeklyTimetable, Long>, JpaSpecificationExecutor<WeeklyTimetable> {

	boolean existsByPeriod_IdAndDayOfWeek(Long periodId, DayOfWeek dayOfWeek);

	boolean existsByPeriod_IdAndDayOfWeekAndIdNot(Long periodId, DayOfWeek dayOfWeek, Long id);

	List<WeeklyTimetable> findAllByPeriod_SchoolClass_IdAndDayOfWeekOrderByPeriod_PeriodOrderAsc(Long classId,
			DayOfWeek dayOfWeek);

	List<WeeklyTimetable> findAllByPeriod_SchoolClass_IdOrderByDayOfWeekAscPeriod_PeriodOrderAsc(Long classId);
}