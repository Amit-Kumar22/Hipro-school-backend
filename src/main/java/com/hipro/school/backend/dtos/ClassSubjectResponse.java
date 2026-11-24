package com.hipro.school.backend.dtos;

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
public class ClassSubjectResponse {

    private Long id;

    private Long classId;

    private String className;

    private Long subjectId;

    private String subjectName;
}