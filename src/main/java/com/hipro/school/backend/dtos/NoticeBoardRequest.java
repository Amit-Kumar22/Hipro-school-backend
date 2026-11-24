package com.hipro.school.backend.dtos;

import java.time.LocalDateTime;

import com.hipro.school.backend.enums.NoticeAudience;
import com.hipro.school.backend.enums.NoticePriority;
import com.hipro.school.backend.enums.NoticeStatus;

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
public class NoticeBoardRequest {

	@NotBlank
	@Size(max = 200)
	private String title;

	@NotBlank
	private String content;

	@NotNull
	private NoticeAudience audience;

	@NotNull
	private NoticePriority priority;

	@NotNull
	private NoticeStatus status;

	@NotNull
	private LocalDateTime publishDate;

	private LocalDateTime expiryDate;

	@Size(max = 500)
	private String attachmentUrl;

	@Size(max = 100)
	private String attachmentName;

	@Size(max = 500)
	private String remarks;
}