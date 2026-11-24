package com.hipro.school.backend.dtos;

import java.time.LocalDateTime;

import com.hipro.school.backend.entities.Document;
import com.hipro.school.backend.enums.DocumentType;

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
public class DocumentResponse {

	private Long id;

	private Long userId;

	private String userFullName;

	private String userEmail;

	private DocumentType documentType;

	private String documentName;

	private String fileUrl;

	private String documentNumber;

	private Boolean verified;

	private Long verifiedBy;

	private LocalDateTime verifiedAt;

	private String remarks;

	private LocalDateTime created;

	private LocalDateTime updated;

	public static DocumentResponse from(Document document) {
		return DocumentResponse.builder().id(document.getId())
				.userId(document.getUser() != null ? document.getUser().getId() : null)
				.userFullName(document.getUser() != null ? document.getUser().getFullName() : null)
				.userEmail(document.getUser() != null ? document.getUser().getEmail() : null)
				.documentType(document.getDocumentType()).documentName(document.getDocumentName())
				.fileUrl(document.getFileUrl())
				.documentNumber(document.getDocumentNumber()).verified(document.getVerified())
				.verifiedBy(document.getVerifiedBy()).verifiedAt(document.getVerifiedAt())
				.remarks(document.getRemarks()).created(document.getCreated()).updated(document.getUpdated()).build();
	}
}
