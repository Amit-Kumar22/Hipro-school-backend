package com.hipro.school.backend.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.Holiday;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

	@Query("""
			SELECT oh FROM Holiday oh WHERE oh.holidayDate = :today
			""")
	Optional<Holiday> findByHolidayDate(LocalDate today);

}
