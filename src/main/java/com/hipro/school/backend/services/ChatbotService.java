package com.hipro.school.backend.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.Chatbot;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.ChatbotRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

	private final ChatbotRepository repository;

	public void create(Chatbot request) {
		log.info("Creating intent: {}", request.getKeyword());
		if (repository.existsByKeyword(request.getKeyword())) {
			throw new ServiceException("Keyword already exists");
		}
		repository.save(request);
	}

	@Transactional
	public void update(Long id, Chatbot request) {
		log.info("Updating intent id: {}", id);
		Chatbot intent = getEntity(id);
		intent.setAnswer(request.getAnswer());
		intent.setActive(request.getActive());
		intent.setKeyword(request.getKeyword().toUpperCase());
	}

	public PageResponse<Chatbot> getAll(Pageable pageable) {
		log.info("Fetching all chatbot intents with pageable: {}", pageable);
		Page<Chatbot> pageChat = repository.findAll(pageable);
		return PageResponse.from(pageChat);
	}

	public void delete(Long id) {
		log.info("Deleting intent id: {}", id);
		Chatbot intent = getEntity(id);
		repository.delete(intent);
	}

	public List<Chatbot> findByKeyword(String keyword) {
		log.info("Fetching chatbot intents by keyword: {}", keyword);
		return repository.findByKeywordAndActiveTrue(keyword.toUpperCase());
	}

	public Chatbot getEntity(Long id) {
		log.info("Fetching chatbot intent id: {}", id);
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Intent not found with id: " + id));
	}

}