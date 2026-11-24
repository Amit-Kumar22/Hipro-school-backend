package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteHeroButtonRequest;
import com.hipro.school.backend.entities.WebsiteHeroButton;
import com.hipro.school.backend.entities.WebsiteHeroSlide;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteHeroButtonRepository;
import com.hipro.school.backend.repositories.WebsiteHeroSlideRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteHeroButtonService {

	@Autowired
	private WebsiteHeroButtonRepository websiteHeroButtonRepository;

	@Autowired
	private WebsiteHeroSlideRepository websiteHeroSlideRepository;

	public WebsiteHeroButton create(WebsiteHeroButtonRequest request) {
		log.info("Creating website hero button: {}", request);
		WebsiteHeroSlide heroSlide = websiteHeroSlideRepository.findById(request.getHeroSlideId())
			.orElseThrow(() -> new ResourceNotFoundException(
					"Hero slide not found with id: " + request.getHeroSlideId()));
		WebsiteHeroButton heroButton = mapToEntity(request, new WebsiteHeroButton());
		heroButton.setHeroSlide(heroSlide);
		return websiteHeroButtonRepository.save(heroButton);
	}

	@Transactional
	public WebsiteHeroButton update(Long id, WebsiteHeroButtonRequest request) {
		log.info("Updating website hero button id: {}", id);
		WebsiteHeroButton existing = getEntity(id);
		mapToEntity(request, existing);
		return websiteHeroButtonRepository.save(existing);
	}

	private WebsiteHeroButton mapToEntity(WebsiteHeroButtonRequest request, WebsiteHeroButton websiteHeroButton) {
		websiteHeroButton.setLabel(request.getLabel());
		websiteHeroButton.setUrl(request.getUrl());
		if (request.getStyle() != null) {
			websiteHeroButton.setStyle(request.getStyle());
		}
		websiteHeroButton.setIcon(request.getIcon());
		if (request.getDisplayOrder() != null) {
			websiteHeroButton.setDisplayOrder(request.getDisplayOrder());
		}
		return websiteHeroButton;
	}

	public WebsiteHeroButton getEntity(Long id) {
		log.info("Fetching website hero button id: {}", id);
		return websiteHeroButtonRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Website hero button not found with id: " + id));
	}

	public PageResponse<WebsiteHeroButton> getAll(Pageable pageable, Long heroSlideId) {
		log.info("Fetching all website hero buttons for hero slide id: {} with pageable: {}", heroSlideId, pageable);
		Page<WebsiteHeroButton> page = websiteHeroButtonRepository.findAllByHeroSlide_Id(heroSlideId, pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website hero button id: {}", id);
		WebsiteHeroButton heroButton = getEntity(id);
		websiteHeroButtonRepository.delete(heroButton);
	}

}
