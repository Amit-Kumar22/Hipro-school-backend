package com.hipro.school.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hipro.school.backend.entities.Period;

public interface PeriodRepository extends JpaRepository<Period, Long> {

	List<Period> findAllBySchoolClass_IdOrderByPeriodOrderAsc(Long classId);

	boolean existsBySchoolClass_IdAndPeriodOrder(Long classId, Integer periodOrder);

	boolean existsBySchoolClass_IdAndPeriodOrderAndIdNot(Long classId, Integer periodOrder, Long id);
}