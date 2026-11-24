package com.hipro.school.backend.dtos.dashboard;

import java.time.LocalDateTime;

import com.hipro.school.backend.enums.NoticeAudience;
import com.hipro.school.backend.enums.NoticePriority;
import com.hipro.school.backend.enums.NoticeStatus;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Recent notice for the dashboard")
public class RecentNoticeResponse {

	@Schema(description = "Notice ID")
	private Long id;

	@Schema(description = "Notice title")
	private String title;

	@Schema(description = "Notice content")
	private String content;

	@Schema(description = "Target audience")
	private NoticeAudience audience;

	@Schema(description = "Notice priority")
	private NoticePriority priority;

	@Schema(description = "Notice status")
	private NoticeStatus status;

	@Schema(description = "Author name")
	private String authorName;

	@Schema(description = "Publish date")
	private LocalDateTime publishDate;

	@Schema(description = "Expiry date")
	private LocalDateTime expiryDate;
}
