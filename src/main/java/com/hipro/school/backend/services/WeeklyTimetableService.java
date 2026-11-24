package com.hipro.school.backend.services;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.WeeklyTimetableRequest;
import com.hipro.school.backend.dtos.WeeklyTimetableResponse;
import com.hipro.school.backend.entities.Period;
import com.hipro.school.backend.entities.Subject;
import com.hipro.school.backend.entities.Teacher;
import com.hipro.school.backend.entities.WeeklyTimetable;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.PeriodRepository;
import com.hipro.school.backend.repositories.SubjectRepository;
import com.hipro.school.backend.repositories.TeacherRepository;
import com.hipro.school.backend.repositories.WeeklyTimetableRepository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WeeklyTimetableService {

	private final WeeklyTimetableRepository timetableRepository;
	private final PeriodRepository periodRepository;
	private final SubjectRepository subjectRepository;
	private final TeacherRepository teacherRepository;

	public WeeklyTimetableResponse create(WeeklyTimetableRequest request) {
		log.info("Creating timetable for period id: {} and day: {}", request.getPeriodId(), request.getDayOfWeek());

		Period period = findPeriod(request.getPeriodId());
		validatePeriodAssignment(request.getPeriodId(), request.getDayOfWeek(), null);
		Subject subject = null;
		Teacher teacher = null;

		// Break periods don't need subject or teacher.
		if (!Boolean.TRUE.equals(period.getBreakPeriod())) {
			if (request.getSubjectId() == null) {
				throw new ServiceException("Subject is required for a non-break period");
			}
			if (request.getTeacherId() == null) {
				throw new ServiceException("Teacher is required for a non-break period");
			}
			subject = findSubject(request.getSubjectId());
			teacher = findTeacher(request.getTeacherId());
		}

		WeeklyTimetable timetable = mapToEntity(request, period, subject, teacher, null);
		WeeklyTimetable saved = timetableRepository.save(timetable);
		log.info("Weekly timetable created successfully with id: {}", saved.getId());
		return mapToResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<WeeklyTimetableResponse> getAll(Long classId, DayOfWeek dayOfWeek) {
		log.info("Fetching timetable. classId: {}, day: {}", classId, dayOfWeek);

		List<WeeklyTimetable> timetables;
		if (classId != null && dayOfWeek != null) {
			timetables = timetableRepository
					.findAllByPeriod_SchoolClass_IdAndDayOfWeekOrderByPeriod_PeriodOrderAsc(classId, dayOfWeek);
		} else if (classId != null) {
			timetables = timetableRepository
					.findAllByPeriod_SchoolClass_IdOrderByDayOfWeekAscPeriod_PeriodOrderAsc(classId);

		} else {
			timetables = timetableRepository.findAll().stream()
					.sorted(java.util.Comparator.comparing(WeeklyTimetable::getDayOfWeek)
							.thenComparing(timetable -> timetable.getPeriod().getPeriodOrder()))
					.toList();
		}
		return timetables.stream().map(this::mapToResponse).toList();
	}

	// Get timetable by id
	@Transactional(readOnly = true)
	public WeeklyTimetableResponse getById(Long id) {
		log.info("Fetching weekly timetable by id: {}", id);
		return mapToResponse(findTimetable(id));
	}

	@Transactional(readOnly = true)
	public List<WeeklyTimetableResponse> getTeacherTimetable(String email, Long classId, Long subjectId,
			DayOfWeek dayOfWeek) {
		log.info("Fetching teacher timetable for email: {}", email);
		Teacher teacher = teacherRepository.findByTeacherUserEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Teacher not found for email: " + email));
		Specification<WeeklyTimetable> spec = buildFilterSpecification(teacher.getId(), classId, subjectId, dayOfWeek);
		List<WeeklyTimetable> timetables = timetableRepository.findAll(spec);

		return timetables.stream().sorted(java.util.Comparator.comparing(WeeklyTimetable::getDayOfWeek)
				.thenComparing(t -> t.getPeriod().getPeriodOrder())).map(this::mapToResponse).toList();
	}

	private Specification<WeeklyTimetable> buildFilterSpecification(Long teacherId, Long classId, Long subjectId,
			DayOfWeek dayOfWeek) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (teacherId != null) {
				predicates.add(cb.equal(root.get("teacher").get("id"), teacherId));
			}

			if (classId != null) {
				Join<WeeklyTimetable, Period> period = root.join("period", JoinType.INNER);
				predicates.add(cb.equal(period.get("schoolClass").get("id"), classId));
			}

			if (subjectId != null) {
				predicates.add(cb.equal(root.get("subject").get("id"), subjectId));
			}

			if (dayOfWeek != null) {
				predicates.add(cb.equal(root.get("dayOfWeek"), dayOfWeek));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	// Update timetable
	public WeeklyTimetableResponse update(Long id, WeeklyTimetableRequest request) {
		log.info("Updating weekly timetable id: {}", id);

		WeeklyTimetable timetable = findTimetable(id);
		Period period = findPeriod(request.getPeriodId());
		validatePeriodAssignment(request.getPeriodId(), request.getDayOfWeek(), id);
		Subject subject = null;
		Teacher teacher = null;

		if (!Boolean.TRUE.equals(period.getBreakPeriod())) {
			if (request.getSubjectId() == null) {
				throw new ServiceException("Subject is required for a non-break period");
			}

			if (request.getTeacherId() == null) {
				throw new ServiceException("Teacher is required for a non-break period");
			}

			subject = findSubject(request.getSubjectId());
			teacher = findTeacher(request.getTeacherId());
		}

		mapToEntity(request, period, subject, teacher, timetable);
		WeeklyTimetable updated = timetableRepository.save(timetable);
		log.info("Weekly timetable updated successfully with id: {}", updated.getId());
		return mapToResponse(updated);
	}

	public void delete(Long id) {
		log.info("Deleting weekly timetable id: {}", id);

		WeeklyTimetable timetable = findTimetable(id);
		timetableRepository.delete(timetable);
		log.info("Weekly timetable deleted successfully with id: {}", id);
	}

	private WeeklyTimetable mapToEntity(WeeklyTimetableRequest request, Period period, Subject subject, Teacher teacher,
			WeeklyTimetable timetable) {

		if (timetable == null) {
			timetable = new WeeklyTimetable();
		}

		timetable.setPeriod(period);
		timetable.setDayOfWeek(request.getDayOfWeek());
		timetable.setSubject(subject);
		timetable.setTeacher(teacher);
		timetable.setRemarks(request.getRemarks());

		return timetable;
	}

	// Validate duplicate period/day assignment.
	private void validatePeriodAssignment(Long periodId, DayOfWeek dayOfWeek, Long currentId) {
		boolean exists;
		if (currentId == null) {
			exists = timetableRepository.existsByPeriod_IdAndDayOfWeek(periodId, dayOfWeek);
		} else {
			exists = timetableRepository.existsByPeriod_IdAndDayOfWeekAndIdNot(periodId, dayOfWeek, currentId);
		}
		if (exists) {
			throw new ServiceException("Timetable already exists for this period and day");
		}
	}

	private WeeklyTimetable findTimetable(Long id) {
		return timetableRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Weekly timetable not found: " + id));
	}

	private Period findPeriod(Long id) {
		return periodRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Period not found: " + id));
	}

	private Subject findSubject(Long id) {
		return subjectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + id));
	}

	private Teacher findTeacher(Long id) {
		return teacherRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + id));
	}

	private WeeklyTimetableResponse mapToResponse(WeeklyTimetable timetable) {
		Period period = timetable.getPeriod();
		return WeeklyTimetableResponse.builder().id(timetable.getId()).classId(period.getSchoolClass().getId())
				.className(period.getSchoolClass().getClassName()).periodId(period.getId()).periodName(period.getName())
				.periodOrder(period.getPeriodOrder()).startTime(period.getStartTime()).endTime(period.getEndTime())
				.breakPeriod(period.getBreakPeriod()).dayOfWeek(timetable.getDayOfWeek())
				.subjectId(timetable.getSubject() != null ? timetable.getSubject().getId() : null)
				.subjectName(timetable.getSubject() != null ? timetable.getSubject().getSubjectName() : null)
				.teacherId(timetable.getTeacher() != null ? timetable.getTeacher().getId() : null)
				.otherTeacherName(timetable.getOtherTeacherName() != null ? timetable.getOtherTeacherName() : null)
				.teacherName(
						timetable.getTeacher() != null ? timetable.getTeacher().getTeacherUser().getFullName() : null)
				.remarks(timetable.getRemarks()).createdAt(timetable.getCreated()).build();
	}
}