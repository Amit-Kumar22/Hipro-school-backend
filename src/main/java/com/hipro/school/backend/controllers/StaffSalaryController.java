package com.hipro.school.backend.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.StaffSalaryRequest;
import com.hipro.school.backend.dtos.StaffSalaryResponse;
import com.hipro.school.backend.enums.SalaryStatus;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.StaffSalaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Staff Salary APIs", description = "APIs for managing staff salaries")
@RestController
@RequestMapping("/v1/staff-salaries")
@RequiredArgsConstructor
@Slf4j
public class StaffSalaryController {

	private final StaffSalaryService staffSalaryService;

	@Operation(summary = "Get staff salaries", description = "Fetches a paginated list of staff salaries. Supports optional userId and status filtering via query params.")
	@GetMapping
	public ResponseEntity<PageResponse<StaffSalaryResponse>> getSalaries(
			@RequestParam(required = false) Long userId,
			@RequestParam(required = false) SalaryStatus status,
			Pageable pageable) {
		return ResponseEntity.ok(staffSalaryService.getSalaries(userId, status, pageable));
	}

	@Operation(summary = "Get a staff salary by id", description = "Fetches a single staff salary by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<StaffSalaryResponse> getById(@PathVariable Long id) {
		log.info("Fetching staff salary by id: {}", id);
		return ResponseEntity.ok(staffSalaryService.getById(id));
	}

	@Operation(summary = "Download salary slip PDF", description = "Generates and downloads a salary slip PDF for the given salary id.")
	@GetMapping("/{id}/pdf")
	public ResponseEntity<byte[]> downloadSalaryPdf(@PathVariable Long id) {
		log.info("Generating salary PDF for salary id: {}", id);
		byte[] pdfBytes = staffSalaryService.generateSalaryPdf(id);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "salary-slip-" + id + ".pdf");
		headers.setContentLength(pdfBytes.length);

		return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}

	@Operation(summary = "Get current user's salary", description = "Fetches a paginated list of salaries for the currently logged-in user.")
	@GetMapping("/my-salaries")
	public ResponseEntity<PageResponse<StaffSalaryResponse>> getMySalaries(
			@RequestParam(required = false) SalaryStatus status, Pageable pageable) {
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(staffSalaryService.getSalariesByEmail(email, status, pageable));
	}

	@Operation(summary = "Create a staff salary", description = "Creates a new staff salary record. Rejects duplicate salary for the same user and month.")
	@PostMapping
	public ResponseEntity<String> create(@Valid @RequestBody StaffSalaryRequest request) {
		log.info("Creating new staff salary: {}", request);
		staffSalaryService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("Staff salary has been created successfully");
	}

	@Operation(summary = "Update a staff salary", description = "Updates an existing staff salary by id using the validated StaffSalaryRequest request body.")
	@PutMapping("/{id}")
	public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody StaffSalaryRequest request) {
		log.info("Updating staff salary id: {} with request: {}", id, request);
		staffSalaryService.update(id, request);
		return ResponseEntity.ok("Staff salary updated successfully");
	}

	@Operation(summary = "Update staff salary status", description = "Updates only the status of a staff salary by id.")
	@PutMapping("/{id}/status")
	public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam SalaryStatus status) {
		log.info("Updating status of staff salary id: {} to {}", id, status);
		staffSalaryService.updateStatus(id, status);
		return ResponseEntity.ok("Staff salary status updated successfully");
	}

	@Operation(summary = "Delete a staff salary", description = "Deletes a staff salary by its id path parameter.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info("Deleting staff salary id: {}", id);
		staffSalaryService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
