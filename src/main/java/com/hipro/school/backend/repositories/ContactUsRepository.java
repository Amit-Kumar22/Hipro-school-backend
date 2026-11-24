package com.hipro.school.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.ContactUs;

@Repository
public interface ContactUsRepository extends JpaRepository<ContactUs, Long> {

	Optional<ContactUs> findByIsActiveTrue();

	@Modifying
	@Query("UPDATE ContactUs c SET c.isActive = false WHERE c.isActive = true")
	void updateAllActiveFalse();

	/*
	 * Optional<ColorTheme> findByThemeName(String themeName);
	 * 
	 * // List<ColorTheme> findByIsActiveTrue();
	 * 
	 * @Modifying(clearAutomatically = true, flushAutomatically = true)
	 * 
	 * @Transactional
	 * 
	 * @Query("UPDATE ColorTheme ct SET ct.isActive = false WHERE ct.isActive = true"
	 * ) void deactivateAllThemes();
	 * 
	 * List<ColorTheme> findByIsActiveTrue();
	 */
}
