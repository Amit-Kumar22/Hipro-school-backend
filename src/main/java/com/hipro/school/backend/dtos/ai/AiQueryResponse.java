package com.hipro.school.backend.dtos.ai;

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
public class AiQueryResponse {

	private String question;

	private String intent;

	private double confidence;

	private String answer;

	private AiReportResult report;

	private long processingMs;
}
