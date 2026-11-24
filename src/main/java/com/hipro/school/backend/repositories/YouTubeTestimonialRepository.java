package com.hipro.school.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hipro.school.backend.entities.YouTubeTestimonial;

public interface YouTubeTestimonialRepository extends JpaRepository<YouTubeTestimonial, Long> {

	@Query("SELECT y FROM YouTubeTestimonial y WHERE y.isActive = true")
	List<YouTubeTestimonial> findAllActive();

}