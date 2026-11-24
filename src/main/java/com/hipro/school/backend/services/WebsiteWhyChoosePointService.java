package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteWhyChoosePointRequest;
import com.hipro.school.backend.entities.WebsiteWhyChoosePoint;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteWhyChoosePointRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteWhyChoosePointService {

	@Autowired
	private WebsiteWhyChoosePointRepository websiteWhyChoosePointRepository;

	public WebsiteWhyChoosePoint create(WebsiteWhyChoosePointRequest request) {
		log.info("Creating website why choose point: {}", request);
		WebsiteWhyChoosePoint whyChoosePoint = WebsiteWhyChoosePoint.builder()
			.point(request.getPoint())
			.displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
			.active(request.getActive() != null ? request.getActive() : true)
			.build();
		return websiteWhyChoosePointRepository.save(whyChoosePoint);
	}

	@Transactional
	public WebsiteWhyChoosePoint update(Long id, WebsiteWhyChoosePointRequest request) {
		log.info("Updating website why choose point id: {}", id);
		WebsiteWhyChoosePoint existing = getEntity(id);
		existing.setPoint(request.getPoint());
		if (request.getDisplayOrder() != null) {
			existing.setDisplayOrder(request.getDisplayOrder());
		}
		if (request.getActive() != null) {
			existing.setActive(request.getActive());
		}
		return websiteWhyChoosePointRepository.save(existing);
	}

	public WebsiteWhyChoosePoint getEntity(Long id) {
		log.info("Fetching website why choose point id: {}", id);
		return websiteWhyChoosePointRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Website why choose point not found with id: " + id));
	}

	public PageResponse<WebsiteWhyChoosePoint> getAll(Pageable pageable) {
		log.info("Fetching all website why choose points with pageable: {}", pageable);
		Page<WebsiteWhyChoosePoint> page = websiteWhyChoosePointRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website why choose point id: {}", id);
		WebsiteWhyChoosePoint whyChoosePoint = getEntity(id);
		websiteWhyChoosePointRepository.delete(whyChoosePoint);
	}

}
