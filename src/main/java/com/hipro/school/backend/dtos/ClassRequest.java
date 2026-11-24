package com.hipro.school.backend.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ClassRequest {

	@NotEmpty(message = "Class name should not be empty")
	private String className;

}