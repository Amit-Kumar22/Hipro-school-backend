package com.hipro.school.backend.dtos;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "Response message", example = "Data fetched successfully")
	private String message;

	@Schema(description = "Actual response data")
	private T result;

	public static <T> ApiResponse<T> success(HttpStatus status, String message, T result) {
		return ApiResponse.<T>builder().statusCode(status.value()).message(message).result(result)
				.build();
	}

	public static <T> ApiResponse<T> failure(HttpStatus status, String message) {
		return ApiResponse.<T>builder().statusCode(status.value()).message(message).build();
	}
}