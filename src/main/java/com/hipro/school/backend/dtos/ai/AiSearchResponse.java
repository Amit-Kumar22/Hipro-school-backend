package com.hipro.school.backend.dtos.ai;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiSearchResponse {

	private String question;

	private String sql;

	private String summary;

	private List<String> columns;

	private List<Map<String, Object>> data;

	private int rowCount;

	private long processingMs;
}
