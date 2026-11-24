package com.hipro.school.backend.dtos;

import java.math.BigDecimal;

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
public class ExamQuestionResponse {

	private Long id;

	private Long examId;

	private String examName;

	private QuestionResponse question;

	private Integer questionOrder;

	private BigDecimal marks;
}
