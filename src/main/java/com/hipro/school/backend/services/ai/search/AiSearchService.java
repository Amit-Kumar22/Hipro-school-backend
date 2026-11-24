package com.hipro.school.backend.services.ai.search;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.config.AiProperties;
import com.hipro.school.backend.config.AiSearchProperties;
import com.hipro.school.backend.dtos.ai.AiSearchRequest;
import com.hipro.school.backend.dtos.ai.AiSearchResponse;
import com.hipro.school.backend.entities.Role;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiSearchService {

	private final AiProperties aiProperties;
	private final AiSearchProperties searchProperties;
	private final SchemaMetadataProvider schemaMetadataProvider;
	private final SqlGenerationService sqlGenerationService;
	private final SqlValidationService sqlValidationService;
	private final ReadOnlyQueryExecutor queryExecutor;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public AiSearchResponse search(AiSearchRequest request, String email) {
		long startedAt = System.currentTimeMillis();

		if (!searchProperties.isEnabled()) {
			throw new ServiceException("AI search is currently disabled.");
		}

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
		if (!Role.ADMIN.equals(user.getRole()) && !Role.SUPERADMIN.equals(user.getRole())) {
			throw new AccessDeniedException("Only admin users can run AI search queries");
		}

		String schemaDescription = schemaMetadataProvider.getSchemaDescription();
		ZoneId timezone = ZoneId.of("Asia/Kolkata");
		LocalDate today = LocalDate.now(timezone);

		String provider = request.getProvider() != null ? request.getProvider() : aiProperties.getDefaultProvider();

		SqlGenerationService.GeneratedSql generated = sqlGenerationService.generate(request.getQuestion(),
				schemaDescription, provider, today, timezone);

		if (generated.sql() == null) {
			long processingMs = System.currentTimeMillis() - startedAt;
			log.info("AI search: no SQL generated for question: {}, reason: {}", request.getQuestion(),
					generated.explanation());
			return AiSearchResponse.builder().question(request.getQuestion()).summary(generated.explanation())
					.columns(java.util.List.of()).data(java.util.List.of()).rowCount(0).processingMs(processingMs)
					.build();
		}

		sqlValidationService.validate(generated.sql());

		ReadOnlyQueryExecutor.ReadOnlyResultSet resultSet = queryExecutor.execute(generated.sql());
		String summary = sqlGenerationService.summarize(request.getQuestion(), resultSet, provider);

		long processingMs = System.currentTimeMillis() - startedAt;

		log.info("AI search completed: user={}, rows={}, processingMs={}", email, resultSet.rowCount(), processingMs);

		return AiSearchResponse.builder().question(request.getQuestion()).sql(generated.sql()).summary(summary)
				.columns(resultSet.columns()).data(resultSet.rows()).rowCount(resultSet.rowCount())
				.processingMs(processingMs).build();
	}
}
