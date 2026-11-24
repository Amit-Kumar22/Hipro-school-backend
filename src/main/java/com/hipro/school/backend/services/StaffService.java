package com.hipro.school.backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hipro.school.backend.dtos.AccessPermissionResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.PermissionRequest;
import com.hipro.school.backend.dtos.StaffAccessControlResponse;
import com.hipro.school.backend.dtos.StaffPermissionRequest;
import com.hipro.school.backend.dtos.UserResponse;
import com.hipro.school.backend.entities.AccessAttribute;
import com.hipro.school.backend.entities.AccessPermission;
import com.hipro.school.backend.entities.StaffAccessControl;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.enums.AccessAttributeName;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.AccessAttributeRepository;
import com.hipro.school.backend.repositories.AccessPermissionRepository;
import com.hipro.school.backend.repositories.StaffAccessControlRepository;
import com.hipro.school.backend.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StaffService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private StaffAccessControlRepository staffAccessControlRepository;

	@Autowired
	private AccessAttributeRepository accessAttributeRepository;

	@Autowired
	private AccessPermissionRepository accessPermissionRepository;

	@Transactional
	public PageResponse<StaffAccessControlResponse> getAllStaffPermission(Pageable pageable) {
		log.info(">>getAllStaffPermission({})", pageable);
		Page<StaffAccessControlResponse> staffs = staffAccessControlRepository.findAll(pageable)
				.map(this::mapToResponse);
		return PageResponse.from(staffs);
	}

	@Transactional
	public List<StaffAccessControlResponse> getUserStaffPermission(String email) {
		log.info(">> getUserStaffPermission(email={})", email);
		return staffAccessControlRepository.findAllByStaffEmail(email).stream().map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	public StaffAccessControlResponse getStaffByPermissionId(Long id) {
		log.info("Fetching staff permissions by id: {}", id);
		StaffAccessControl sc = staffAccessControlRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Staff Access Control Not Found Please Add Permissions "));
		return mapToResponse(sc);
	}

	@Transactional
	public StaffAccessControl addStaffPermissions(StaffPermissionRequest dto) {
		log.info("Adding staff permissions for user id: {}", dto.getUserId());

		// 1. Validate User
		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

		// 2. Create StaffAccessControl
		StaffAccessControl accessControl = new StaffAccessControl();
		accessControl.setStaff(user);
		staffAccessControlRepository.save(accessControl);

		// 3. Map Permissions
		List<AccessPermission> permissionList = dto.getPermissions().stream().map(p -> {

			AccessAttribute attribute = accessAttributeRepository.findById(p.getAttributeId())
					.orElseThrow(() -> new RuntimeException("Attribute not found: " + p.getAttributeId()));

			AccessPermission permission = new AccessPermission();
			permission.setStaffAccessControl(accessControl);
			permission.setAttribute(attribute);
			permission.setCanAdd(p.isCanAdd());
			permission.setCanUpdate(p.isCanUpdate());
			permission.setCanDelete(p.isCanDelete());
			permission.setCanRead(p.isCanRead());

			return permission;
		}).collect(Collectors.toList());

		// 4. Save All Permissions
		accessPermissionRepository.saveAll(permissionList);

		accessControl.setPermissions(permissionList);
		return accessControl;
	}

	@Transactional
	public StaffAccessControl updateStaffPermissions(StaffPermissionRequest dto) {
		log.info("Updating staff permissions for user id: {}", dto.getUserId());

		// 1. Validate User
		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

		// 2. Get Existing StaffAccessControl
		StaffAccessControl accessControl = staffAccessControlRepository.findByStaff(user).orElseThrow(
				() -> new RuntimeException("Staff access control not found for userId: " + dto.getUserId()));

		// 3. Fetch Existing Permissions from DB
		List<AccessPermission> existingPermissions = accessPermissionRepository.findByStaffAccessControl(accessControl);

		// Convert existing permissions to Map(attributeId -> permission)
		Map<Long, AccessPermission> existingMap = existingPermissions.stream()
				.collect(Collectors.toMap(p -> p.getAttribute().getId(), p -> p));

		List<AccessPermission> updatedPermissions = new ArrayList<>();

		// 4. Update or Insert Permissions
		for (PermissionRequest p : dto.getPermissions()) {

			AccessAttribute attribute = accessAttributeRepository.findById(p.getAttributeId())
					.orElseThrow(() -> new RuntimeException("Attribute not found: " + p.getAttributeId()));

			AccessPermission permission;

			if (existingMap.containsKey(p.getAttributeId())) {
				// Update existing permission
				permission = existingMap.get(p.getAttributeId());
			} else {
				// Create new permission
				permission = new AccessPermission();
				permission.setStaffAccessControl(accessControl);
				permission.setAttribute(attribute);
			}

			permission.setCanAdd(p.isCanAdd());
			permission.setCanUpdate(p.isCanUpdate());
			permission.setCanDelete(p.isCanDelete());
			permission.setCanRead(p.isCanRead());
			updatedPermissions.add(permission);
		}

		// 5. OPTIONAL: Remove permissions not present in request
		List<Long> requestAttributeIds = dto.getPermissions().stream().map(PermissionRequest::getAttributeId)
				.collect(Collectors.toList());

		List<AccessPermission> toDelete = existingPermissions.stream()
				.filter(p -> !requestAttributeIds.contains(p.getAttribute().getId())).collect(Collectors.toList());

		accessPermissionRepository.deleteAll(toDelete);

		// 6. Save updated permissions
		accessPermissionRepository.saveAll(updatedPermissions);
		accessControl.setPermissions(updatedPermissions);
		return accessControl;
	}

	@Transactional
	public AccessPermissionResponse getAccessPermission(String email, AccessAttributeName attributeName) {
		log.info(">> getAccessPermission(email={}, attribute={})", email, attributeName);

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

		AccessPermission permission = staffAccessControlRepository.findByStaff(user).map(StaffAccessControl::getPermissions)
				.orElse(List.of()).stream()
				.filter(p -> p.getAttribute() != null && p.getAttribute().getAttributeName() == attributeName)
				.findFirst().orElse(null);

		if (permission == null) {
			return AccessPermissionResponse.denied(attributeName);
		}

		return AccessPermissionResponse.of(attributeName, permission.isCanAdd(), permission.isCanUpdate(),
				permission.isCanDelete(), permission.isCanRead());
	}

	public StaffAccessControlResponse mapToResponse(StaffAccessControl entity) {

		StaffAccessControlResponse res = new StaffAccessControlResponse();
		res.setId(entity.getId());
		res.setCreated(entity.getCreated());
		res.setUpdated(entity.getUpdated());
		// map staff
		UserResponse userRes = modelMapper.map(entity.getStaff(), UserResponse.class);
		res.setStaff(userRes);
		// map permissions
		res.setPermissions(entity.getPermissions());
		return res;
	}

}
