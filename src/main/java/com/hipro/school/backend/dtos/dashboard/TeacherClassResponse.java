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
@Schema(description = "Teacher's assigned class information")
public class TeacherClassResponse {

	@Schema(description = "Class ID")
	private Long classId;

	@Schema(description = "Class name")
	private String className;

	@Schema(description = "Number of students in the class")
	private long studentCount;

	@Schema(description = "Whether teacher is class teacher of this class")
	private Boolean isClassTeacher;
}
