package com.hipro.school.backend.entities;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hipro.school.backend.enums.HomeworkStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "homework_notes", indexes = { @Index(name = "idx_notes_homework", columnList = "homework_id") })
@Getter
@Setter
public class HomeworkNote extends BaseEntity {

	// Relation
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "homework_id", nullable = false)
	@JsonIgnore
	private Homework homework;

	@Column(name = "homework_date", nullable = false)
	private LocalDate homeworkDate;

	private List<String> questions;

	@Enumerated(EnumType.STRING)
	private HomeworkStatus status;
}