package com.hipro.school.backend.entities;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "announcements")
public class Announcement extends BaseEntity {

	@NotBlank(message = "Announcement name is required")
	@Size(max = 150)
	@Column(name = "announcement_name", nullable = false, length = 150)
	private String announcementName;

	@NotBlank(message = "Description is required")
	@Size(max = 1000)
	@Column(name = "description", nullable = false, length = 1000)
	private String description;

	@NotNull(message = "Start date is required")
	@Column(name = "start_date")
	private LocalDate startDate;

	@NotNull(message = "End date is required")
	@Column(name = "end_date")
	private LocalDate endDate;

	@Valid
	@ElementCollection
	@CollectionTable(name = "announcement_notice_boards", joinColumns = @JoinColumn(name = "announcement_id"))
	private List<AnnNoticeBoard> noticeBoards;
}