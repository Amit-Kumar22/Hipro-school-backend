package com.hipro.school.backend.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.entities.YouTubeTestimonial;
import com.hipro.school.backend.repositories.YouTubeTestimonialRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class YouTubeTestimonialService {

	@Autowired
	private YouTubeTestimonialRepository tubeTestimonialRepository;

	private static final String CACHE_ALL_ACTIVE = "all-youtube-testimonial-Cache";
	private static final String CACHE_BY_ID = "youtube-testimonial-by-id";

	@Cacheable(value = CACHE_ALL_ACTIVE, key = "'ALL'")
	public ApiResponse<List<YouTubeTestimonial>> getAllYouTubeTestimonial() {
		log.info("Fetching all YouTube testimonials");
		return ApiResponse.success(HttpStatus.OK, "All YouTube testimonials fetched successfully",
				tubeTestimonialRepository.findAll());
	}

	@Cacheable(value = CACHE_ALL_ACTIVE)
	public ApiResponse<List<YouTubeTestimonial>> getAllActiveYouTubeTestimonial() {
		log.info("Fetching all active YouTube testimonials");
		List<YouTubeTestimonial> activeList = tubeTestimonialRepository.findAllActive();
		Collections.shuffle(activeList);
		return ApiResponse.success(HttpStatus.OK, "All YouTube testimonials fetched successfully", activeList);
	}

	@Cacheable(value = CACHE_BY_ID, key = "#id")
	public YouTubeTestimonial getYouTubeTestimonialById(Long id) {
		log.info("Fetching YouTube testimonial by id: {}", id);
		return tubeTestimonialRepository.findById(id).orElse(null);
	}

	@Transactional
	@CacheEvict(value = { CACHE_ALL_ACTIVE, CACHE_BY_ID }, allEntries = true)
	public YouTubeTestimonial updateYouTubeTestimonial(Long id, YouTubeTestimonial ytReq) {
		log.info("Updating YouTube testimonial id: {}", id);
		YouTubeTestimonial yt = getYouTubeTestimonialById(id);
		yt.setVideoTitle(yt.getVideoTitle());
		yt.setVideoUrl(yt.getVideoUrl());
		yt.setIsActive(ytReq.getIsActive());
		return tubeTestimonialRepository.save(yt);
	}

	@Transactional
	@CacheEvict(value = CACHE_ALL_ACTIVE, allEntries = true)
	public YouTubeTestimonial saveYouTubeTestimonial(YouTubeTestimonial ytReq) {
		log.info("Saving YouTube testimonial: {}", ytReq.getVideoTitle());
		ytReq.setIsActive(true);
		return tubeTestimonialRepository.save(ytReq);
	}

	@Transactional
	@CacheEvict(value = { CACHE_ALL_ACTIVE, CACHE_BY_ID }, allEntries = true)
	public void deleteCountry(Long id) {
		log.info("Deleting YouTube testimonial id: {}", id);
		tubeTestimonialRepository.deleteById(id);
	}

}