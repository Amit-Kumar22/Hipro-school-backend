package com.hipro.school.backend.controllers;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.SchoolLead;
import com.hipro.school.backend.services.JustdialSchoolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "School Lead APIs", description = "APIs for importing and fetching school leads")
@RestController
@RequestMapping("/v1/school-leads")
@RequiredArgsConstructor
@Slf4j
public class LeadController {

	private final JustdialSchoolService justdialSchoolService;

	@Operation(summary = "Import leads from Justdial", description = "Imports school leads from Justdial for the given city and category query params and returns the saved leads.")
	@PostMapping("/import/justdial")
	public ResponseEntity<List<SchoolLead>> importFromJustdial(@RequestParam String city,
			@RequestParam(defaultValue = "Schools") String category) {
		log.info("Importing leads from Justdial for city: {} and category: {}", city, category);
		List<SchoolLead> leads = justdialSchoolService.fetchAndSaveSchools(city, category);
		return ResponseEntity.ok(leads);
	}

	@Operation(summary = "Get all school leads", description = "Fetches a paginated list of school leads.")
	@GetMapping
	public ResponseEntity<PageResponse<SchoolLead>> getAllLeads(Pageable pageable) {
		log.info("Fetching all school leads with pageable: {}", pageable);
		return ResponseEntity.ok(justdialSchoolService.getAllLeads(pageable));
	}
}