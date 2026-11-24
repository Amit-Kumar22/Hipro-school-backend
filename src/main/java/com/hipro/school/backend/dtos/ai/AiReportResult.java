package com.hipro.school.backend.dtos.ai;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiReportResult {

	private String title;

	private List<String> columns;

	private List<Map<String, Object>> rows;

	private Map<String, Object> metrics;
}
