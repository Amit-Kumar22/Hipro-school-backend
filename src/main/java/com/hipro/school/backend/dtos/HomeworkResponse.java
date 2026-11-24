package com.hipro.school.backend.dtos;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HomeworkResponse {

	private Long id;

	private Long weeklyTimetableId;

	private LocalDate homeworkDate;

	private LocalDate dueDate;

	private String className;

	private String subjectName;

	private String teacherName;

	private String dayOfWeek;

	private String periodName;

	private List<HomeworkNoteResponse> notes;

}
