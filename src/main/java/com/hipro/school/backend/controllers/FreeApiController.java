package com.hipro.school.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.AboutUs;
import com.hipro.school.backend.entities.Announcement;
import com.hipro.school.backend.entities.Chatbot;
import com.hipro.school.backend.entities.WebsiteColorTheme;
import com.hipro.school.backend.entities.YouTubeTestimonial;
import com.hipro.school.backend.services.AboutUsService;
import com.hipro.school.backend.services.AnnouncementService;
import com.hipro.school.backend.services.ChatbotService;
import com.hipro.school.backend.services.WebsiteColorThemeService;
import com.hipro.school.backend.services.YouTubeTestimonialService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/free")
@Slf4j
@Tag(name = "Free api Management", description = "APIs to manage dynamic Free api Management for UI")
public class FreeApiController {

	@Autowired
	private WebsiteColorThemeService colorService;

	@Autowired
	private YouTubeTestimonialService tubeTestimonialService;

	@Autowired
	private AnnouncementService announcementService;

	@Autowired
	private AboutUsService aboutUsService;

	@Autowired
	private ChatbotService chatBotService;

	@Operation(summary = "Get all About Us entries", description = "Fetches paginated About Us content based on the Pageable query parameters.")
	@GetMapping("/about-us")
	public ResponseEntity<PageResponse<AboutUs>> getAll(Pageable pageable) {
		log.info("API: Fetching about us with pageable {}", pageable);
		return ResponseEntity.ok(aboutUsService.getAllAboutUs(pageable));
	}

	@Operation(summary = "Get active announcements", description = "Fetches all currently active announcements.")
	@GetMapping("/announcements/active")
	public ResponseEntity<List<Announcement>> getActiveAnnouncements() {
		log.info("API: Fetching active announcements");
		return ResponseEntity.ok(announcementService.getActiveAnnouncements());
	}

	@Operation(summary = "Get active color themes")
	@GetMapping("/colour-theme")
	public ResponseEntity<WebsiteColorTheme> getActive() {
		log.info("API: Fetching active color themes");
		return ResponseEntity.ok(colorService.getActiveThemes());
	}

	@Operation(summary = "Get all Active YouTube testimonials")
	// @GetMapping("/youtube-testimonial")
	public ResponseEntity<ApiResponse<List<YouTubeTestimonial>>> getAllActive() {
		log.info("API: Fetching active YouTube testimonials");
		return ResponseEntity.ok(tubeTestimonialService.getAllActiveYouTubeTestimonial());
	}

	@Operation(summary = "Search chatbots by keyword", description = "Searches active chatbots matching the supplied keyword query parameter and returns the results.")
	@GetMapping("/search")
	public ResponseEntity<List<Chatbot>> getByKeyword(@RequestParam String keyword) {
		log.info("API: Fetching chatbots by keyword {}", keyword);
		return ResponseEntity.ok(chatBotService.findByKeyword(keyword));
	}

}
