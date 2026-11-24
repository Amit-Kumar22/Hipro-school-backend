package com.hipro.school.backend.dtos;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
public class WeeklyTimetableResponse {

	private Long id;

	private Long classId;

	private String className;

	private Long periodId;

	private String periodName;

	private Integer periodOrder;

	private LocalTime startTime;

	private LocalTime endTime;

	private Boolean breakPeriod;

	private DayOfWeek dayOfWeek;

	private Long subjectId;

	private String subjectName;
	
	private String otherTeacherName;

	private Long teacherId;

	private String teacherName;

	private String remarks;

	private LocalDateTime createdAt;
}