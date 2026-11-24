package com.hipro.school.backend.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.FeeGenerateRequest;
import com.hipro.school.backend.dtos.FeePaymentRequest;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.PaymentResponse;
import com.hipro.school.backend.dtos.StudentFeeResponse;
import com.hipro.school.backend.enums.FeeStatus;
import com.hipro.school.backend.enums.PaymentStatus;
import com.hipro.school.backend.services.StudentFeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Student Fee APIs", description = "APIs for generating student fees, recording payments and tracking dues")
@RestController
@RequestMapping("/v1/student-fees")
@RequiredArgsConstructor
@Slf4j
public class StudentFeeController {

	private static final MediaType EXCEL_MEDIA_TYPE = MediaType
			.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

	private final StudentFeeService studentFeeService;

	@Operation(summary = "Get student fees", description = "Fetches a paginated list of student fees. Requires the schoolCode query param and supports optional classId, studentId and status filtering via query params.")
	@GetMapping
	public ResponseEntity<PageResponse<StudentFeeResponse>> getStudentFees(@RequestParam(required = false) Long classId,
			@RequestParam(required = false) Long studentId, @RequestParam(required = false) FeeStatus status,
			Pageable pageable) {
		log.info("Fetching student fees for school code: {}, class id: {}, student id: {}, status: {} pageable: {}",
				classId, studentId, status, pageable);
		return ResponseEntity.ok(studentFeeService.getStudentFees(classId, studentId, status, pageable));
	}

	@Operation(summary = "Download student fees excel", description = "Generates and downloads an Excel file of student fees. Supports optional classId, studentId and status filtering via query params.")
	@GetMapping("/excel")
	public ResponseEntity<byte[]> downloadExcel(@RequestParam(required = false) Long classId,
			@RequestParam(required = false) Long studentId, @RequestParam(required = false) FeeStatus status) {
		log.info("Downloading student fees excel for class id: {}, student id: {}, status: {}", classId, studentId,
				status);
		byte[] excelBytes = studentFeeService.exportToExcel(classId, studentId, status);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(EXCEL_MEDIA_TYPE);
		headers.setContentDispositionFormData("attachment", "student-fees.xlsx");
		headers.setContentLength(excelBytes.length);

		return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
	}

	@Operation(summary = "Get a student fee by id", description = "Fetches a single student fee with its amounts, due date and status by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<StudentFeeResponse> getById(@PathVariable Long id) {
		log.info("Fetching student fee by id: {}", id);
		return ResponseEntity.ok(studentFeeService.getById(id));
	}

	@Operation(summary = "Get a student fee by id", description = "Fetches a single student fee with its amounts, due date and status by its unique id path parameter.")
	@GetMapping("/daily-fees")
	public ResponseEntity<PageResponse<PaymentResponse>> getDailyFees(@RequestParam LocalDate today,
			Pageable pageable) {
		log.info("Fetching student fee by id: {}", today, pageable);
		return ResponseEntity.ok(studentFeeService.getDailyFees(today, pageable));
	}

	@Operation(summary = "Generate student fees", description = "Bulk generates student fees for every active student of the given class from the fee structures configured for the academic year. Existing student-fee rows are skipped. Returns a summary of generated and skipped counts.")
	@PostMapping("/generate")
	public ResponseEntity<String> generateFees(@Valid @RequestBody FeeGenerateRequest request) {
		log.info("Generating student fees: {}", request);
		studentFeeService.generateFees(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("Generate student fees");
	}

	@Operation(summary = "Record a payment", description = "Records an amount paid against a student fee, updates paid/due amounts and status (PENDING/PARTIAL/PAID) and generates a payment receipt with a unique receipt number. Rejects overpayment and payments against fully paid fees.")
	@PostMapping("/{id}/payments")
	public ResponseEntity<PaymentResponse> recordPayment(@PathVariable Long id,
			@Valid @RequestBody FeePaymentRequest request) {
		log.info("Recording payment for student fee id: {} with request: {}", id, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(studentFeeService.recordPayment(id, request));
	}

	@Operation(summary = "Get payment history", description = "Fetches all payment receipts recorded against a student fee, newest first.")
	@GetMapping("/{id}/payments")
	public ResponseEntity<List<PaymentResponse>> getPayments(@PathVariable Long id) {
		log.info("Fetching payments for student fee id: {}", id);
		return ResponseEntity.ok(studentFeeService.getPayments(id));
	}

	@Operation(summary = "Get payment history", description = "Fetches all payment receipts recorded against a student fee, newest first.")
	@GetMapping("/all-payment")
	public ResponseEntity<PageResponse<PaymentResponse>> getAllPayment(@RequestParam(required = false) Long studentId,
			@RequestParam(required = false) Long classId, @RequestParam(required = false) LocalDate feeDate,
			@RequestParam(required = false) PaymentStatus paymentStatus, Pageable pageable) {
		log.info("Fetching payments for student fee id: {}", studentId);
		return ResponseEntity.ok(studentFeeService.getAllPayment(studentId, classId, feeDate, paymentStatus, pageable));
	}

	@Operation(summary = "Mark overdue fees", description = "Flags all PENDING or PARTIAL student fees whose due date is in the past as OVERDUE. Intended to be triggered by a scheduler or an admin action. Returns the number of fees marked overdue.")
	@PostMapping("/overdue/mark")
	public ResponseEntity<Long> markOverdue() {
		log.info("Marking overdue student fees");
		return ResponseEntity.ok((long) studentFeeService.markOverdue());
	}

	@Operation(summary = "Delete a student fee", description = "Deletes a student fee by its id path parameter. Deletion is blocked when payments have been recorded against it.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info("Deleting student fee id: {}", id);
		studentFeeService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
