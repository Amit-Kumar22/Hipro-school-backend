package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteHeroSlideRequest;
import com.hipro.school.backend.entities.WebsiteHeroSlide;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteHeroSlideRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteHeroSlideService {

	@Autowired
	private WebsiteHeroSlideRepository websiteHeroSlideRepository;

	public WebsiteHeroSlide create(WebsiteHeroSlideRequest request) {
		log.info("Creating website hero slide: {}", request);
		WebsiteHeroSlide heroSlide = WebsiteHeroSlide.builder()
			.backgroundImageUrl(request.getBackgroundImageUrl())
			.badge(request.getBadge())
			.title(request.getTitle())
			.subtitle(request.getSubtitle())
			.description(request.getDescription())
			.displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
			.active(request.getActive() != null ? request.getActive() : true)
			.build();
		return websiteHeroSlideRepository.save(heroSlide);
	}

	@Transactional
	public WebsiteHeroSlide update(Long id, WebsiteHeroSlideRequest request) {
		log.info("Updating website hero slide id: {}", id);
		WebsiteHeroSlide existing = getEntity(id);
		existing.setBackgroundImageUrl(request.getBackgroundImageUrl());
		existing.setBadge(request.getBadge());
		existing.setTitle(request.getTitle());
		existing.setSubtitle(request.getSubtitle());
		existing.setDescription(request.getDescription());
		if (request.getDisplayOrder() != null) {
			existing.setDisplayOrder(request.getDisplayOrder());
		}
		if (request.getActive() != null) {
			existing.setActive(request.getActive());
		}
		return websiteHeroSlideRepository.save(existing);
	}

	public WebsiteHeroSlide getEntity(Long id) {
		log.info("Fetching website hero slide id: {}", id);
		return websiteHeroSlideRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Website hero slide not found with id: " + id));
	}

	public PageResponse<WebsiteHeroSlide> getAll(Pageable pageable) {
		log.info("Fetching all website hero slides with pageable: {}", pageable);
		Page<WebsiteHeroSlide> page = websiteHeroSlideRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website hero slide id: {}", id);
		WebsiteHeroSlide heroSlide = getEntity(id);
		websiteHeroSlideRepository.delete(heroSlide);
	}

}
