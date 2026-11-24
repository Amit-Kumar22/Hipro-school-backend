package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteHeaderRequest;
import com.hipro.school.backend.entities.WebsiteHeader;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteHeaderRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteHeaderService {

	@Autowired
	private WebsiteHeaderRepository websiteHeaderRepository;

	public WebsiteHeader create(WebsiteHeaderRequest request) {
		log.info("Creating website header: {}", request);
		WebsiteHeader websiteHeader = mapToEntity(request, new WebsiteHeader());
		return websiteHeaderRepository.save(websiteHeader);
	}

	@Transactional
	public WebsiteHeader update(Long id, WebsiteHeaderRequest request) {
		log.info("Updating website header id: {}", id);
		WebsiteHeader existing = getEntity(id);
		mapToEntity(request, existing);
		return websiteHeaderRepository.save(existing);
	}

	private WebsiteHeader mapToEntity(WebsiteHeaderRequest request, WebsiteHeader websiteHeader) {
		websiteHeader.setLogoUrl(request.getLogoUrl());
		websiteHeader.setLogoAlt(request.getLogoAlt());
		if (request.getMobileMenuEnabled() != null) {
			websiteHeader.setMobileMenuEnabled(request.getMobileMenuEnabled());
		}
		websiteHeader.setCtaLabel(request.getCtaLabel());
		websiteHeader.setCtaUrl(request.getCtaUrl());
		return websiteHeader;
	}

	public WebsiteHeader getEntity(Long id) {
		log.info("Fetching website header id: {}", id);
		return websiteHeaderRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Website header not found with id: " + id));
	}

	public PageResponse<WebsiteHeader> getAll(Pageable pageable) {
		log.info("Fetching all website headers with pageable: {}", pageable);
		Page<WebsiteHeader> page = websiteHeaderRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website header id: {}", id);
		WebsiteHeader websiteHeader = getEntity(id);
		websiteHeaderRepository.delete(websiteHeader);
	}

}
