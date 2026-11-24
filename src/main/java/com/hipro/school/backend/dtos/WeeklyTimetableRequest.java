package com.hipro.school.backend.dtos;

import java.time.DayOfWeek;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class WeeklyTimetableRequest {

	@NotNull
	private Long periodId;

	@NotNull
	private DayOfWeek dayOfWeek;

	private Long subjectId;

	private Long teacherId;
	
	private String otherTeacherName;

	@Size(max = 500)
	private String remarks;
}