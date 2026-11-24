package com.hipro.school.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.NoticeBoardRequest;
import com.hipro.school.backend.dtos.NoticeBoardResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.NoticeBoard;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.enums.NoticeAudience;
import com.hipro.school.backend.enums.NoticeStatus;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.NoticeBoardRepository;
import com.hipro.school.backend.repositories.UserRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NoticeBoardService {

	private final NoticeBoardRepository noticeBoardRepository;

	private final UserRepository userRepository;

	private final ModelMapper modelMapper;

	public NoticeBoardResponse create(String email, NoticeBoardRequest request) {
		log.info("Creating notice board with title: {}");
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));
		validateDates(request);
		NoticeBoard notice = mapToEntity(request, null);
		notice.setAuthor(user);
		NoticeBoard saved = noticeBoardRepository.save(notice);
		log.info("Notice board created successfully with id: {}", saved.getId());
		return mapToResponse(saved);
	}

	@Transactional(readOnly = true)
	public PageResponse<NoticeBoardResponse> getAll(NoticeAudience audience, NoticeStatus status, Long userId,
			Pageable pageable) {
		log.info("Fetching notices. audience: {}, status: {}, pageable: {}", audience, status, pageable);
		Specification<NoticeBoard> specification = byFilters(audience, status, userId);
		Page<NoticeBoard> page = noticeBoardRepository.findAll(specification, pageable);
		return PageResponse.from(page.map(this::mapToResponse));
	}

	@Transactional(readOnly = true)
	public NoticeBoardResponse getById(Long id) {
		log.info("Fetching notice board by id: {}", id);
		return mapToResponse(findNotice(id));
	}

	public NoticeBoardResponse update(Long id, NoticeBoardRequest request) {
		log.info("Updating notice board id: {}", id);
		NoticeBoard notice = findNotice(id);
		validateDates(request);
		mapToEntity(request, notice);
		NoticeBoard updated = noticeBoardRepository.save(notice);
		log.info("Notice board updated successfully with id: {}", updated.getId());
		return mapToResponse(updated);
	}

	public void delete(Long id) {
		log.info("Deleting notice board id: {}", id);
		NoticeBoard notice = findNotice(id);
		noticeBoardRepository.delete(notice);
		log.info("Notice board deleted successfully with id: {}", id);
	}

	private NoticeBoard mapToEntity(NoticeBoardRequest request, NoticeBoard notice) {
		if (notice == null) {
			notice = new NoticeBoard();
		}

		notice.setTitle(request.getTitle());
		notice.setContent(request.getContent());
		notice.setAudience(request.getAudience());
		notice.setPriority(request.getPriority());
		notice.setStatus(request.getStatus());
		notice.setPublishDate(request.getPublishDate());
		notice.setExpiryDate(request.getExpiryDate());
		notice.setAttachmentUrl(request.getAttachmentUrl());
		notice.setAttachmentName(request.getAttachmentName());
		notice.setRemarks(request.getRemarks());

		return notice;
	}

	private void validateDates(NoticeBoardRequest request) {
		if (request.getExpiryDate() != null && request.getPublishDate().isAfter(request.getExpiryDate())) {
			throw new ServiceException("Expiry date must be after publish date");
		}
		if (request.getStatus() == NoticeStatus.PUBLISHED && request.getPublishDate() == null) {
			throw new ServiceException("Publish date is required for published notice");
		}
	}

	private NoticeBoard findNotice(Long id) {
		return noticeBoardRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Notice board not found: " + id));
	}

	private NoticeBoardResponse mapToResponse(NoticeBoard notice) {
		return modelMapper.map(notice, NoticeBoardResponse.class);
	}

	private Specification<NoticeBoard> byFilters(NoticeAudience audience, NoticeStatus status, Long userId) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (audience != null) {
				predicates.add(cb.equal(root.get("audience"), audience));
			}
			if (status != null) {
				predicates.add(cb.equal(root.get("status"), status));
			}
			if (userId != null) {
				predicates.add(cb.equal(root.get("author").get("id"), userId));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}