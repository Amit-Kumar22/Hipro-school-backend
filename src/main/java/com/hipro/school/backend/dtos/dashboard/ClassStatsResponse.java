package com.hipro.school.backend.dtos.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Class-wise statistics for the dashboard")
public class ClassStatsResponse {

	@Schema(description = "Class ID")
	private Long classId;

	@Schema(description = "Class name")
	private String className;

	@Schema(description = "Number of students in the class")
	private long studentCount;

	@Schema(description = "Number of teachers assigned to the class")
	private long teacherCount;

	@Schema(description = "Number of fee structures for the class")
	private long feeStructureCount;
}
