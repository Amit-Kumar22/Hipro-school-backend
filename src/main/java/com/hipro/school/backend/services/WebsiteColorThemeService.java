package com.hipro.school.backend.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.hipro.school.backend.dtos.ColorThemeRequest;
import com.hipro.school.backend.entities.WebsiteColorTheme;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteColorThemeRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class WebsiteColorThemeService {

	@Autowired
	private WebsiteColorThemeRepository repository;

	@Autowired
	private ModelMapper modelMapper;

	private static final String COLOR_THEME_CACHE = "all-color-Cache";

	@CacheEvict(value = COLOR_THEME_CACHE, allEntries = true)
	public void create(WebsiteColorTheme dto) {
		log.info("Creating new color theme: {}", dto.getThemeName());
		dto.setIsActive(true); // default to inactive on creation
		repository.save(dto);
	}

	@CacheEvict(value = COLOR_THEME_CACHE, allEntries = true)
	public void update(Long id, ColorThemeRequest request) {
		log.info("Updating color theme with ID: {}", id);
		WebsiteColorTheme theme = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Theme not found with id: " + id));
		modelMapper.map(request, theme);
		theme.setId(id);
		repository.save(theme);
	}

	@CacheEvict(value = COLOR_THEME_CACHE, allEntries = true)
	public void delete(Long id) {
		log.info("Deleting color theme with ID: {}", id);
		WebsiteColorTheme theme = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Theme not found" + id));
		repository.delete(theme);
	}

	@Cacheable(value = COLOR_THEME_CACHE, key = "#id")
	public WebsiteColorTheme getById(Long id) {
		log.info("Fetching color theme by ID: {}", id);
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Theme not found" + id));
	}

	@Cacheable(value = COLOR_THEME_CACHE, key = "'all'")
	public List<WebsiteColorTheme> getAll() {
		log.info("Fetching all color themes");
		List<WebsiteColorTheme> colorThemes = repository.findAll();
		return colorThemes;
	}

	@Cacheable(value = COLOR_THEME_CACHE, key = "'active'")
	public WebsiteColorTheme getActiveThemes() {
		log.info("Fetching  active color themes");
		return repository.findByIsActiveTrue().stream().findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("No active color theme found"));
	}

	@CacheEvict(value = COLOR_THEME_CACHE, allEntries = true)
	public WebsiteColorTheme activateColorTheme(Long colourThemeId) {
		log.info("Activating color theme with ID: {}", colourThemeId);
		repository.deactivateAllThemes();
		WebsiteColorTheme theme = repository.findById(colourThemeId)
				.orElseThrow(() -> new ResourceNotFoundException("Color theme not found with id: " + colourThemeId));

		// Step 3: Activate the theme
		theme.setIsActive(true);
		return repository.save(theme);
	}

}
