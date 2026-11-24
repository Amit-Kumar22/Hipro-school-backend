package com.hipro.school.backend.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.ExamQuestionRequest;
import com.hipro.school.backend.dtos.ExamQuestionResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.QuestionOptionResponse;
import com.hipro.school.backend.dtos.QuestionResponse;
import com.hipro.school.backend.entities.TestExam;
import com.hipro.school.backend.entities.ExamQuestion;
import com.hipro.school.backend.entities.Question;
import com.hipro.school.backend.entities.QuestionOption;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.ExamQuestionRepository;
import com.hipro.school.backend.repositories.TestExamRepository;
import com.hipro.school.backend.repositories.QuestionRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamQuestionService {

	private final ExamQuestionRepository examQuestionRepository;
	private final TestExamRepository examRepository;
	private final QuestionRepository questionRepository;

	public ExamQuestionResponse create(ExamQuestionRequest request) {
		log.info("Creating exam question for exam id: {} and question id: {}", request.getExamId(),
				request.getQuestionId());
		TestExam exam = findExam(request.getExamId());
		Question question = findQuestion(request.getQuestionId());
		validateNotDuplicate(request.getExamId(), request.getQuestionId());

		ExamQuestion examQuestion = ExamQuestion.builder().exam(exam).question(question)
				.questionOrder(request.getQuestionOrder()).marks(request.getMarks()).build();
		ExamQuestion saved = examQuestionRepository.save(examQuestion);
		log.info("Created exam question id: {}", saved.getId());
		return mapToResponse(saved);
	}

	@Transactional(readOnly = true)
	public PageResponse<ExamQuestionResponse> getExamQuestions(Long examId, Long questionId, Pageable pageable) {
		log.info("Fetching exam questions for exam id: {}, question id: {} with pageable: {}", examId,
				questionId, pageable);
		if (examId != null && !examRepository.existsById(examId)) {
			throw new ResourceNotFoundException("Exam not found: " + examId);
		}
		if (questionId != null && !questionRepository.existsById(questionId)) {
			throw new ResourceNotFoundException("Question not found: " + questionId);
		}
		Specification<ExamQuestion> specification = byFilters(examId, questionId);
		Page<ExamQuestionResponse> page = examQuestionRepository.findAll(specification, pageable)
				.map(this::mapToResponse);
		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public ExamQuestionResponse getById(Long id) {
		log.info("Fetching exam question by id: {}", id);
		return mapToResponse(findExamQuestion(id));
	}

	public ExamQuestionResponse update(Long id, ExamQuestionRequest request) {
		log.info("Updating exam question id: {} with request: {}", id, request);
		ExamQuestion examQuestion = findExamQuestion(id);
		TestExam exam = findExam(request.getExamId());
		Question question = findQuestion(request.getQuestionId());

		// Validate uniqueness only when the exam/question mapping is changing.
		if (!examQuestion.getExam().getId().equals(exam.getId())
				|| !examQuestion.getQuestion().getId().equals(question.getId())) {
			validateNotDuplicate(request.getExamId(), request.getQuestionId());
		}

		examQuestion.setExam(exam);
		examQuestion.setQuestion(question);
		examQuestion.setQuestionOrder(request.getQuestionOrder());
		examQuestion.setMarks(request.getMarks());

		ExamQuestion updated = examQuestionRepository.save(examQuestion);
		log.info("Updated exam question id: {}", updated.getId());
		return mapToResponse(updated);
	}

	public void delete(Long id) {
		log.info("Deleting exam question id: {}", id);
		ExamQuestion examQuestion = findExamQuestion(id);
		examQuestionRepository.delete(examQuestion);
		log.info("Deleted exam question id: {}", id);
	}

	private ExamQuestion findExamQuestion(Long id) {
		return examQuestionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Exam question not found: " + id));
	}

	private TestExam findExam(Long examId) {
		return examRepository.findById(examId)
				.orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + examId));
	}

	private Question findQuestion(Long questionId) {
		return questionRepository.findById(questionId)
				.orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionId));
	}

	private void validateNotDuplicate(Long examId, Long questionId) {
		if (examQuestionRepository.existsByExamIdAndQuestionId(examId, questionId)) {
			throw new ServiceException("Question " + questionId + " is already added to exam " + examId);
		}
	}

	private ExamQuestionResponse mapToResponse(ExamQuestion examQuestion) {
		return ExamQuestionResponse.builder().id(examQuestion.getId()).examId(examQuestion.getExam().getId())
				.examName(examQuestion.getExam().getTitleName()).question(mapToQuestionResponse(examQuestion.getQuestion()))
				.questionOrder(examQuestion.getQuestionOrder()).marks(examQuestion.getMarks()).build();
	}

	private QuestionResponse mapToQuestionResponse(Question question) {
		/*List<QuestionOptionResponse> options = question.getOptions().stream()
				.sorted(Comparator.comparing(QuestionOption::getOptionOrder))
				.map(option -> QuestionOptionResponse.builder().id(option.getId()).optionText(option.getOptionText())
						.correct(option.getCorrect()).optionOrder(option.getOptionOrder()).build())
				.toList();*/
		return QuestionResponse.builder().id(question.getId()).questionText(question.getQuestionText())
				.type(question.getType()).marks(question.getMarks()).difficulty(question.getDifficulty())
				.subjectId(question.getSubject() != null ? question.getSubject().getId() : null)
				.subjectName(question.getSubject() != null ? question.getSubject().getSubjectName() : null)
				.modelAnswer(question.getModelAnswer()).build();
	}

	public static Specification<ExamQuestion> byFilters(Long examId, Long questionId) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			// Optional exam filter.
			if (examId != null) {
				predicates.add(cb.equal(root.get("exam").get("id"), examId));
			}
			// Optional question filter.
			if (questionId != null) {
				predicates.add(cb.equal(root.get("question").get("id"), questionId));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
