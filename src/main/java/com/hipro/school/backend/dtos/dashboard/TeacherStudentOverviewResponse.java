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
@Schema(description = "Teacher's student overview for dashboard")
public class TeacherStudentOverviewResponse {

	@Schema(description = "Total students across assigned classes")
	private long totalStudents;

	@Schema(description = "Total active students")
	private long activeStudents;

	@Schema(description = "Number of assigned classes")
	private long totalClasses;
}
