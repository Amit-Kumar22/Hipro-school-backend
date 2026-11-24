package com.hipro.school.backend.entities;

import java.time.DayOfWeek;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "weekly_timetables", uniqueConstraints = {
		@UniqueConstraint(name = "uk_timetable_class_section_day_period", columnNames = { "day_of_week",
				"period_id" }) }, indexes = {
						@Index(name = "idx_timetable_teacher_day", columnList = "teacher_id, day_of_week"),
						@Index(name = "idx_timetable_period", columnList = "period_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyTimetable extends BaseEntity {

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "period_id", nullable = false)
	private Period period;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "day_of_week", nullable = false, length = 15)
	private DayOfWeek dayOfWeek;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subject_id")
	private Subject subject;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher_id")
	private Teacher teacher;

	@Column(name = "other_teacher_name", length = 50)
	private String otherTeacherName;
	
	@Size(max = 500)
	@Column(length = 500)
	private String remarks;
}