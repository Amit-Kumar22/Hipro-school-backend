package com.hipro.school.backend.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.FeeGenerateRequest;
import com.hipro.school.backend.dtos.FeePaymentRequest;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.PaymentResponse;
import com.hipro.school.backend.dtos.StudentFeeResponse;
import com.hipro.school.backend.entities.FeeStructure;
import com.hipro.school.backend.entities.Payment;
import com.hipro.school.backend.entities.SchoolClass;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.entities.StudentFee;
import com.hipro.school.backend.enums.FeeStatus;
import com.hipro.school.backend.enums.PaymentStatus;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.exceptions.ServiceException;
import com.hipro.school.backend.repositories.FeeStructureRepository;
import com.hipro.school.backend.repositories.PaymentRepository;
import com.hipro.school.backend.repositories.SchoolClassRepository;
import com.hipro.school.backend.repositories.StudentFeeRepository;
import com.hipro.school.backend.repositories.StudentRepository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentFeeService {

	private static final String[] EXCEL_HEADERS = { "Fee ID", "Admission No", "Student ID", "Student Name", "Class",
			"Fee Type", "Amount", "Paid Amount", "Due Amount", "Fee Date", "Due Date", "Status" };

	private final StudentFeeRepository studentFeeRepository;
	private final FeeStructureRepository feeStructureRepository;
	private final StudentRepository studentRepository;
	private final PaymentRepository paymentRepository;
	private final SchoolClassRepository schoolClassRepository;

	@Transactional(readOnly = true)
	public PageResponse<StudentFeeResponse> getStudentFees(Long classId, Long studentId, FeeStatus status,
			Pageable pageable) {
		log.info("Fetching student fees for class id: {}, student id: {}, status: {} pageable: {}", classId, studentId,
				status, pageable);
		Specification<StudentFee> specification = byFilters(classId, studentId, status);
		Page<StudentFeeResponse> page = studentFeeRepository.findAll(specification, pageable).map(this::mapToResponse);
		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public StudentFeeResponse getById(Long id) {
		log.info("Fetching student fee by id: {}", id);
		return mapToResponse(findStudentFee(id));
	}

	@Transactional(readOnly = true)
	public byte[] exportToExcel(Long classId, Long studentId, FeeStatus status) {
		log.info("Exporting student fees to excel for class id: {}, student id: {}, status: {}", classId, studentId,
				status);
		List<StudentFee> fees = studentFeeRepository.findAll(byFilters(classId, studentId, status));
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Student Fees");

			CellStyle headerStyle = workbook.createCellStyle();
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			CellStyle moneyStyle = workbook.createCellStyle();
			moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

			CellStyle dateStyle = workbook.createCellStyle();
			dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd-mm-yyyy"));

			Row header = sheet.createRow(0);
			for (int i = 0; i < EXCEL_HEADERS.length; i++) {
				Cell cell = header.createCell(i);
				cell.setCellValue(EXCEL_HEADERS[i]);
				cell.setCellStyle(headerStyle);
			}

			int rowIndex = 1;
			for (StudentFee fee : fees) {
				Row row = sheet.createRow(rowIndex++);
				Student student = fee.getStudent();
				FeeStructure structure = fee.getFeeStructure();
				row.createCell(0).setCellValue(fee.getId());
				row.createCell(1).setCellValue(student.getAdmissionNumber());
				row.createCell(2).setCellValue(student.getId());
				row.createCell(3).setCellValue(student.getStudentUser() != null ? student.getStudentUser().getFullName()
						: null);
				row.createCell(4).setCellValue(structure.getClassRoom().getClassName());
				row.createCell(5).setCellValue(structure.getFeeType().name());
				writeMoney(row, 6, fee.getAmount(), moneyStyle);
				writeMoney(row, 7, fee.getPaidAmount(), moneyStyle);
				writeMoney(row, 8, fee.getDueAmount(), moneyStyle);
				writeDate(row, 9, fee.getFeeDate(), dateStyle);
				writeDate(row, 10, fee.getDueDate(), dateStyle);
				row.createCell(11).setCellValue(fee.getStatus().name());
			}

			sheet.createFreezePane(0, 1);
			sizeColumns(sheet, rowIndex);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new ServiceException("Unable to generate student fee excel file: " + e.getMessage());
		}
	}

	private static void writeMoney(Row row, int columnIndex, BigDecimal amount, CellStyle style) {
		Cell cell = row.createCell(columnIndex);
		if (amount != null) {
			cell.setCellValue(amount.doubleValue());
			cell.setCellStyle(style);
		}
	}

	private static void writeDate(Row row, int columnIndex, LocalDate date, CellStyle style) {
		Cell cell = row.createCell(columnIndex);
		if (date != null) {
			cell.setCellValue(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			cell.setCellStyle(style);
		}
	}

	private static void sizeColumns(Sheet sheet, int rowCount) {
		for (int column = 0; column < EXCEL_HEADERS.length; column++) {
			int maxLength = EXCEL_HEADERS[column].length();
			for (int row = 0; row < rowCount; row++) {
				Cell cell = sheet.getRow(row).getCell(column);
				if (cell != null && cell.getCellType() == CellType.STRING) {
					maxLength = Math.max(maxLength, cell.getStringCellValue().length());
				}
			}
			sheet.setColumnWidth(column, Math.min(60, Math.max(12, maxLength + 2)) * 256);
		}
	}

	@Transactional(readOnly = true)
	public PageResponse<PaymentResponse> getDailyFees(LocalDate today, Pageable pageable) {
		log.info("Fetching student fee by id: {}, {}", today, pageable);
		log.info("Fetching daily student fees: today={}, pageable={}", today, pageable);

		if (today == null) {
			today = LocalDate.now();
		}
		Specification<Payment> specification = byFilters(null, null, today, PaymentStatus.SUCCESS);
		Page<Payment> page = paymentRepository.findAll(specification, pageable);
		return PageResponse.from(page.map(this::mapToPaymentResponse));
	}

	public void generateFees(FeeGenerateRequest request) {
		log.info("Generating fees for class id: {} and academic year id: {}", request.getStudentId(),
				request.getFeeStructureId());
		FeeStructure structures = feeStructureRepository.findById(request.getFeeStructureId()).orElseThrow(
				() -> new ResourceNotFoundException("FeeStructure not found by Id :: " + request.getFeeStructureId()));
		Student students = studentRepository.findById(request.getStudentId()).orElseThrow(
				() -> new ResourceNotFoundException("Student not found by Id :: " + request.getStudentId()));
		StudentFee fee = new StudentFee();
		fee.setFeeStructure(structures);
		fee.setStudent(students);
		fee.setFeeDate(request.getFeeDate());
		fee.setAmount(structures.getAmount());
		fee.setDueAmount(structures.getAmount());
		fee.setStatus(FeeStatus.PENDING);
		studentFeeRepository.save(fee);
	}

	public PaymentResponse recordPayment(Long studentFeeId, FeePaymentRequest request) {
		log.info("Recording payment of {} for student fee id: {}", request.getAmount(), studentFeeId);
		StudentFee studentFee = findStudentFee(studentFeeId);

		if (studentFee.getStatus() == FeeStatus.PAID) {
			throw new ServiceException("Student fee " + studentFeeId + " is already fully paid");
		}
		if (request.getAmount().compareTo(studentFee.getDueAmount()) > 0) {
			throw new IllegalArgumentException("Payment amount exceeds due amount: " + studentFee.getDueAmount());
		}

		studentFee.setPaidAmount(studentFee.getPaidAmount().add(request.getAmount()));
		studentFee.setDueAmount(studentFee.getDueAmount().subtract(request.getAmount()));
		studentFee.setStatus(
				studentFee.getDueAmount().compareTo(BigDecimal.ZERO) == 0 ? FeeStatus.PAID : FeeStatus.PARTIAL);

		Payment payment = paymentRepository.save(Payment.builder().receiptNumber(generateReceiptNumber())
				.studentFee(studentFee).totalAmount(request.getAmount()).paymentMode(request.getPaymentMode())
				.transactionId(request.getTransactionId()).remark(request.getRemark()).status(PaymentStatus.SUCCESS)
				.paidAt(LocalDateTime.now()).build());
		log.info("Recorded payment receipt: {} for student fee id: {}", payment.getReceiptNumber(), studentFeeId);
		return mapToPaymentResponse(payment);
	}

	@Transactional(readOnly = true)
	public List<PaymentResponse> getPayments(Long studentFeeId) {
		log.info("Fetching payments for student fee id: {}", studentFeeId);
		findStudentFee(studentFeeId);
		return paymentRepository.findAllByStudentFee_IdOrderByPaidAtDesc(studentFeeId).stream()
				.map(this::mapToPaymentResponse).toList();
	}

	@Transactional(readOnly = true)
	public PageResponse<PaymentResponse> getAllPayment(Long studentId, Long classId, LocalDate feeDate,
			PaymentStatus paymentStatus, Pageable pageable) {
		log.info("Fetching payments for student fee id: {}, {}, {}", studentId, classId, feeDate);
		Specification<Payment> specification = byFilters(studentId, classId, feeDate, paymentStatus);
		Page<Payment> page = paymentRepository.findAll(specification, pageable);
		return PageResponse.from(page.map(this::mapToPaymentResponse));
	}

	public int markOverdue() {
		log.info("Marking overdue student fees as of date: {}", LocalDate.now());
		List<StudentFee> overdueFees = studentFeeRepository
				.findByStatusInAndDueDateBefore(List.of(FeeStatus.PENDING, FeeStatus.PARTIAL), LocalDate.now());
		overdueFees.forEach(fee -> fee.setStatus(FeeStatus.OVERDUE));
		studentFeeRepository.saveAll(overdueFees);
		log.info("Marked {} student fees as overdue", overdueFees.size());
		return overdueFees.size();
	}

	public void delete(Long id) {
		log.info("Deleting student fee id: {}", id);
		StudentFee studentFee = findStudentFee(id);
		if (!paymentRepository.findAllByStudentFee_IdOrderByPaidAtDesc(id).isEmpty()) {
			throw new ServiceException("Cannot delete student fee " + id + " because payments exist against it");
		}
		studentFeeRepository.delete(studentFee);
		log.info("Deleted student fee id: {}", id);
	}

	private StudentFee findStudentFee(Long id) {
		return studentFeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Student fee not found: " + id));
	}

	private SchoolClass findClass(Long classId) {
		return schoolClassRepository.findById(classId)
				.orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));
	}

	private LocalDate computeDueDate(Integer dueMonth) {
		LocalDate dueDate = LocalDate.now();
		return dueDate.withDayOfMonth(dueDate.lengthOfMonth());
	}

	private String generateReceiptNumber() {
		String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
		return "RCPT-" + System.currentTimeMillis() + "-" + random;
	}

	private StudentFeeResponse mapToResponse(StudentFee studentFee) {
		FeeStructure structure = studentFee.getFeeStructure();
		Student student = studentFee.getStudent();
		return StudentFeeResponse.builder().id(studentFee.getId()).studentId(student.getId())
				.studentName(student.getStudentUser() != null ? student.getStudentUser().getFullName() : null)
				.classId(structure.getClassRoom().getId()).className(structure.getClassRoom().getClassName())
				.feeStructureId(structure.getId()).feeType(structure.getFeeType()).amount(studentFee.getAmount())
				.paidAmount(studentFee.getPaidAmount()).dueAmount(studentFee.getDueAmount())
				.dueDate(studentFee.getDueDate()).status(studentFee.getStatus()).build();
	}

	private PaymentResponse mapToPaymentResponse(Payment payment) {
		return PaymentResponse.builder().id(payment.getId()).receiptNumber(payment.getReceiptNumber())
				.studentId(payment.getStudentFee().getStudent().getId())
				.studentFeeId(payment.getStudentFee() != null ? payment.getStudentFee().getId() : null)
				.totalAmount(payment.getTotalAmount()).paymentMode(payment.getPaymentMode())
				.transactionId(payment.getTransactionId()).remark(payment.getRemark()).status(payment.getStatus())
				.paidAt(payment.getPaidAt()).build();
	}

	public static Specification<Payment> byFilters(Long studentId, Long classId, LocalDate feeDate,
			PaymentStatus status) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (studentId != null) {
				predicates.add(cb.equal(root.get("studentFee").get("student").get("id"), studentId));
			}
			if (classId != null) {
				predicates.add(cb.equal(root.get("studentFee").get("student").get("schoolClass").get("id"), classId));
			}
			if (feeDate != null) {
				predicates.add(cb.equal(root.get("studentFee").get("feeDate"), feeDate));
			}
			if (status != null) {
				predicates.add(cb.equal(root.get("status"), status));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public static Specification<StudentFee> byFilters(Long classId, Long studentId, FeeStatus status) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			// Optional class filter. StudentFee -> Student -> SchoolClass
			if (classId != null) {
				Join<StudentFee, Student> student = root.join("student", JoinType.INNER);
				Join<Student, SchoolClass> schoolClass = student.join("schoolClass", JoinType.INNER);
				predicates.add(cb.equal(schoolClass.get("id"), classId));
			}
			// Optional student filter.
			if (studentId != null) {
				predicates.add(cb.equal(root.get("student").get("id"), studentId));
			}
			// Optional status filter.
			if (status != null) {
				predicates.add(cb.equal(root.get("status"), status));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
