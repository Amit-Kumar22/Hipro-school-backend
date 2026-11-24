package com.hipro.school.backend.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.DocumentRequest;
import com.hipro.school.backend.dtos.DocumentResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.enums.DocumentType;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.DocumentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Document APIs", description = "APIs for managing user documents")
@RestController
@RequestMapping("/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

	private final DocumentService documentService;

	@Operation(summary = "Create a document", description = "Creates a new document for the currently logged-in user using multipart form-data with an optional file and the document fields.")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<DocumentResponse> create(@Valid @ModelAttribute DocumentRequest request) {
		log.info(">> create document: {}, file={}", request.getDocumentName());
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.status(HttpStatus.CREATED).body(documentService.create(request, email));
	}

	@Operation(summary = "Get all documents", description = "Fetches a paginated list of documents. Supports optional userId, documentType, verified, and search filtering via query params.")
	@GetMapping
	public ResponseEntity<PageResponse<DocumentResponse>> getAll(@RequestParam(required = false) Long userId,
			@RequestParam(required = false) DocumentType documentType, @RequestParam(required = false) Boolean verified,
			@RequestParam(required = false) String search,
			@PageableDefault(size = 20, sort = "created", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
		log.info(">> get all documents: userId={}, documentType={}, verified={}, search={}", userId, documentType,
				verified, search);
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(documentService.getAll(email, userId, documentType, verified, search, pageable));
	}

	@Operation(summary = "Get my documents", description = "Fetches a paginated list of documents of the currently logged-in user. Supports optional documentType filtering via query param.")
	@GetMapping("/my")
	public ResponseEntity<PageResponse<DocumentResponse>> getMyDocuments(
			@RequestParam(required = false) DocumentType documentType,
			@PageableDefault(size = 20, sort = "created", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
		log.info(">> get my documents: documentType={}", documentType);
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(documentService.getMyDocuments(email, documentType, pageable));
	}

	@Operation(summary = "Get a document by id", description = "Fetches a document by its unique id path parameter.")
	@GetMapping("/{id}")
	public ResponseEntity<DocumentResponse> getById(@PathVariable Long id) {
		log.info(">> get document by id={}", id);
		return ResponseEntity.ok(documentService.getById(id));
	}

	@Operation(summary = "Update a document", description = "Updates an existing document by id using the validated DocumentRequest request body.")
	@PutMapping("/{id}")
	public ResponseEntity<DocumentResponse> update(@PathVariable Long id, @Valid @RequestBody DocumentRequest request) {
		log.info(">> update document id={}: {}", id, request);
		return ResponseEntity.ok(documentService.update(id, request));
	}

	@Operation(summary = "Verify a document", description = "Marks a document as verified by the currently logged-in user, with optional remarks.")
	@PostMapping("/{id}/verify")
	public ResponseEntity<DocumentResponse> verify(@PathVariable Long id,
			@RequestParam(required = false) String remarks) {
		log.info(">> verify document id={}", id);
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(documentService.verify(id, remarks, email));
	}

	@Operation(summary = "Delete a document", description = "Deletes a document and its uploaded file by its id path parameter.")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
		log.info(">> delete document id={}", id);
		documentService.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Document deleted successfully", null));
	}
}
