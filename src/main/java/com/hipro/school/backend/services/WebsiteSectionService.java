package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteSectionRequest;
import com.hipro.school.backend.entities.WebsiteSection;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteSectionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteSectionService {

	@Autowired
	private WebsiteSectionRepository websiteSectionRepository;

	public WebsiteSection create(WebsiteSectionRequest request) {
		log.info("Creating website section: {}", request);
		WebsiteSection websiteSection = WebsiteSection.builder()
			.sectionType(request.getSectionType())
			.enabled(request.getEnabled() != null ? request.getEnabled() : true)
			.displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
			.eyebrow(request.getEyebrow())
			.title(request.getTitle())
			.description(request.getDescription())
			.build();
		return websiteSectionRepository.save(websiteSection);
	}

	@Transactional
	public WebsiteSection update(Long id, WebsiteSectionRequest request) {
		log.info("Updating website section id: {}", id);
		WebsiteSection existing = getEntity(id);
		if (request.getSectionType() != null) {
			existing.setSectionType(request.getSectionType());
		}
		if (request.getEnabled() != null) {
			existing.setEnabled(request.getEnabled());
		}
		if (request.getDisplayOrder() != null) {
			existing.setDisplayOrder(request.getDisplayOrder());
		}
		existing.setEyebrow(request.getEyebrow());
		existing.setTitle(request.getTitle());
		existing.setDescription(request.getDescription());
		return websiteSectionRepository.save(existing);
	}

	public WebsiteSection getEntity(Long id) {
		log.info("Fetching website section id: {}", id);
		return websiteSectionRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Website section not found with id: " + id));
	}

	public PageResponse<WebsiteSection> getAll(Pageable pageable) {
		log.info("Fetching all website sections with pageable: {}", pageable);
		Page<WebsiteSection> page = websiteSectionRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website section id: {}", id);
		WebsiteSection websiteSection = getEntity(id);
		websiteSectionRepository.delete(websiteSection);
	}

}
