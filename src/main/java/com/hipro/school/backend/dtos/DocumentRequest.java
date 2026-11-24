package com.hipro.school.backend.dtos;

import org.springframework.web.multipart.MultipartFile;

import com.hipro.school.backend.enums.DocumentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class DocumentRequest {

	@NotNull
	private DocumentType documentType;

	@NotBlank
	@Size(max = 150)
	private String documentName;

	// Optional when the file is uploaded in the same request
	private MultipartFile file;

	@Size(max = 100)
	private String documentNumber;

	@Size(max = 500)
	private String remarks;
}
