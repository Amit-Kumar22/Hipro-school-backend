package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteGalleryItemRequest;
import com.hipro.school.backend.entities.WebsiteGalleryItem;
import com.hipro.school.backend.enums.GalleryType;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteGalleryItemRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteGalleryItemService {

	@Autowired
	private WebsiteGalleryItemRepository websiteGalleryItemRepository;

	public WebsiteGalleryItem create(WebsiteGalleryItemRequest request) {
		log.info("Creating website gallery item: {}", request);
		WebsiteGalleryItem galleryItem = WebsiteGalleryItem.builder()
			.title(request.getTitle())
			.category(request.getCategory())
			.imageUrl(request.getImageUrl())
			.type(request.getType() != null ? request.getType() : GalleryType.IMAGE)
			.displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
			.active(request.getActive() != null ? request.getActive() : true)
			.build();
		return websiteGalleryItemRepository.save(galleryItem);
	}

	@Transactional
	public WebsiteGalleryItem update(Long id, WebsiteGalleryItemRequest request) {
		log.info("Updating website gallery item id: {}", id);
		WebsiteGalleryItem existing = getEntity(id);
		existing.setTitle(request.getTitle());
		existing.setCategory(request.getCategory());
		existing.setImageUrl(request.getImageUrl());
		if (request.getType() != null) {
			existing.setType(request.getType());
		}
		if (request.getDisplayOrder() != null) {
			existing.setDisplayOrder(request.getDisplayOrder());
		}
		if (request.getActive() != null) {
			existing.setActive(request.getActive());
		}
		return websiteGalleryItemRepository.save(existing);
	}

	public WebsiteGalleryItem getEntity(Long id) {
		log.info("Fetching website gallery item id: {}", id);
		return websiteGalleryItemRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Website gallery item not found with id: " + id));
	}

	public PageResponse<WebsiteGalleryItem> getAll(Pageable pageable) {
		log.info("Fetching all website gallery items with pageable: {}", pageable);
		Page<WebsiteGalleryItem> page = websiteGalleryItemRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website gallery item id: {}", id);
		WebsiteGalleryItem galleryItem = getEntity(id);
		websiteGalleryItemRepository.delete(galleryItem);
	}

}
