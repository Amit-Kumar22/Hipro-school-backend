package com.hipro.school.backend.entities;

import java.time.LocalDateTime;

import com.hipro.school.backend.enums.DocumentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "documents", indexes = { @Index(name = "idx_document_user", columnList = "user_id"),
		@Index(name = "idx_person_document_type", columnList = "document_type") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "document_type", nullable = false, length = 50)
	private DocumentType documentType;

	@Column(name = "document_name", nullable = false, length = 150)
	private String documentName;

	@Column(name = "file_url", nullable = false, length = 500)
	private String fileUrl;

	@Column(name = "document_number", length = 100)
	private String documentNumber;

	@Builder.Default
	@Column(name = "is_verified", nullable = false)
	private Boolean verified = false;

	@Column(name = "verified_by")
	private Long verifiedBy;

	@Column(name = "verified_at")
	private LocalDateTime verifiedAt;

	@Column(name = "remarks", length = 500)
	private String remarks;

}