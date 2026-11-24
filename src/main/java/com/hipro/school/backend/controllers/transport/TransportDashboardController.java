package com.hipro.school.backend.controllers.transport;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.transport.TransportDashboardResponse;
import com.hipro.school.backend.services.transport.TransportDashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Transport Dashboard APIs", description = "Aggregated transport metrics and reports")
@RestController
@RequestMapping("/v1/transport/dashboard")
@RequiredArgsConstructor
public class TransportDashboardController {

	private final TransportDashboardService dashboardService;

	@Operation(summary = "Transport dashboard", description = "Returns total vehicles, active drivers, active routes, transport students, monthly salary and vehicle expenses, document expiries and outstanding transport fees.")
	@GetMapping
	public ResponseEntity<TransportDashboardResponse> getDashboard(
			@RequestParam(required = false, defaultValue = "30") Integer expiryWithinDays) {
		return ResponseEntity.ok(dashboardService.getDashboard(expiryWithinDays));
	}
}
