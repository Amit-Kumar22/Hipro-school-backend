package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteTopperRequest;
import com.hipro.school.backend.entities.WebsiteTopper;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteTopperRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteTopperService {

	@Autowired
	private WebsiteTopperRepository websiteTopperRepository;

	public WebsiteTopper create(WebsiteTopperRequest request) {
		log.info("Creating website topper: {}", request);
		WebsiteTopper topper = WebsiteTopper.builder()
			.rank(request.getRank())
			.name(request.getName())
			.photoUrl(request.getPhotoUrl())
			.percentage(request.getPercentage())
			.stream(request.getStream())
			.session(request.getSession())
			.active(request.getActive() != null ? request.getActive() : true)
			.build();
		return websiteTopperRepository.save(topper);
	}

	@Transactional
	public WebsiteTopper update(Long id, WebsiteTopperRequest request) {
		log.info("Updating website topper id: {}", id);
		WebsiteTopper existing = getEntity(id);
		existing.setRank(request.getRank());
		existing.setName(request.getName());
		existing.setPhotoUrl(request.getPhotoUrl());
		existing.setPercentage(request.getPercentage());
		existing.setStream(request.getStream());
		existing.setSession(request.getSession());
		if (request.getActive() != null) {
			existing.setActive(request.getActive());
		}
		return websiteTopperRepository.save(existing);
	}

	public WebsiteTopper getEntity(Long id) {
		log.info("Fetching website topper id: {}", id);
		return websiteTopperRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Website topper not found with id: " + id));
	}

	public PageResponse<WebsiteTopper> getAll(Pageable pageable) {
		log.info("Fetching all website toppers with pageable: {}", pageable);
		Page<WebsiteTopper> page = websiteTopperRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website topper id: {}", id);
		WebsiteTopper topper = getEntity(id);
		websiteTopperRepository.delete(topper);
	}

}
