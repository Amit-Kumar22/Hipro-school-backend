package com.hipro.school.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hipro.school.backend.dtos.AdminUserDto;
import com.hipro.school.backend.dtos.EmployeeListResponse;
import com.hipro.school.backend.dtos.MailRequest;
import com.hipro.school.backend.dtos.UserRequest;
import com.hipro.school.backend.entities.Role;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	@Autowired
	private ModelMapper modelMapper;

	@Transactional
	public void adminSaveUser(AdminUserDto userDto) {
		log.info("Admin saving user with email: {}", userDto.getEmail());
		User user = new User(); // user.setCompanyName(userDto.getCompanyName());
		user.setFullName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setPhone(userDto.getPhoneNumber());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));

		if (userDto.getRole() == null) {
			throw new IllegalArgumentException("Role cannot be null or empty.");
		}
		user.setRole(userDto.getRole());
		User savedUser = userRepository.save(user);

		// Only send email if user saved successfully
		if (savedUser.getId() != null) {
			MailRequest mailRequest = new MailRequest();
			mailRequest.setName(savedUser.getFullName());
			mailRequest.setToken(userDto.getPassword());
			mailRequest.setTo(savedUser.getEmail());
			mailRequest.setSubject("Welcome to Weekytrips World Tour");
			// Sending creds in email is not a best practice, consider sending a password
			// reset link instead
			// emailService.sendWelcomeEmail(mailRequest);
			log.info("Welcome email sent to: {}", savedUser.getEmail());
		} else {
			log.error("User creation failed, email not sent for: {}", userDto.getEmail());
			throw new RuntimeException("Failed to create user. Email not sent.");
		}

		log.info("User created successfully with email: {}", savedUser.getEmail());
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email).get();
	}

	@Transactional(readOnly = true)
	public List<EmployeeListResponse> getAllEmployee(Boolean isDeleted) {
		log.info("Fetching all employees excluding SUPERADMIN and CUSTOMER roles");

		// Determine which users to fetch based on deletion flag
		List<User> users = null;
		;

		// Filter roles and map to DTO
		return users.stream().filter(user -> user.getRole() != Role.SUPERADMIN && user.getRole() != Role.TEACHER)
				.map(user -> {
					EmployeeListResponse response = modelMapper.map(user, EmployeeListResponse.class);
					response.setRole("ROLE_" + user.getRole().name());
					return response;
				}).collect(Collectors.toList());
	}

	public EmployeeListResponse getByUserId(String email) {
		log.info("Fetching user by clientId: {}", email);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Lead not found - " + email));
		return modelMapper.map(user, EmployeeListResponse.class);
	}

	@Transactional
	public void updateUser(String email, UserRequest userRequest) {
		log.info("Updating user with email: {}", email);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("email address cannot be changed. - " + email));
		user.setFullName(userRequest.getName());
		user.setPhone(userRequest.getPhone());
	}

	@Transactional
	public void deleteUser(String email) {
		log.info("Deleting user with email: {}", email);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found - " + email));

	}

}