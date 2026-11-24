package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteFeatureRequest;
import com.hipro.school.backend.entities.WebsiteFeature;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteFeatureRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteFeatureService {

	@Autowired
	private WebsiteFeatureRepository websiteFeatureRepository;

	public WebsiteFeature create(WebsiteFeatureRequest request) {
		log.info("Creating website feature: {}", request);
		WebsiteFeature websiteFeature = WebsiteFeature.builder()
			.icon(request.getIcon())
			.title(request.getTitle())
			.description(request.getDescription())
			.displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
			.active(request.getActive() != null ? request.getActive() : true)
			.build();
		return websiteFeatureRepository.save(websiteFeature);
	}

	@Transactional
	public WebsiteFeature update(Long id, WebsiteFeatureRequest request) {
		log.info("Updating website feature id: {}", id);
		WebsiteFeature existing = getEntity(id);
		existing.setIcon(request.getIcon());
		existing.setTitle(request.getTitle());
		existing.setDescription(request.getDescription());
		if (request.getDisplayOrder() != null) {
			existing.setDisplayOrder(request.getDisplayOrder());
		}
		if (request.getActive() != null) {
			existing.setActive(request.getActive());
		}
		return websiteFeatureRepository.save(existing);
	}

	public WebsiteFeature getEntity(Long id) {
		log.info("Fetching website feature id: {}", id);
		return websiteFeatureRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Website feature not found with id: " + id));
	}

	public PageResponse<WebsiteFeature> getAll(Pageable pageable) {
		log.info("Fetching all website features with pageable: {}", pageable);
		Page<WebsiteFeature> page = websiteFeatureRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website feature id: {}", id);
		WebsiteFeature websiteFeature = getEntity(id);
		websiteFeatureRepository.delete(websiteFeature);
	}

}
