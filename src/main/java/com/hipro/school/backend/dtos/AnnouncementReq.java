package com.hipro.school.backend.dtos;

import java.time.LocalDate;
import java.util.List;

import com.hipro.school.backend.entities.AnnNoticeBoard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementReq {

	
	private String announcementName;

	private String description;

	private LocalDate startDate;

	private LocalDate endDate;

	private List<AnnNoticeBoard> noticeBoards;
}