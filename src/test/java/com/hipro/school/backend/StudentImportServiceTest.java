package com.hipro.school.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.hipro.school.backend.dtos.StudentRequest;
import com.hipro.school.backend.dtos.UploadResponse;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.SchoolClassRepository;
import com.hipro.school.backend.services.ProfileService;
import com.hipro.school.backend.services.StudentImportService;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

class StudentImportServiceTest {

	private StudentImportService service;

	private ProfileService profileService;

	private SchoolClassRepository classRepository;

	@BeforeEach
	void setUp() {
		service = new StudentImportService();
		profileService = Mockito.mock(ProfileService.class);
		classRepository = Mockito.mock(SchoolClassRepository.class);

		try (var factory = Validation.buildDefaultValidatorFactory()) {
			Validator validator = factory.getValidator();
			ReflectionTestUtils.setField(service, "validator", validator);
		}
		ReflectionTestUtils.setField(service, "profileService", profileService);
		ReflectionTestUtils.setField(service, "classRepository", classRepository);

		SchoolClass schoolClass = new SchoolClass();
		schoolClass.setId(1L);
		schoolClass.setClassName("Class 1");
		when(classRepository.findByClassNameIgnoreCase("Class 1")).thenReturn(java.util.Optional.of(schoolClass));
		when(classRepository.findByClassNameIgnoreCase("Class 2")).thenReturn(java.util.Optional.of(schoolClass));
		when(classRepository.findByClassNameIgnoreCase("Class 3")).thenReturn(java.util.Optional.of(schoolClass));
	}

	@Test
	void importsBundledCsvSample() throws Exception {
		MockMultipartFile file = resourceFile("/samples/students.csv", "students.csv", "text/csv");

		UploadResponse response = service.importStudents(file);

		assertEquals(6, response.getTotal());
		assertEquals(6, response.getSuccess());
		assertEquals(0, response.getFailed());
		assertTrue(response.getErrors().isEmpty());
		verify(profileService, times(6)).addNewStudent(any(StudentRequest.class));
	}

	@Test
	void importsBundledExcelSample() throws Exception {
		MockMultipartFile file = resourceFile("/samples/students.xlsx", "students.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

		UploadResponse response = service.importStudents(file);

		assertEquals(6, response.getTotal());
		assertEquals(6, response.getSuccess());
		assertEquals(0, response.getFailed());
		assertTrue(response.getErrors().isEmpty());
		verify(profileService, times(6)).addNewStudent(any(StudentRequest.class));
	}

	private MockMultipartFile resourceFile(String resource, String fileName, String contentType) throws Exception {
		try (var in = getClass().getResourceAsStream(resource)) {
			if (in == null) {
				throw new IllegalStateException("Sample file not found on classpath: " + resource);
			}
			return new MockMultipartFile("file", fileName, contentType, in.readAllBytes());
		}
	}

	private MockMultipartFile csv(String content) {
		return new MockMultipartFile("file", "students.csv", "text/csv", content.getBytes(StandardCharsets.UTF_8));
	}

	@Test
	void importsValidCsvRows() {
		String content = "name,fatherName,motherName,parentEmail,parentPhone,className,dob,address,pincode,gender\n"
				+ "Amit Kumar,Ramesh Kumar,Sita Devi,ram@test.com,9876543210,Class 1,2015-05-12,Street 1,123456,MALE\n"
				+ "Priya Sharma,Anil Sharma,Rekha Sharma,,9876543211,Class 1,12/05/2015,,,\n";

		UploadResponse response = service.importStudents(csv(content));

		assertEquals(2, response.getTotal());
		assertEquals(2, response.getSuccess());
		assertEquals(0, response.getFailed());
		verify(profileService, times(2)).addNewStudent(any(StudentRequest.class));
	}

	@Test
	void reportsRowWiseValidationErrors() {
		String content = "name,parentPhone,className\n" + "Amit,9876543210,Class 1\n" + "Rohan,12345,Class 1\n"
				+ ",9876543212,Class 1\n";

		UploadResponse response = service.importStudents(csv(content));

		assertEquals(3, response.getTotal());
		assertEquals(1, response.getSuccess());
		assertEquals(2, response.getFailed());
		assertTrue(response.getErrors().get(0).startsWith("Row 3"));
		assertTrue(response.getErrors().get(1).startsWith("Row 4"));
		verify(profileService, times(1)).addNewStudent(any(StudentRequest.class));
	}

	@Test
	void failsWhenClassDoesNotExist() {
		String content = "name,parentPhone,className\nAmit,9876543210,Unknown Class\n";

		UploadResponse response = service.importStudents(csv(content));

		assertEquals(1, response.getTotal());
		assertEquals(0, response.getSuccess());
		assertTrue(response.getErrors().get(0).contains("Class not found with name: Unknown Class"));
	}

	@Test
	void failsWhenRequiredColumnMissing() {
		String content = "name,parentPhone\nAmit,9876543210\n";

		ServiceException exception = assertThrows(ServiceException.class, () -> service.importStudents(csv(content)));
		assertTrue(exception.getMessage().contains("Missing required column(s): className"));
	}

	@Test
	void rejectsUnsupportedFileType() {
		MockMultipartFile file = new MockMultipartFile("file", "students.txt", "text/plain", "name".getBytes());

		ServiceException exception = assertThrows(ServiceException.class, () -> service.importStudents(file));
		assertTrue(exception.getMessage().contains("Unsupported file type"));
	}

	@Test
	void importsExcelRows() throws Exception {
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Students");
			Row header = sheet.createRow(0);
			String[] columns = { "Name", "Father Name", "Parent Phone", "Class Name", "DOB", "Gender" };
			for (int i = 0; i < columns.length; i++) {
				header.createCell(i).setCellValue(columns[i]);
			}
			Row data = sheet.createRow(1);
			data.createCell(0).setCellValue("Amit Kumar");
			data.createCell(1).setCellValue("Ramesh Kumar");
			data.createCell(2).setCellValue(9876543210d);
			data.createCell(3).setCellValue("Class 1");
			var dateCell = data.createCell(4);
			dateCell.setCellValue(new java.util.Date(115, 4, 12));
			var dateStyle = workbook.createCellStyle();
			dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/mm/yyyy"));
			dateCell.setCellStyle(dateStyle);
			data.createCell(5).setCellValue("Male");

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			MockMultipartFile file = new MockMultipartFile("file", "students.xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", out.toByteArray());

			UploadResponse response = service.importStudents(file);

			assertEquals(1, response.getTotal());
			assertEquals(1, response.getSuccess());
			verify(profileService, times(1)).addNewStudent(any(StudentRequest.class));
		}
	}
}
