package com.hipro.school.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.StudentFormRequest;
import com.hipro.school.backend.dtos.StudentRequest;
import com.hipro.school.backend.dtos.UploadResponse;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.enums.UserStatus;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.ProfileService;
import com.hipro.school.backend.services.StudentImportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Student Admission User APIs", description = "This collection contains APIs for Student Admission resources")
@RestController
@RequestMapping("/v1/student")
public class StudentController {

	@Autowired
	private ProfileService profileService;

	@Autowired
	private StudentImportService studentImportService;

	@Operation(summary = "Create a new student admission", description = "Creates a new student admission record using the StudentRequest request body for the currently logged-in user.")
	@PostMapping("/new-addmission")
	public ResponseEntity<ApiResponse<String>> newAddmission(@Valid @RequestBody StudentRequest request) {
		log.info("API: Create new Addmission for student {}", request.getName());
		profileService.addNewStudent(request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.CREATED, "Student detail successfully added!", null));
	}

	@Operation(summary = "Create a new student admission via admission form", description = "Creates a new student admission record using the StudentFormRequest request body for the currently logged-in user.")
	@PostMapping("/addmission-form")
	public ResponseEntity<ApiResponse<String>> admissionForm(@Valid @RequestBody StudentFormRequest request) {
		log.info("API: Create new Admission for student {}", request.getName());
		profileService.addAdmissionForm(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(HttpStatus.CREATED, "Student detail successfully added!", null));
	}

	@Operation(summary = "Get All Franchise API", description = "This API fetches All Franchise  Details.")
	@GetMapping("/all-student")
	public ResponseEntity<PageResponse<Student>> getAllStudentDetails(Pageable pageable,
			@RequestParam(required = false) Long classId, @RequestParam(required = false) String search,
			@RequestParam(required = false) String feeStatus,
			@RequestParam(defaultValue = "ACTIVE") UserStatus status) {
		log.info(">> getAllStudentDetails({}, {})", pageable);
		return ResponseEntity.ok(profileService.getAllSchoolStudent(classId, search, feeStatus, status, pageable));
	}

	@Operation(summary = "Bulk import students from CSV or Excel", description = "Upload a CSV or Excel (.xls/.xlsx) file with a header row (name, fatherName, motherName, parentEmail, parentPhone, className, dob, address, pincode, gender). Each valid row creates a student admission; invalid rows are reported with their row number.")
	@PostMapping(value = "/bulk-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<UploadResponse>> bulkImportStudents(@RequestParam MultipartFile file) {
		log.info("API: bulk import students from file {}", file.getOriginalFilename());
		UploadResponse res = studentImportService.importStudents(file);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.CREATED, "Student bulk import processed", res));
	}

	@Operation(summary = "Upload a raw student file", description = "Uploads a raw student data file (multipart form-data) along with the schoolCode parameter for the currently logged-in user.")
	@PostMapping(value = "/student-raw-file-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<UploadResponse>> uploadStudentRawFile(@RequestParam MultipartFile file,
			@RequestParam String schoolCode) {
		log.info("API: Upload new Student file {} for school code {}", file.getOriginalFilename(), schoolCode);
		String email = AuthDetailsProvider.getLoggedEmail();
		UploadResponse res = profileService.uploadNewStudentFile(file, email, schoolCode);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.CREATED, "Student detail successfully added!", res));
	}

	@Operation(summary = "Update Student data API", description = "Update All student data from raw data to user table.")
	@PutMapping("/update-student/{studentId}")
	public ResponseEntity<ApiResponse<String>> updateStudentDetails(@PathVariable Long studentId,
			@RequestBody StudentRequest request) {
		log.info(">> updateStudentDetails({}, {})", studentId, request);
		profileService.updateStudentDetails(studentId, request);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "Student updated successfully", null));
	}

	@Operation(summary = "Delete Student data API", description = "Delete student by studentId.")
	@PutMapping("/delete-student/{studentId}")
	public ResponseEntity<ApiResponse<String>> deleteStudentDetails(@PathVariable Long studentId) {
		log.info(">> updateStudentDetails({}, {})", studentId);
		profileService.deleteStudentDetails(studentId);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT, "Student updated successfully", null));
	}
}
