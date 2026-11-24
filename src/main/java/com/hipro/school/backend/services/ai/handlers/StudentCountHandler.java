package com.hipro.school.backend.services.ai.handlers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.hipro.school.backend.dtos.ai.AiReportResult;
import com.hipro.school.backend.enums.AiIntent;
import com.hipro.school.backend.repositories.StudentRepository;
import com.hipro.school.backend.services.ai.AiIntentHandler;
import com.hipro.school.backend.services.ai.AiQueryContext;
import com.hipro.school.backend.services.ai.AiQuerySupport;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StudentCountHandler implements AiIntentHandler {

	private static final List<String> COLUMNS = List.of("className", "studentCount");

	private final StudentRepository studentRepository;
	private final AiQuerySupport support;

	@Override
	public AiIntent intent() {
		return AiIntent.STUDENT_COUNT;
	}

	@Override
	public AiReportResult handle(JsonNode params, AiQueryContext context) {
		Long classId = support.resolveClassId(params, context);
		String className = support.resolveClassName(classId);
		long studentCount = classId != null ? studentRepository.countBySchoolClass_Id(classId)
				: studentRepository.count();

		List<Map<String, Object>> rows = new ArrayList<>();
		Map<String, Object> entry = new LinkedHashMap<>();
		entry.put("className", className != null ? className : "All classes");
		entry.put("studentCount", studentCount);
		rows.add(entry);

		Map<String, Object> metrics = new LinkedHashMap<>();
		metrics.put("classId", classId);
		metrics.put("className", className);
		metrics.put("studentCount", studentCount);

		String title = className != null ? "Student count - " + className : "Total student count";
		return AiReportResult.builder().title(title).columns(COLUMNS).rows(rows).metrics(metrics).build();
	}
}
