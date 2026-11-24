package com.hipro.school.backend.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.HomeworkNoteResponse;
import com.hipro.school.backend.dtos.HomeworkRequest;
import com.hipro.school.backend.dtos.HomeworkResponse;
import com.hipro.school.backend.dtos.NoteRequest;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.Homework;
import com.hipro.school.backend.entities.HomeworkNote;
import com.hipro.school.backend.entities.Period;
import com.hipro.school.backend.entities.WeeklyTimetable;
import com.hipro.school.backend.enums.HomeworkStatus;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.HomeworkRepository;
import com.hipro.school.backend.repositories.WeeklyTimetableRepository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class HomeworkService {

	@Autowired
	private HomeworkRepository homeworkRepository;

	@Autowired
	private WeeklyTimetableRepository weeklyTimetableRepository;

	public PageResponse<HomeworkResponse> getAll(Pageable pageable) {
		log.info("Fetching homework list");
		Page<Homework> pages = homeworkRepository.findAll(pageable);
		return toPageResponse(pages);
	}

	public PageResponse<HomeworkResponse> getAllHomeWork(Long teacherId, Long sectionId, Long subjectId,
			LocalDate fromDate, LocalDate toDate, String email, Pageable pageable) {
		log.info("Fetching homework list with filters");
		Specification<Homework> specification = filter(teacherId, sectionId, subjectId, fromDate, toDate);
		Page<Homework> page = homeworkRepository.findAll(specification, pageable);
		return toPageResponse(page);
	}

	public PageResponse<HomeworkResponse> getAdminHomework(Long classId, Long subjectId, Long teacherId,
			DayOfWeek dayOfWeek, Long periodId, LocalDate homeworkDateFrom, LocalDate homeworkDateTo,
			LocalDate dueDateFrom, LocalDate dueDateTo, String noteStatus, String keyword, Pageable pageable) {
		log.info("Admin homework search with full filters");
		Specification<Homework> spec = adminFilter(classId, subjectId, teacherId, dayOfWeek, periodId,
				homeworkDateFrom, homeworkDateTo, dueDateFrom, dueDateTo, noteStatus, keyword);
		Page<Homework> page = homeworkRepository.findAll(spec, pageable);
		return toPageResponse(page);
	}

	public HomeworkResponse getById(Long id) {
		log.info("Fetching homework by id: {}", id);
		Homework homework = homeworkRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Homework not found with id: " + id));
		return toResponse(homework);
	}

	@Transactional
	public void create(HomeworkRequest req) {
		log.info("Creating homework for weekly timetable id: {}", req.getWeeklyTimetableId());

		WeeklyTimetable timetable = weeklyTimetableRepository.findById(req.getWeeklyTimetableId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Weekly timetable not found with id: " + req.getWeeklyTimetableId()));

		Homework homework = new Homework();
		homework.setWeeklyTimetable(timetable);
		homework.setHomeworkDate(req.getHomeworkDate());
		homework.setDueDate(req.getDueDate());

		if (req.getNotes() != null) {
			for (NoteRequest noteReq : req.getNotes()) {
				HomeworkNote note = new HomeworkNote();
				note.setQuestions(noteReq.getQuestions());
				note.setHomeworkDate(noteReq.getHomeworkDate());
				note.setHomework(homework);
				note.setStatus(HomeworkStatus.ACTIVE);
				homework.getNotes().add(note);
			}
		}

		homeworkRepository.save(homework);
		log.info("Homework created with id: {}", homework.getId());
	}

	@Transactional
	public void delete(Long id) {
		log.info("Deleting homework id: {}", id);
		Homework homework = homeworkRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Homework not found with id: " + id));
		homeworkRepository.delete(homework);
	}

	@Transactional
	public void addDailyHomework(Long homeworkId, NoteRequest request) {
		log.info("Adding daily homework note for homework id: {}", homeworkId);
		Homework homework = homeworkRepository.findById(homeworkId)
				.orElseThrow(() -> new ResourceNotFoundException("Homework not found with id: " + homeworkId));

		HomeworkNote note = new HomeworkNote();
		note.setQuestions(request.getQuestions());
		note.setHomeworkDate(request.getHomeworkDate());
		note.setHomework(homework);
		note.setStatus(HomeworkStatus.ACTIVE);
		homework.getNotes().add(note);
	}

	private PageResponse<HomeworkResponse> toPageResponse(Page<Homework> page) {
		PageResponse<HomeworkResponse> response = new PageResponse<>();
		response.setContent(page.getContent().stream().map(this::toResponse).toList());
		response.setTotalElements(page.getTotalElements());
		response.setTotalPages(page.getTotalPages());
		response.setPageNumber(page.getNumber());
		response.setPageSize(page.getSize());
		response.setLast(page.isLast());
		return response;
	}

	private HomeworkResponse toResponse(Homework homework) {
		String className = null;
		String subjectName = null;
		String teacherName = null;
		Long weeklyTimetableId = null;
		String dayOfWeek = null;
		String periodName = null;

		if (homework.getWeeklyTimetable() != null) {
			WeeklyTimetable tt = homework.getWeeklyTimetable();
			weeklyTimetableId = tt.getId();
			dayOfWeek = tt.getDayOfWeek() != null ? tt.getDayOfWeek().name() : null;

			if (tt.getPeriod() != null) {
				periodName = tt.getPeriod().getName();
				if (tt.getPeriod().getSchoolClass() != null) {
					className = tt.getPeriod().getSchoolClass().getClassName();
				}
			}
			if (tt.getSubject() != null) {
				subjectName = tt.getSubject().getSubjectName();
			}
			if (tt.getTeacher() != null && tt.getTeacher().getTeacherUser() != null) {
				teacherName = tt.getTeacher().getTeacherUser().getFullName();
			}
		}

		List<HomeworkNoteResponse> noteResponses = new ArrayList<>();
		if (homework.getNotes() != null) {
			noteResponses = homework.getNotes().stream().map(this::toNoteResponse).toList();
		}

		return HomeworkResponse.builder()
				.id(homework.getId())
				.weeklyTimetableId(weeklyTimetableId)
				.homeworkDate(homework.getHomeworkDate())
				.dueDate(homework.getDueDate())
				.className(className)
				.subjectName(subjectName)
				.teacherName(teacherName)
				.dayOfWeek(dayOfWeek)
				.periodName(periodName)
				.notes(noteResponses)
				.build();
	}

	private HomeworkNoteResponse toNoteResponse(HomeworkNote note) {
		return HomeworkNoteResponse.builder()
				.id(note.getId())
				.homeworkDate(note.getHomeworkDate())
				.questions(note.getQuestions())
				.status(note.getStatus() != null ? note.getStatus().name() : null)
				.build();
	}

	public static Specification<Homework> filter(Long teacherId, Long sectionId, Long subjectId, LocalDate fromDate,
			LocalDate toDate) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			Join<Homework, WeeklyTimetable> tt = root.join("weeklyTimetable", JoinType.INNER);

			if (teacherId != null) {
				predicates.add(cb.equal(tt.get("teacher").get("id"), teacherId));
			}

			if (sectionId != null) {
				Join<WeeklyTimetable, Period> period = tt.join("period", JoinType.INNER);
				predicates.add(cb.equal(period.get("schoolClass").get("id"), sectionId));
			}

			if (subjectId != null) {
				predicates.add(cb.equal(tt.get("subject").get("id"), subjectId));
			}

			if (fromDate != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("homeworkDate"), fromDate));
			}

			if (toDate != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("homeworkDate"), toDate));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public static Specification<Homework> adminFilter(Long classId, Long subjectId, Long teacherId,
			DayOfWeek dayOfWeek, Long periodId, LocalDate homeworkDateFrom, LocalDate homeworkDateTo,
			LocalDate dueDateFrom, LocalDate dueDateTo, String noteStatus, String keyword) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			Join<Homework, WeeklyTimetable> tt = root.join("weeklyTimetable", JoinType.INNER);

			if (teacherId != null) {
				predicates.add(cb.equal(tt.get("teacher").get("id"), teacherId));
			}

			if (classId != null) {
				Join<WeeklyTimetable, Period> period = tt.join("period", JoinType.INNER);
				predicates.add(cb.equal(period.get("schoolClass").get("id"), classId));
			}

			if (subjectId != null) {
				predicates.add(cb.equal(tt.get("subject").get("id"), subjectId));
			}

			if (dayOfWeek != null) {
				predicates.add(cb.equal(tt.get("dayOfWeek"), dayOfWeek));
			}

			if (periodId != null) {
				predicates.add(cb.equal(tt.get("period").get("id"), periodId));
			}

			if (homeworkDateFrom != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("homeworkDate"), homeworkDateFrom));
			}

			if (homeworkDateTo != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("homeworkDate"), homeworkDateTo));
			}

			if (dueDateFrom != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), dueDateFrom));
			}

			if (dueDateTo != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), dueDateTo));
			}

			if (noteStatus != null) {
				try {
					HomeworkStatus status = HomeworkStatus.valueOf(noteStatus.toUpperCase());
					Join<Homework, HomeworkNote> notes = root.join("notes", JoinType.INNER);
					predicates.add(cb.equal(notes.get("status"), status));
				} catch (IllegalArgumentException e) {
					log.warn("Invalid noteStatus: {}", noteStatus);
				}
			}

			if (keyword != null && !keyword.isBlank()) {
				String pattern = "%" + keyword.toLowerCase() + "%";
				Join<Homework, HomeworkNote> notes = root.join("notes", JoinType.INNER);
				Join<HomeworkNote, String> questions = notes.join("questions", JoinType.INNER);
				Predicate keywordMatch = cb.like(cb.lower(questions), pattern);
				predicates.add(keywordMatch);
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
