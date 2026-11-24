package com.hipro.school.backend.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hipro.school.backend.entities.Announcement;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    Page<Announcement> findByIsActiveTrue(Pageable pageable);

	List<Announcement> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate today1,
            LocalDate today2
    );

}