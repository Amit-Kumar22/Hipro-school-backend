package com.hipro.school.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.WebsiteColorTheme;

import jakarta.transaction.Transactional;

@Repository
public interface WebsiteColorThemeRepository extends JpaRepository<WebsiteColorTheme, Long> {

	Optional<WebsiteColorTheme> findByThemeName(String themeName);

	// List<ColorTheme> findByIsActiveTrue();

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
	@Query("UPDATE WebsiteColorTheme ct SET ct.isActive = false WHERE ct.isActive = true")
	void deactivateAllThemes();

	List<WebsiteColorTheme> findByIsActiveTrue();
}
