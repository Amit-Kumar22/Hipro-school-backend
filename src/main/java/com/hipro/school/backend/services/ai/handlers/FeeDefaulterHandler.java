package com.hipro.school.backend.services.ai.handlers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.hipro.school.backend.dtos.ai.AiReportResult;
import com.hipro.school.backend.entities.StudentFee;
import com.hipro.school.backend.enums.AiIntent;
import com.hipro.school.backend.enums.FeeStatus;
import com.hipro.school.backend.repositories.StudentFeeRepository;
import com.hipro.school.backend.services.ai.AiIntentHandler;
import com.hipro.school.backend.services.ai.AiQueryContext;
import com.hipro.school.backend.services.ai.AiQuerySupport;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FeeDefaulterHandler implements AiIntentHandler {

	private static final List<String> COLUMNS = List.of("studentName", "className", "dueAmount", "dueDate", "status");

	private static final int DEFAULT_LIMIT = 50;
	private static final int MAX_LIMIT = 200;

	private final StudentFeeRepository studentFeeRepository;
	private final AiQuerySupport support;

	@Override
	public AiIntent intent() {
		return AiIntent.FEE_DEFAULTERS;
	}

	@Override
	public AiReportResult handle(JsonNode params, AiQueryContext context) {
		int limit = support.resolveLimit(params, DEFAULT_LIMIT, MAX_LIMIT);
		Long classId = support.resolveClassId(params, context);
		String className = support.resolveClassName(classId);

		List<StudentFee> defaulters = studentFeeRepository.findDefaulterFees(
				List.of(FeeStatus.PENDING, FeeStatus.PARTIAL, FeeStatus.OVERDUE), context.getToday(), classId,
				PageRequest.of(0, limit));

		List<Map<String, Object>> rows = new ArrayList<>();
		for (StudentFee fee : defaulters) {
			Map<String, Object> entry = new LinkedHashMap<>();
			entry.put("studentName",
					fee.getStudent() != null && fee.getStudent().getStudentUser() != null
							? fee.getStudent().getStudentUser().getFullName()
							: null);
			entry.put("className", fee.getStudent() != null && fee.getStudent().getSchoolClass() != null
					? fee.getStudent().getSchoolClass().getClassName()
					: null);
			entry.put("dueAmount", support.decimal(fee.getDueAmount()));
			entry.put("dueDate", fee.getDueDate() != null ? fee.getDueDate().toString() : null);
			entry.put("status", fee.getStatus() != null ? fee.getStatus().name() : null);
			rows.add(entry);
		}

		Map<String, Object> metrics = new LinkedHashMap<>();
		metrics.put("classId", classId);
		metrics.put("className", className);
		metrics.put("defaultingStudents", (long) rows.size());
		metrics.put("totalDueAmount", rows.stream().map(row -> (BigDecimal) row.get("dueAmount"))
				.reduce(BigDecimal.ZERO, BigDecimal::add));
		metrics.put("asOn", context.getToday().toString());

		return AiReportResult.builder().title("Fee defaulters" + (className != null ? " - " + className : ""))
				.columns(COLUMNS).rows(rows).metrics(metrics).build();
	}
}
