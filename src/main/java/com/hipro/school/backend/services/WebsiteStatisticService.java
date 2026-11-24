package com.hipro.school.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.WebsiteStatisticRequest;
import com.hipro.school.backend.entities.WebsiteStatistic;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.WebsiteStatisticRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteStatisticService {

	@Autowired
	private WebsiteStatisticRepository websiteStatisticRepository;

	public WebsiteStatistic create(WebsiteStatisticRequest request) {
		log.info("Creating website statistic: {}", request);
		WebsiteStatistic statistic = WebsiteStatistic.builder()
			.icon(request.getIcon())
			.number(request.getNumber())
			.label(request.getLabel())
			.displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
			.active(request.getActive() != null ? request.getActive() : true)
			.build();
		return websiteStatisticRepository.save(statistic);
	}

	@Transactional
	public WebsiteStatistic update(Long id, WebsiteStatisticRequest request) {
		log.info("Updating website statistic id: {}", id);
		WebsiteStatistic existing = getEntity(id);
		existing.setIcon(request.getIcon());
		existing.setNumber(request.getNumber());
		existing.setLabel(request.getLabel());
		if (request.getDisplayOrder() != null) {
			existing.setDisplayOrder(request.getDisplayOrder());
		}
		if (request.getActive() != null) {
			existing.setActive(request.getActive());
		}
		return websiteStatisticRepository.save(existing);
	}

	public WebsiteStatistic getEntity(Long id) {
		log.info("Fetching website statistic id: {}", id);
		return websiteStatisticRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Website statistic not found with id: " + id));
	}

	public PageResponse<WebsiteStatistic> getAll(Pageable pageable) {
		log.info("Fetching all website statistics with pageable: {}", pageable);
		Page<WebsiteStatistic> page = websiteStatisticRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public void delete(Long id) {
		log.info("Deleting website statistic id: {}", id);
		WebsiteStatistic statistic = getEntity(id);
		websiteStatisticRepository.delete(statistic);
	}

}
