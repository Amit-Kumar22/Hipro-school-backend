package com.hipro.school.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.AnnouncementReq;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.Announcement;
import com.hipro.school.backend.services.AnnouncementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Announcement APIs", description = "APIs for managing announcements")
@RestController
@RequestMapping("/v1/announcements")
@Slf4j
public class AnnouncementController {

	@Autowired
	private AnnouncementService announcementService;

	@Operation(summary = "Get all announcements", description = "Fetches a paginated list of announcements. Supports pagination.")
	@GetMapping
	public ResponseEntity<PageResponse<Announcement>> getAllAnnouncements(Pageable pageable) {
				return ResponseEntity.ok(announcementService.getAllAnnouncements(pageable));
	}

	@Operation(summary = "Create an announcement", description = "Creates a new announcement using the AnnouncementReq request body.")
	@PostMapping
	public ResponseEntity<Announcement> createAnnouncement(@RequestBody AnnouncementReq announcement) {
		log.info("Creating new announcement: {}", announcement);
		announcementService.createAnnouncement(announcement);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "Update an announcement", description = "Updates an existing announcement by id using the AnnouncementReq request body.")
	@PutMapping("/{id}")
	public ResponseEntity<Announcement> updateAnnouncement(@PathVariable Long id,
			@RequestBody AnnouncementReq announcement) {
		log.info("Updating announcement id: {} with announcement: {}", id, announcement);
		announcementService.updateAnnouncement(id, announcement);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Delete an announcement", description = "Deletes an announcement by its id path parameter.")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteAnnouncement(@PathVariable Long id) {
		log.info("Deleting announcement id: {}", id);
		announcementService.deleteAnnouncement(id);
		return ResponseEntity.ok("Announcement deleted successfully");
	}

	@Operation(summary = "Get an announcement by id", description = "Fetches an announcement by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<Announcement> getAnnouncement(@PathVariable Long id) {
		log.info("Fetching announcement by id: {}", id);
		return ResponseEntity.ok(announcementService.getAnnouncementById(id));
	}

	@Operation(summary = "Get active announcements", description = "Fetches all active announcements.")
	@GetMapping("/active")
	public ResponseEntity<List<Announcement>> getActiveAnnouncements() {
		log.info("Fetching all active announcements");
		return ResponseEntity.ok(announcementService.getActiveAnnouncements());
	}

}