package com.hipro.school.backend.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.QuestionOptionRequest;
import com.hipro.school.backend.dtos.QuestionRequest;
import com.hipro.school.backend.dtos.QuestionResponse;
import com.hipro.school.backend.entities.TestExam;
import com.hipro.school.backend.entities.ExamQuestion;
import com.hipro.school.backend.entities.Question;
import com.hipro.school.backend.entities.QuestionOption;
import com.hipro.school.backend.entities.Subject;
import com.hipro.school.backend.enums.QuestionType;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.ExamQuestionRepository;
import com.hipro.school.backend.repositories.TestExamRepository;
import com.hipro.school.backend.repositories.QuestionRepository;
import com.hipro.school.backend.repositories.SubjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionService {

	private final QuestionRepository questionRepository;

	private final SubjectRepository subjectRepository;

	private final TestExamRepository examRepository;

	private final ExamQuestionRepository examQuestionRepository;

	public QuestionResponse create(QuestionRequest request) {
		log.info("Creating question for subject id: {} and exam id: {}", request.getSubjectId(), request.getExamId());

		// Validate input before hitting the database.
		validateQuestion(request);

		Subject subject = subjectRepository.findById(request.getSubjectId())
				.orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + request.getSubjectId()));

		Question question = Question.builder().questionText(request.getQuestionText()).type(request.getType())
				.marks(request.getMarks()).difficulty(request.getDifficulty()).subject(subject)
				.modelAnswer(request.getModelAnswer()).build();

		if (request.getOptions() != null) {
			for (QuestionOptionRequest optionRequest : request.getOptions()) {
				QuestionOption option = QuestionOption.builder().optionText(optionRequest.getOptionText())
						.correct(optionRequest.getCorrect()).optionOrder(optionRequest.getOptionOrder())
						.question(question).build();
				//question.getOptions().add(option);
			}
		}
		Question saved = questionRepository.save(question);
		log.info("Created question id: {}", saved.getId());
		mapToExam(saved, request);
		return mapToResponse(saved);
	}

	private void mapToExam(Question saved, QuestionRequest request) {
		log.info("Mapping question id: {} to exam id: {}", saved.getId(), request.getExamId());
		if (request.getQuestionOrder() == null) {
			throw new IllegalArgumentException("Question order is required when examId is provided");
		}
		TestExam exam = examRepository.findById(request.getExamId())
				.orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + request.getExamId()));
		if (examQuestionRepository.existsByExamIdAndQuestionId(exam.getId(), saved.getId())) {
			throw new ServiceException("Question " + saved.getId() + " is already added to exam " + exam.getId());
		}
		examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(saved)
				.questionOrder(request.getQuestionOrder()).marks(request.getMarks()).build());
	}

	@Transactional(readOnly = true)
	public PageResponse<QuestionResponse> getAll(Pageable pageable) {
		log.info("Fetching all questions with pageable: {}", pageable);
		Page<QuestionResponse> page = questionRepository.findAll(pageable)
				.map(this::mapToResponse);
		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public QuestionResponse getById(Long id) {
		log.info("Fetching question by id: {}", id);
		Question question = questionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Question not found: " + id));
		return mapToResponse(question);
	}

	@Transactional(readOnly = true)
	public PageResponse<QuestionResponse> getBySubject(Long subjectId, Pageable pageable) {
		log.info("Fetching questions by subject id: {} with pageable: {}", subjectId, pageable);
		if (!subjectRepository.existsById(subjectId)) {
			throw new ResourceNotFoundException("Subject not found: " + subjectId);
		}
		Page<QuestionResponse> page = questionRepository.findAllBySubjectId(subjectId, pageable)
				.map(this::mapToResponse);
		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public PageResponse<QuestionResponse> getBySubjectAndType(Long subjectId, QuestionType type, Pageable pageable) {
		log.info("Fetching questions by subject id: {} and type: {} with pageable: {}", subjectId, type, pageable);
		if (!subjectRepository.existsById(subjectId)) {
			throw new ResourceNotFoundException("Subject not found: " + subjectId);
		}
		Page<QuestionResponse> page = questionRepository.findAllBySubjectIdAndType(subjectId, type, pageable)
				.map(this::mapToResponse);
		return PageResponse.from(page);
	}

	public QuestionResponse update(Long id, QuestionRequest request) {
		log.info("Updating question id: {}", id);
		Question question = questionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Question not found: " + id));
		Subject subject = subjectRepository.findById(request.getSubjectId())
				.orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + request.getSubjectId()));
		validateQuestion(request);
		question.setQuestionText(request.getQuestionText());
		question.setType(request.getType());
		question.setMarks(request.getMarks());
		question.setDifficulty(request.getDifficulty());
		question.setSubject(subject);
		question.setModelAnswer(request.getModelAnswer());

		// Remove old options.
		//question.getOptions().clear();

		// Add updated options.
		if (request.getOptions() != null) {

			for (QuestionOptionRequest optionRequest : request.getOptions()) {
				QuestionOption option = QuestionOption.builder().optionText(optionRequest.getOptionText())
						.correct(optionRequest.getCorrect()).optionOrder(optionRequest.getOptionOrder())
						.question(question).build();
				//question.getOptions().add(option);
			}
		}
		Question updated = questionRepository.save(question);
		return mapToResponse(updated);
	}

	public void delete(Long id) {
		log.info("Deleting question id: {}", id);
		Question question = questionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Question not found: " + id));
		questionRepository.delete(question);
	}

	private void validateQuestion(QuestionRequest request) {
		switch (request.getType()) {
		case MCQ -> validateMcq(request);
		case TRUE_FALSE -> validateTrueFalse(request);
		case SHORT_ANSWER -> validateShortAnswer(request);
		case LONG_ANSWER -> validateLongAnswer(request);
		}
	}

	private void validateMcq(QuestionRequest request) {
		if (request.getOptions() == null || request.getOptions().size() < 2) {
			throw new IllegalArgumentException("MCQ must have at least 2 options");
		}
		long correctCount = request.getOptions().stream().filter(QuestionOptionRequest::getCorrect).count();
		if (correctCount != 1) {
			throw new IllegalArgumentException("MCQ must have exactly one correct option");
		}
		validateOptionOrders(request.getOptions());
	}

	private void validateTrueFalse(QuestionRequest request) {
		if (request.getOptions() == null || request.getOptions().size() != 2) {
			throw new IllegalArgumentException("TRUE_FALSE must have exactly 2 options");
		}
		long correctCount = request.getOptions().stream().filter(QuestionOptionRequest::getCorrect).count();
		if (correctCount != 1) {
			throw new IllegalArgumentException("TRUE_FALSE must have exactly one correct option");
		}
		Set<String> optionTexts = request.getOptions().stream()
				.map(option -> option.getOptionText().trim().toLowerCase()).collect(Collectors.toSet());
		if (!optionTexts.contains("true") || !optionTexts.contains("false")) {
			throw new IllegalArgumentException("TRUE_FALSE options must be True and False");
		}
	}

	private void validateShortAnswer(QuestionRequest request) {
		if (request.getOptions() != null && !request.getOptions().isEmpty()) {
			throw new IllegalArgumentException("SHORT_ANSWER cannot have options");
		}
		if (request.getModelAnswer() == null || request.getModelAnswer().isBlank()) {
			throw new IllegalArgumentException("Model answer is required for SHORT_ANSWER");
		}
	}

	private void validateLongAnswer(QuestionRequest request) {
		if (request.getOptions() != null && !request.getOptions().isEmpty()) {
			throw new IllegalArgumentException("LONG_ANSWER cannot have options");
		}
	}

	private void validateOptionOrders(List<QuestionOptionRequest> options) {
		Set<Integer> orders = options.stream().map(QuestionOptionRequest::getOptionOrder).collect(Collectors.toSet());
		if (orders.size() != options.size()) {
			throw new IllegalArgumentException("Duplicate option order is not allowed");
		}
	}

	private QuestionResponse mapToResponse(Question question) {
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
}