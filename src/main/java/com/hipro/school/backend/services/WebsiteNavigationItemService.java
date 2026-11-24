package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteNavigationItemRequest;
import com.hipro.school.backend.entities.WebsiteNavigationItem;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteNavigationItemRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteNavigationItemService {

	@Autowired
	private WebsiteNavigationItemRepository websiteNavigationItemRepository;

	public WebsiteNavigationItem create(WebsiteNavigationItemRequest request) {
		log.info("Creating website navigation item: {}", request);
		WebsiteNavigationItem navigationItem = WebsiteNavigationItem.builder()
			.label(request.getLabel())
			.url(request.getUrl())
			.displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
			.active(request.getActive() != null ? request.getActive() : true)
			.build();
		return websiteNavigationItemRepository.save(navigationItem);
	}

	@Transactional
	public WebsiteNavigationItem update(Long id, WebsiteNavigationItemRequest request) {
		log.info("Updating website navigation item id: {}", id);
		WebsiteNavigationItem existing = getEntity(id);
		existing.setLabel(request.getLabel());
		existing.setUrl(request.getUrl());
		if (request.getDisplayOrder() != null) {
			existing.setDisplayOrder(request.getDisplayOrder());
		}
		if (request.getActive() != null) {
			existing.setActive(request.getActive());
		}
		return websiteNavigationItemRepository.save(existing);
	}

	public WebsiteNavigationItem getEntity(Long id) {
		log.info("Fetching website navigation item id: {}", id);
		return websiteNavigationItemRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Website navigation item not found with id: " + id));
	}

	public PageResponse<WebsiteNavigationItem> getAll(Pageable pageable) {
		log.info("Fetching all website navigation items with pageable: {}", pageable);
		Page<WebsiteNavigationItem> page = websiteNavigationItemRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website navigation item id: {}", id);
		WebsiteNavigationItem navigationItem = getEntity(id);
		websiteNavigationItemRepository.delete(navigationItem);
	}

}
