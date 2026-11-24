package com.hipro.school.backend.services.ai.handlers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.hipro.school.backend.dtos.ai.AiReportResult;
import com.hipro.school.backend.enums.AiIntent;
import com.hipro.school.backend.repositories.AttendanceRepository;
import com.hipro.school.backend.services.ai.AiIntentHandler;
import com.hipro.school.backend.services.ai.AiQueryContext;
import com.hipro.school.backend.services.ai.AiQuerySupport;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AttendanceReportHandler implements AiIntentHandler {

	private static final List<String> COLUMNS = List.of("status", "count");

	private final AttendanceRepository attendanceRepository;
	private final AiQuerySupport support;

	@Override
	public AiIntent intent() {
		return AiIntent.ATTENDANCE_REPORT;
	}

	@Override
	public AiReportResult handle(JsonNode params, AiQueryContext context) {
		LocalDate date = support.resolveDate(params, "date", context.getToday());

		List<Map<String, Object>> rows = new ArrayList<>();
		long total = 0;
		List<Object[]> grouped = attendanceRepository.countGroupByStatusForDate(date);
		for (Object[] row : grouped) {
			Map<String, Object> entry = new LinkedHashMap<>();
			entry.put("status", row[0] != null ? row[0].toString() : null);
			entry.put("count", support.longValue(row[1]));
			rows.add(entry);
			total += support.longValue(row[1]);
		}

		Map<String, Object> metrics = new LinkedHashMap<>();
		metrics.put("date", date.toString());
		metrics.put("totalRecords", total);

		return AiReportResult.builder().title("Attendance report for " + date).columns(COLUMNS).rows(rows)
				.metrics(metrics).build();
	}
}
