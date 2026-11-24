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
@Schema(description = "Dashboard summary response with overall entity counts")
public class SummaryResponse {

	@Schema(description = "Total number of students")
	private long totalStudents;

	@Schema(description = "Total number of teachers")
	private long totalTeachers;

	@Schema(description = "Total number of classes")
	private long totalClasses;

	@Schema(description = "Total number of subjects")
	private long totalSubjects;

	@Schema(description = "Total number of active users")
	private long totalActiveUsers;

	@Schema(description = "Total number of notices")
	private long totalNotices;

	@Schema(description = "Total number of exams")
	private long totalExams;
}
