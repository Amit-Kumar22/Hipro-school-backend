package com.hipro.school.backend.dtos;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeadUploadResponse {
	private int totalRecords;
	private int successCount;
	private int failureCount;
	private List<String> errors;
}