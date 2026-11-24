package com.hipro.school.backend.services.ai.handlers;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
public class FeeReportHandler implements AiIntentHandler {

	private static final List<String> COLUMNS = List.of("status", "count", "amount", "paidAmount", "dueAmount");

	private final StudentFeeRepository studentFeeRepository;
	private final AiQuerySupport support;

	@Override
	public AiIntent intent() {
		return AiIntent.FEE_REPORT;
	}

	@Override
	public AiReportResult handle(JsonNode params, AiQueryContext context) {
		YearMonth month = support.resolveYearMonth(params, YearMonth.from(context.getToday()));
		Long classId = support.resolveClassId(params, context);
		String className = support.resolveClassName(classId);

		Object[] totals = studentFeeRepository.sumAmountsForPeriod(classId, month.atDay(1), month.atEndOfMonth());
		BigDecimal amount = totals != null ? support.decimal(totals[0]) : BigDecimal.ZERO;
		BigDecimal paid = totals != null ? support.decimal(totals[1]) : BigDecimal.ZERO;
		BigDecimal due = totals != null ? support.decimal(totals[2]) : BigDecimal.ZERO;
		long records = totals != null ? support.longValue(totals[3]) : 0L;

		List<Map<String, Object>> rows = new ArrayList<>();
		List<Object[]> grouped = studentFeeRepository.sumGroupedByStatusForPeriod(classId, month.atDay(1),
				month.atEndOfMonth());
		for (Object[] row : grouped) {
			Map<String, Object> entry = new LinkedHashMap<>();
			entry.put("status", row[0] != null ? row[0].toString() : null);
			entry.put("count", support.longValue(row[1]));
			entry.put("amount", support.decimal(row[2]));
			entry.put("paidAmount", support.decimal(row[3]));
			entry.put("dueAmount", support.decimal(row[4]));
			rows.add(entry);
		}

		String monthLabel = month.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + month.getYear();
		Map<String, Object> metrics = new LinkedHashMap<>();
		metrics.put("period", month.toString());
		metrics.put("classId", classId);
		metrics.put("className", className);
		metrics.put("totalRecords", records);
		metrics.put("totalAmount", amount);
		metrics.put("totalPaidAmount", paid);
		metrics.put("totalDueAmount", due);

		String title = "Fee report for " + monthLabel + (className != null ? " - " + className : "");
		return AiReportResult.builder().title(title).columns(COLUMNS).rows(rows).metrics(metrics).build();
	}
}
