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
@Schema(description = "Timetable entry for dashboard")
public class TimetableEntryResponse {

	@Schema(description = "Period name")
	private String periodName;

	@Schema(description = "Subject name")
	private String subjectName;

	@Schema(description = "Teacher name")
	private String teacherName;

	@Schema(description = "Start time")
	private String startTime;

	@Schema(description = "End time")
	private String endTime;

	@Schema(description = "Day of week")
	private String dayOfWeek;

	@Schema(description = "Whether it's a break period")
	private Boolean breakPeriod;
}
