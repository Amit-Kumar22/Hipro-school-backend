package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteTestimonialRequest;
import com.hipro.school.backend.entities.WebsiteTestimonial;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteTestimonialRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteTestimonialService {

	@Autowired
	private WebsiteTestimonialRepository websiteTestimonialRepository;

	public WebsiteTestimonial create(WebsiteTestimonialRequest request) {
		log.info("Creating website testimonial: {}", request);
		WebsiteTestimonial testimonial = WebsiteTestimonial.builder()
			.quote(request.getQuote())
			.parentName(request.getParentName())
			.relation(request.getRelation())
			.photoUrl(request.getPhotoUrl())
			.rating(request.getRating())
			.displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
			.active(request.getActive() != null ? request.getActive() : true)
			.build();
		return websiteTestimonialRepository.save(testimonial);
	}

	@Transactional
	public WebsiteTestimonial update(Long id, WebsiteTestimonialRequest request) {
		log.info("Updating website testimonial id: {}", id);
		WebsiteTestimonial existing = getEntity(id);
		existing.setQuote(request.getQuote());
		existing.setParentName(request.getParentName());
		existing.setRelation(request.getRelation());
		existing.setPhotoUrl(request.getPhotoUrl());
		existing.setRating(request.getRating());
		if (request.getDisplayOrder() != null) {
			existing.setDisplayOrder(request.getDisplayOrder());
		}
		if (request.getActive() != null) {
			existing.setActive(request.getActive());
		}
		return websiteTestimonialRepository.save(existing);
	}

	public WebsiteTestimonial getEntity(Long id) {
		log.info("Fetching website testimonial id: {}", id);
		return websiteTestimonialRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Website testimonial not found with id: " + id));
	}

	public PageResponse<WebsiteTestimonial> getAll(Pageable pageable) {
		log.info("Fetching all website testimonials with pageable: {}", pageable);
		Page<WebsiteTestimonial> page = websiteTestimonialRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website testimonial id: {}", id);
		WebsiteTestimonial testimonial = getEntity(id);
		websiteTestimonialRepository.delete(testimonial);
	}

}
