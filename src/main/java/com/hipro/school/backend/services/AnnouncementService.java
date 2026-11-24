package com.hipro.school.backend.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hipro.school.backend.dtos.AnnouncementReq;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.Announcement;
import com.hipro.school.backend.repositories.AnnouncementRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AnnouncementService {

	@Autowired
	private AnnouncementRepository announcementRepository;

	public void createAnnouncement(AnnouncementReq req) {
		log.info("Creating announcement : {}", req.getAnnouncementName());
		Announcement an = new Announcement();
		Announcement announcement = maptoEntity(req, an);
		announcementRepository.save(announcement);
	}

	public void updateAnnouncement(Long id, AnnouncementReq announcement) {
		log.info("Updating announcement id : {}", id);
		Announcement existing = announcementRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Announcement not found"));
		maptoEntity(announcement, existing);
	}

	public void deleteAnnouncement(Long id) {
		log.info("Deleting announcement id : {}", id);
		Announcement announcement = announcementRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Announcement not found"));
		announcementRepository.delete(announcement);
	}

	public Announcement getAnnouncementById(Long id) {
		log.info("Fetching announcement id : {}", id);
		return announcementRepository.findById(id).orElseThrow(() -> new RuntimeException("Announcement not found"));
	}

	public PageResponse<Announcement> getAllAnnouncements(Pageable pageable) {
		log.info("Fetching all announcements");
		Page<Announcement> page = announcementRepository.findAll(pageable);
		return PageResponse.from(page);
	}

	public List<Announcement> getActiveAnnouncements() {
		LocalDate today = LocalDate.now();
		log.info("Fetching active announcements for date : {}", today);
		return announcementRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today);
	}

	private Announcement maptoEntity(AnnouncementReq req, Announcement an) {
		an.setAnnouncementName(req.getAnnouncementName());
		an.setDescription(req.getDescription());
		an.setStartDate(req.getStartDate());
		an.setEndDate(req.getEndDate());
		an.setNoticeBoards(req.getNoticeBoards());
		return an;
	}

}