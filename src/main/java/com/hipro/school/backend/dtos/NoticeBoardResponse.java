package com.hipro.school.backend.dtos;

import java.time.LocalDateTime;

import com.hipro.school.backend.enums.NoticeAudience;
import com.hipro.school.backend.enums.NoticePriority;
import com.hipro.school.backend.enums.NoticeStatus;

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
public class NoticeBoardResponse {

	private Long id;

	private String title;

	private String content;

	private NoticeAudience audience;

	private NoticePriority priority;

	private NoticeStatus status;

	private LocalDateTime publishDate;

	private LocalDateTime expiryDate;

	private String attachmentUrl;

	private String attachmentName;

	private String remarks;

	private UserResponse author;

	private LocalDateTime createdAt;
}