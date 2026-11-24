package com.hipro.school.backend.services.ai;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.SchoolClassRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AiQuerySupport {

	private final SchoolClassRepository schoolClassRepository;

	public Long resolveClassId(JsonNode params, AiQueryContext context) {
		if (context.getClassId() != null) {
			return context.getClassId();
		}
		if (params != null && params.hasNonNull("classId")) {
			return params.get("classId").asLong();
		}
		if (params != null && params.hasNonNull("className")) {
			String className = params.get("className").asText().trim();
			SchoolClass schoolClass = schoolClassRepository.findByClassNameIgnoreCase(className)
					.orElseThrow(() -> new ResourceNotFoundException("Class not found: " + className));
			return schoolClass.getId();
		}
		return null;
	}

	public String resolveClassName(Long classId) {
		if (classId == null) {
			return null;
		}
		return schoolClassRepository.findById(classId).map(SchoolClass::getClassName).orElse(null);
	}

	public YearMonth resolveYearMonth(JsonNode params, YearMonth defaultValue) {
		if (params == null || !params.hasNonNull("yearMonth")) {
			return defaultValue;
		}
		try {
			return YearMonth.parse(params.get("yearMonth").asText().trim());
		} catch (DateTimeParseException e) {
			return defaultValue;
		}
	}

	public LocalDate resolveDate(JsonNode params, String field, LocalDate defaultValue) {
		if (params == null || !params.hasNonNull(field)) {
			return defaultValue;
		}
		try {
			return LocalDate.parse(params.get(field).asText().trim());
		} catch (DateTimeParseException e) {
			return defaultValue;
		}
	}

	public int resolveLimit(JsonNode params, int defaultValue, int maxValue) {
		if (params == null || !params.hasNonNull("limit")) {
			return defaultValue;
		}
		int limit = params.get("limit").asInt(defaultValue);
		return Math.max(1, Math.min(limit, maxValue));
	}

	public BigDecimal decimal(Object value) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		if (value instanceof BigDecimal decimal) {
			return decimal;
		}
		if (value instanceof Double d && (d.isNaN() || d.isInfinite())) {
			return BigDecimal.ZERO;
		}
		if (value instanceof Float f && (f.isNaN() || f.isInfinite())) {
			return BigDecimal.ZERO;
		}
		if (value instanceof Number number) {
			return BigDecimal.valueOf(number.doubleValue());
		}
		try {
			return new BigDecimal(value.toString());
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO;
		}
	}

	public long longValue(Object value) {
		return value instanceof Number number ? number.longValue() : 0L;
	}
}
