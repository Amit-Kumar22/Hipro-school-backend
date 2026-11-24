package com.hipro.school.backend.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "teacher_subject_section")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherSubjectSection extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "teacher_id")
	private User teacher;

	@ManyToOne
	@JoinColumn(name = "class_id")
	private SchoolClass schoolClass;

	@ManyToOne
	@JoinColumn(name = "subject_id")
	private Subject subject;

}