package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteSocialLinkRequest;
import com.hipro.school.backend.entities.WebsiteSocialLink;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteSocialLinkRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteSocialLinkService {

	@Autowired
	private WebsiteSocialLinkRepository websiteSocialLinkRepository;

	public WebsiteSocialLink create(WebsiteSocialLinkRequest request) {
		log.info("Creating website social link: {}", request);
		WebsiteSocialLink socialLink = mapToEntity(request, new WebsiteSocialLink());
		return websiteSocialLinkRepository.save(socialLink);
	}

	@Transactional
	public WebsiteSocialLink update(Long id, WebsiteSocialLinkRequest request) {
		log.info("Updating website social link id: {}", id);
		WebsiteSocialLink existing = getEntity(id);
		mapToEntity(request, existing);
		return websiteSocialLinkRepository.save(existing);
	}

	private WebsiteSocialLink mapToEntity(WebsiteSocialLinkRequest request, WebsiteSocialLink websiteSocialLink) {
		if (request.getPlatform() != null) {
			websiteSocialLink.setPlatform(request.getPlatform());
		}
		websiteSocialLink.setUrl(request.getUrl());
		if (request.getActive() != null) {
			websiteSocialLink.setActive(request.getActive());
		}
		if (request.getDisplayOrder() != null) {
			websiteSocialLink.setDisplayOrder(request.getDisplayOrder());
		}
		return websiteSocialLink;
	}

	public WebsiteSocialLink getEntity(Long id) {
		log.info("Fetching website social link id: {}", id);
		return websiteSocialLinkRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Website social link not found with id: " + id));
	}

	public PageResponse<WebsiteSocialLink> getAll(Pageable pageable) {
		log.info("Fetching all website social links with pageable: {}", pageable);
		Page<WebsiteSocialLink> page = websiteSocialLinkRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website social link id: {}", id);
		WebsiteSocialLink socialLink = getEntity(id);
		websiteSocialLinkRepository.delete(socialLink);
	}

}
