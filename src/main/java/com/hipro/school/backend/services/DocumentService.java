package com.hipro.school.backend.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.hipro.school.backend.dtos.DocumentRequest;
import com.hipro.school.backend.dtos.DocumentResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.entities.Document;
import com.hipro.school.backend.entities.Role;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.enums.DocumentType;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.DocumentRepository;
import com.hipro.school.backend.repositories.UserRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentService {

	private static final String DOCUMENT_FOLDER = "documents";

	private final DocumentRepository documentRepository;
	private final UserRepository userRepository;
	//private final FileUploaderService fileUploaderService;

	public DocumentResponse create(DocumentRequest request, String email) {
		log.info("Creating document '{}' of type {} for user: {}", request.getDocumentName(),
				request.getDocumentType(), email);
		User user = findUser(email);
		String fileUrl=null;
		if (request.getFile() != null ) {
			fileUrl = request.getFile().getOriginalFilename();
			/*try {
				fileUrl = fileUploaderService.uploadFile(file, DOCUMENT_FOLDER);
			} catch (IOException | IllegalArgumentException e) {
				throw new ServiceException("Failed to upload document file: " + e.getMessage());
			}*/
		}
		Document document = Document.builder().user(user).documentType(request.getDocumentType())
				.documentName(request.getDocumentName()).fileUrl(fileUrl)
				.documentNumber(request.getDocumentNumber()).remarks(request.getRemarks()).build();
		Document saved = documentRepository.save(document);
		log.info("Document created successfully with id: {}", saved.getId());
		return DocumentResponse.from(saved);
	}

	// Get all documents with filters and pagination
	@Transactional(readOnly = true)
	public PageResponse<DocumentResponse> getAll(String email, Long userId, DocumentType documentType,
			Boolean verified, String search, Pageable pageable) {
		log.info("Fetching documents - email: {}, userId: {}, documentType: {}, verified: {}, search: {}", email,
				userId, documentType, verified, search);
		User user = findUser(email);
		if (!Role.ADMIN.equals(user.getRole()) && !Role.SUPERADMIN.equals(user.getRole())) {
			userId = user.getId();
		}
		Specification<Document> specification = byFilters(userId, documentType, verified, search);
		Page<Document> page = documentRepository.findAll(specification, pageable);
		return PageResponse.from(page.map(DocumentResponse::from));
	}

	// Get documents of the logged-in user
	@Transactional(readOnly = true)
	public PageResponse<DocumentResponse> getMyDocuments(String email, DocumentType documentType, Pageable pageable) {
		log.info("Fetching documents for user: {}, documentType: {}", email, documentType);
		User user = findUser(email);
		Specification<Document> specification = byFilters(user.getId(), documentType, null, null);
		Page<Document> page = documentRepository.findAll(specification, pageable);
		return PageResponse.from(page.map(DocumentResponse::from));
	}

	// Get document by id
	@Transactional(readOnly = true)
	public DocumentResponse getById(Long id) {
		log.info("Fetching document by id: {}", id);
		return DocumentResponse.from(findDocument(id));
	}

	// Update document metadata
	public DocumentResponse update(Long id, DocumentRequest request) {
		log.info("Updating document id: {}", id);
		Document document = findDocument(id);
		document.setDocumentType(request.getDocumentType());
		document.setDocumentName(request.getDocumentName());
		document.setDocumentNumber(request.getDocumentNumber());
		document.setRemarks(request.getRemarks());
		Document updated = documentRepository.save(document);
		log.info("Document updated successfully with id: {}", updated.getId());
		return DocumentResponse.from(updated);
	}

	// Verify a document
	public DocumentResponse verify(Long id, String remarks, String verifierEmail) {
		log.info("Verifying document id: {}", id);
		Document document = findDocument(id);
		User verifier = findUser(verifierEmail);
		document.setVerified(true);
		document.setVerifiedBy(verifier.getId());
		document.setVerifiedAt(LocalDateTime.now());
		if (StringUtils.hasText(remarks)) {
			document.setRemarks(remarks);
		}
		Document updated = documentRepository.save(document);
		log.info("Document verified successfully with id: {}", updated.getId());
		return DocumentResponse.from(updated);
	}

	// Delete document and its uploaded file
	public void delete(Long id) {
		log.info("Deleting document id: {}", id);
		Document document = findDocument(id);
		//fileUploaderService.deleteFile(document.getFileUrl());
		documentRepository.delete(document);
		log.info("Document deleted successfully with id: {}", id);
	}

	// Specification for filtering documents.
	private Specification<Document> byFilters(Long userId, DocumentType documentType, Boolean verified,
			String search) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (userId != null) {
				predicates.add(cb.equal(root.get("user").get("id"), userId));
			}
			if (documentType != null) {
				predicates.add(cb.equal(root.get("documentType"), documentType));
			}
			if (verified != null) {
				predicates.add(cb.equal(root.get("verified"), verified));
			}
			if (StringUtils.hasText(search)) {
				String searchValue = "%" + search.trim().toLowerCase() + "%";
				predicates.add(cb.or(cb.like(cb.lower(root.get("documentName")), searchValue),
						cb.like(cb.lower(root.get("fileName")), searchValue),
						cb.like(cb.lower(root.get("documentNumber")), searchValue)));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private Document findDocument(Long id) {
		return documentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id));
	}

	private User findUser(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
	}
}
