package com.hipro.school.backend.services.ai.search;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.hipro.school.backend.config.AiProperties;
import com.hipro.school.backend.config.AiSearchProperties;
import com.hipro.school.backend.dtos.ai.AiSearchRequest;
import com.hipro.school.backend.dtos.ai.AiSearchResponse;
import com.hipro.school.backend.entities.Role;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class AiSearchServiceTest {

	@Mock
	private SchemaMetadataProvider schemaMetadataProvider;
	@Mock
	private SqlGenerationService sqlGenerationService;
	@Mock
	private SqlValidationService sqlValidationService;
	@Mock
	private ReadOnlyQueryExecutor queryExecutor;
	@Mock
	private UserRepository userRepository;

	private AiSearchService aiSearchService;
	private AiProperties aiProperties;
	private AiSearchProperties searchProperties;

	private User adminUser;
	private User studentUser;

	@BeforeEach
	void setUp() {
		aiProperties = new AiProperties();
		aiProperties.setDefaultProvider("xkiro");
		aiProperties.setSummaryEnabled(false);

		searchProperties = new AiSearchProperties();
		searchProperties.setEnabled(true);

		aiSearchService = new AiSearchService(aiProperties, searchProperties, schemaMetadataProvider,
				sqlGenerationService, sqlValidationService, queryExecutor, userRepository);

		adminUser = new User();
		adminUser.setId(1L);
		adminUser.setEmail("admin@school.com");
		adminUser.setRole(Role.ADMIN);

		studentUser = new User();
		studentUser.setId(2L);
		studentUser.setEmail("student@school.com");
		studentUser.setRole(Role.STUDENT);
	}

	@Test
	@DisplayName("Should return search results for admin user")
	void shouldReturnSearchResultsForAdmin() {
		AiSearchRequest request = AiSearchRequest.builder().question("Show all students").build();
		String schema = "Table: students\nColumns: id, name";
		String sql = "SELECT * FROM students";
		ReadOnlyQueryExecutor.ReadOnlyResultSet resultSet = new ReadOnlyQueryExecutor.ReadOnlyResultSet(
				List.of("id", "name"), List.of(Map.of("id", 1, "name", "John")), 1);

		when(userRepository.findByEmail("admin@school.com")).thenReturn(Optional.of(adminUser));
		when(schemaMetadataProvider.getSchemaDescription()).thenReturn(schema);
		when(sqlGenerationService.generate(eq("Show all students"), eq(schema), eq("xkiro"), any(LocalDate.class),
				any(ZoneId.class))).thenReturn(new SqlGenerationService.GeneratedSql(sql, "Get all students"));
		when(queryExecutor.execute(sql)).thenReturn(resultSet);

		AiSearchResponse response = aiSearchService.search(request, "admin@school.com");

		assertNotNull(response);
		assertEquals("Show all students", response.getQuestion());
		assertEquals(sql, response.getSql());
		assertEquals(1, response.getRowCount());
		assertEquals(List.of("id", "name"), response.getColumns());
		assertNotNull(response.getProcessingMs());
	}

	@Test
	@DisplayName("Should deny access to non-admin users")
	void shouldDenyAccessToNonAdmin() {
		AiSearchRequest request = AiSearchRequest.builder().question("Show all students").build();

		when(userRepository.findByEmail("student@school.com")).thenReturn(Optional.of(studentUser));

		assertThrows(AccessDeniedException.class, () -> aiSearchService.search(request, "student@school.com"));
	}

	@Test
	@DisplayName("Should throw exception when AI search is disabled")
	void shouldThrowExceptionWhenDisabled() {
		AiSearchRequest request = AiSearchRequest.builder().question("Show all students").build();
		searchProperties.setEnabled(false);

		assertThrows(ServiceException.class, () -> aiSearchService.search(request, "admin@school.com"));
	}

	@Test
	@DisplayName("Should throw exception when user not found")
	void shouldThrowExceptionWhenUserNotFound() {
		AiSearchRequest request = AiSearchRequest.builder().question("Show all students").build();

		when(userRepository.findByEmail("unknown@school.com")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> aiSearchService.search(request, "unknown@school.com"));
	}

	@Test
	@DisplayName("Should handle null SQL from AI (unsupported question)")
	void shouldHandleNullSqlFromAi() {
		AiSearchRequest request = AiSearchRequest.builder().question("What is the meaning of life?").build();
		String schema = "Table: students\nColumns: id, name";

		when(userRepository.findByEmail("admin@school.com")).thenReturn(Optional.of(adminUser));
		when(schemaMetadataProvider.getSchemaDescription()).thenReturn(schema);
		when(sqlGenerationService.generate(eq("What is the meaning of life?"), eq(schema), eq("xkiro"),
				any(LocalDate.class), any(ZoneId.class)))
				.thenReturn(new SqlGenerationService.GeneratedSql(null, "Cannot answer this question"));

		AiSearchResponse response = aiSearchService.search(request, "admin@school.com");

		assertNotNull(response);
		assertEquals("What is the meaning of life?", response.getQuestion());
		assertNull(response.getSql());
		assertEquals("Cannot answer this question", response.getSummary());
		assertEquals(0, response.getRowCount());
	}

	@Test
	@DisplayName("Should throw exception when SQL validation fails")
	void shouldThrowExceptionWhenSqlValidationFails() {
		AiSearchRequest request = AiSearchRequest.builder().question("Show all students").build();
		String schema = "Table: students\nColumns: id, name";
		String sql = "DROP TABLE students";

		when(userRepository.findByEmail("admin@school.com")).thenReturn(Optional.of(adminUser));
		when(schemaMetadataProvider.getSchemaDescription()).thenReturn(schema);
		when(sqlGenerationService.generate(eq("Show all students"), eq(schema), eq("xkiro"), any(LocalDate.class),
				any(ZoneId.class))).thenReturn(new SqlGenerationService.GeneratedSql(sql, "Drop table"));
		doThrow(new ServiceException("SQL contains forbidden keyword: DROP"))
				.when(sqlValidationService).validate(sql);

		assertThrows(ServiceException.class, () -> aiSearchService.search(request, "admin@school.com"));
	}

	@Test
	@DisplayName("Should use custom provider when specified")
	void shouldUseCustomProviderWhenSpecified() {
		AiSearchRequest request = AiSearchRequest.builder().question("Show all students").provider("openai").build();
		String schema = "Table: students\nColumns: id, name";
		String sql = "SELECT * FROM students";
		ReadOnlyQueryExecutor.ReadOnlyResultSet resultSet = new ReadOnlyQueryExecutor.ReadOnlyResultSet(
				List.of("id", "name"), List.of(Map.of("id", 1, "name", "John")), 1);

		when(userRepository.findByEmail("admin@school.com")).thenReturn(Optional.of(adminUser));
		when(schemaMetadataProvider.getSchemaDescription()).thenReturn(schema);
		when(sqlGenerationService.generate(eq("Show all students"), eq(schema), eq("openai"), any(LocalDate.class),
				any(ZoneId.class))).thenReturn(new SqlGenerationService.GeneratedSql(sql, "Get all students"));
		when(queryExecutor.execute(sql)).thenReturn(resultSet);

		AiSearchResponse response = aiSearchService.search(request, "admin@school.com");

		assertNotNull(response);
		verify(sqlGenerationService).generate(eq("Show all students"), eq(schema), eq("openai"), any(LocalDate.class),
				any(ZoneId.class));
	}

	@Test
	@DisplayName("Should handle empty result set")
	void shouldHandleEmptyResultSet() {
		AiSearchRequest request = AiSearchRequest.builder().question("Show students with name XYZ").build();
		String schema = "Table: students\nColumns: id, name";
		String sql = "SELECT * FROM students WHERE name = 'XYZ'";
		ReadOnlyQueryExecutor.ReadOnlyResultSet resultSet = new ReadOnlyQueryExecutor.ReadOnlyResultSet(
				List.of("id", "name"), List.of(), 0);

		when(userRepository.findByEmail("admin@school.com")).thenReturn(Optional.of(adminUser));
		when(schemaMetadataProvider.getSchemaDescription()).thenReturn(schema);
		when(sqlGenerationService.generate(eq("Show students with name XYZ"), eq(schema), eq("xkiro"),
				any(LocalDate.class), any(ZoneId.class)))
				.thenReturn(new SqlGenerationService.GeneratedSql(sql, "Get students by name"));
		when(queryExecutor.execute(sql)).thenReturn(resultSet);

		AiSearchResponse response = aiSearchService.search(request, "admin@school.com");

		assertNotNull(response);
		assertEquals(0, response.getRowCount());
		assertTrue(response.getData().isEmpty());
	}
}
