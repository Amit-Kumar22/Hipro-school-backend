package com.hipro.school.backend.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class ExamSubjectResponse {

    private Long id;

    private Long subjectId;

    private String subjectName;

    private LocalDateTime examDate;

    private Integer durationMinutes;

    private BigDecimal totalMarks;

    private BigDecimal passingMarks;
}