package com.hipro.school.backend.dtos;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeworkRequest {

	private Long weeklyTimetableId;

	private List<NoteRequest> notes;

	private LocalDate homeworkDate;

	private LocalDate dueDate;
}
