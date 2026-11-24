package com.hipro.school.backend.controllers.transport;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.StudentFeeResponse;
import com.hipro.school.backend.dtos.transport.TransportFeeGenerateRequest;
import com.hipro.school.backend.services.transport.TransportFeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Transport Fee APIs", description = "APIs integrating transport charges with student fee management. Payments reuse the existing fee collection endpoints.")
@RestController
@RequestMapping("/v1/transport/fees")
@RequiredArgsConstructor
@Slf4j
public class TransportFeeController {

	private final TransportFeeService feeService;

	@Operation(summary = "Generate transport fee", description = "Creates a TRANSPORT_FEE student fee from the route's transport fee. Further collection and receipts reuse the existing /v1/student-fees payment APIs.")
	@PostMapping("/generate")
	public ResponseEntity<StudentFeeResponse> generate(@Valid @RequestBody TransportFeeGenerateRequest request) {
		log.info("Generating transport fee for student {}", request.getStudentId());
		return ResponseEntity.status(HttpStatus.CREATED).body(feeService.generateTransportFee(request));
	}

	@Operation(summary = "Outstanding transport fees", description = "Lists pending, partial and overdue TRANSPORT_FEE dues with optional student filter.")
	@GetMapping("/outstanding")
	public ResponseEntity<PageResponse<StudentFeeResponse>> getOutstanding(
			@RequestParam(required = false) Long studentId, Pageable pageable) {
		return ResponseEntity.ok(feeService.getOutstandingTransportFees(studentId, pageable));
	}
}
