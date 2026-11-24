package com.hipro.school.backend.entities;

import java.time.LocalDateTime;

import com.hipro.school.backend.enums.NoticeAudience;
import com.hipro.school.backend.enums.NoticePriority;
import com.hipro.school.backend.enums.NoticeStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notice_boards", indexes = { @Index(name = "idx_notice_status", columnList = "status"),
		@Index(name = "idx_notice_publish_date", columnList = "publish_date"),
		@Index(name = "idx_notice_expiry_date", columnList = "expiry_date"),
		@Index(name = "idx_notice_audience", columnList = "audience") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeBoard extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id")
	private User author;

	@NotBlank
	@Size(max = 200)
	@Column(nullable = false, length = 200)
	private String title;

	@NotBlank
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private NoticeAudience audience;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Builder.Default
	private NoticePriority priority = NoticePriority.NORMAL;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Builder.Default
	private NoticeStatus status = NoticeStatus.DRAFT;

	@NotNull
	@Column(name = "publish_date", nullable = false)
	private LocalDateTime publishDate;

	@Column(name = "expiry_date")
	private LocalDateTime expiryDate;

	@Size(max = 500)
	@Column(name = "attachment_url", length = 500)
	private String attachmentUrl;

	@Size(max = 100)
	@Column(name = "attachment_name", length = 100)
	private String attachmentName;

	@Size(max = 500)
	@Column(length = 500)
	private String remarks;
}