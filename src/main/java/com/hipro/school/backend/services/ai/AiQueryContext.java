package com.hipro.school.backend.services.ai;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiQueryContext {

	private final Long classId;

	private final LocalDate today;
}
