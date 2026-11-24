package com.hipro.school.backend.dtos;

import java.math.BigDecimal;
import java.util.List;

import com.hipro.school.backend.enums.QuestionDifficulty;
import com.hipro.school.backend.enums.QuestionType;

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
public class QuestionResponse {

    private Long id;

    private String questionText;

    private QuestionType type;

    private BigDecimal marks;

    private QuestionDifficulty difficulty;

    private Long subjectId;

    private String subjectName;

    private String modelAnswer;

    private List<QuestionOptionResponse> options;
}