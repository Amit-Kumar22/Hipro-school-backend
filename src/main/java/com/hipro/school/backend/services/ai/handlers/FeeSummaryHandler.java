package com.hipro.school.backend.services.ai.handlers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.hipro.school.backend.dtos.ai.AiReportResult;
import com.hipro.school.backend.enums.AiIntent;
import com.hipro.school.backend.repositories.StudentFeeRepository;
import com.hipro.school.backend.services.ai.AiIntentHandler;
import com.hipro.school.backend.services.ai.AiQueryContext;
import com.hipro.school.backend.services.ai.AiQuerySupport;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FeeSummaryHandler implements AiIntentHandler {

	private static final List<String> COLUMNS = List.of("status", "count");

	private final StudentFeeRepository studentFeeRepository;
	private final AiQuerySupport support;

	@Override
	public AiIntent intent() {
		return AiIntent.FEE_SUMMARY;
	}

	@Override
	public AiReportResult handle(JsonNode params, AiQueryContext context) {
		Object[] totals = studentFeeRepository.sumAmountsAggregated();

		List<Map<String, Object>> rows = new ArrayList<>();
		List<Object[]> grouped = studentFeeRepository.countGroupByStatus();
		for (Object[] row : grouped) {
			Map<String, Object> entry = new LinkedHashMap<>();
			entry.put("status", row[0] != null ? row[0].toString() : null);
			entry.put("count", support.longValue(row[1]));
			rows.add(entry);
		}

		Map<String, Object> metrics = new LinkedHashMap<>();
		metrics.put("totalAmount", totals != null ? support.decimal(totals[0]) : BigDecimal.ZERO);
		metrics.put("totalPaidAmount", totals != null ? support.decimal(totals[1]) : BigDecimal.ZERO);
		metrics.put("totalDueAmount", totals != null ? support.decimal(totals[2]) : BigDecimal.ZERO);

		return AiReportResult.builder().title("Overall fee summary").columns(COLUMNS).rows(rows).metrics(metrics)
				.build();
	}
}
