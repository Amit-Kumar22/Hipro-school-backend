package com.hipro.school.backend.controllers;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ExamResultFilterRequest;
import com.hipro.school.backend.dtos.ExamResultRequest;
import com.hipro.school.backend.dtos.ExamResultResponse;
import com.hipro.school.backend.enums.ResultStatus;
import com.hipro.school.backend.services.ExamResultService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Exam Result APIs", description = "This collection contains APIs for Exam Result resources")
@RestController
@RequestMapping("/v1/exam-results")
@RequiredArgsConstructor
@Validated
public class ExamResultController {

	private final ExamResultService examResultService;

	@Operation(summary = "Search exam results", description = "Search and filter exam results by exam, subject, student, class, status, absence, or pass status.")
	@GetMapping
	public ResponseEntity<List<ExamResultResponse>> search(@RequestParam(required = false) Long examId,
			@RequestParam(required = false) Long examSubjectId, @RequestParam(required = false) Long studentId,
			@RequestParam(required = false) Long classId, @RequestParam(required = false) ResultStatus status,
			@RequestParam(required = false) Boolean absent, @RequestParam(required = false) Boolean passed) {
		log.info(">> search exam results with filters: examId={}, examSubjectId={}, studentId={}, classId={}, status={}, absent={}, passed={}",
				examId, examSubjectId, studentId, classId, status, absent, passed);
		ExamResultFilterRequest request = ExamResultFilterRequest.builder().examId(examId).examSubjectId(examSubjectId)
				.studentId(studentId).classId(classId).status(status).absent(absent).passed(passed).build();

		return ResponseEntity.ok(examResultService.search(request));
	}

	@Operation(summary = "Create a new exam result", description = "Creates a new exam result record using the ExamResultRequest request body.")
	@PostMapping
	public ResponseEntity<ExamResultResponse> create(@Valid @RequestBody ExamResultRequest request) {
		log.info(">> create exam result: {}", request);
		return ResponseEntity.status(HttpStatus.CREATED).body(examResultService.create(request));
	}

	@Operation(summary = "Update an exam result", description = "Updates an existing exam result by its ID using the ExamResultRequest request body.")
	@PutMapping("/{id}")
	public ResponseEntity<ExamResultResponse> update(@PathVariable Long id,
			@Valid @RequestBody ExamResultRequest request) {
		log.info(">> update exam result id={}: {}", id, request);
		return ResponseEntity.ok(examResultService.update(id, request));
	}

	@Operation(summary = "Get exam result by ID", description = "Fetches a single exam result record by its ID.")
	@GetMapping("/{id}")
	public ResponseEntity<ExamResultResponse> getById(@PathVariable Long id) {
		log.info(">> get exam result by id={}", id);
		return ResponseEntity.ok(examResultService.getById(id));
	}

	@Operation(summary = "Download exam results PDF", description = "Generates and downloads a PDF report of exam results. Pass studentId to limit the report to one student; omit it to include every student.")
	@GetMapping("/pdf")
	public ResponseEntity<byte[]> downloadResultsPdf(@RequestParam(required = false) Long studentId) {
		log.info(">> download exam results PDF for studentId={}", studentId);
		byte[] pdfBytes = examResultService.generateResultsPdf(studentId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment",
				studentId != null ? "exam-results-student-" + studentId + ".pdf" : "exam-results.pdf");
		headers.setContentLength(pdfBytes.length);

		return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}

	@Operation(summary = "Update exam result status", description = "Updates the status of an exam result by its ID.")
	@PutMapping("/{id}/status")
	public ResponseEntity<ExamResultResponse> updateStatus(@PathVariable Long id, @RequestParam ResultStatus status) {
		log.info(">> update exam result id={} status to {}", id, status);
		return ResponseEntity.ok(examResultService.updateStatus(id, status));
	}

	@Operation(summary = "Delete an exam result", description = "Deletes an exam result record by its ID.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info(">> delete exam result id={}", id);
		examResultService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
