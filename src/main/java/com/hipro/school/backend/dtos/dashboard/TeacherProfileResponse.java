package com.hipro.school.backend.dtos.dashboard;

import java.util.List;

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
@Schema(description = "Teacher profile information for dashboard")
public class TeacherProfileResponse {

	@Schema(description = "Teacher ID")
	private Long teacherId;

	@Schema(description = "Teacher full name")
	private String fullName;

	@Schema(description = "Employee code")
	private String employeeCode;

	@Schema(description = "Email")
	private String email;

	@Schema(description = "Phone number")
	private String phone;

	@Schema(description = "Primary subject name")
	private String primarySubject;

	@Schema(description = "Qualification")
	private String qualification;

	@Schema(description = "Experience years")
	private java.math.BigDecimal experienceYears;

	@Schema(description = "Assigned class names")
	private List<String> assignedClassNames;
}
