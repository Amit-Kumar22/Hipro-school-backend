package com.hipro.school.backend.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.hipro.school.backend.enums.QuestionDifficulty;
import com.hipro.school.backend.enums.QuestionType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question extends BaseEntity {

	@Column(nullable = false, columnDefinition = "TEXT")
	private String questionText;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private QuestionType type;

	@Column(precision = 10, scale = 2)
	private BigDecimal marks;

	@Enumerated(EnumType.STRING)
	private QuestionDifficulty difficulty;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subject_id")
	private Subject subject;

	@Column(columnDefinition = "TEXT")
	private String modelAnswer;

//	@Builder.Default
//	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<QuestionOption> options = new ArrayList<>();

}