package com.hipro.school.backend.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "homework")
@Getter
@Setter
public class Homework extends BaseEntity {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "time_table_id", nullable = false)
	private WeeklyTimetable weeklyTimetable;

	@OneToMany(mappedBy = "homework", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<HomeworkNote> notes = new ArrayList<>();

	private LocalDate homeworkDate;

	private LocalDate dueDate;

}