package com.hipro.school.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hipro.school.backend.dtos.AddMemberRequest;
import com.hipro.school.backend.dtos.ApiResponse;
import com.hipro.school.backend.dtos.ChatMessageResponse;
import com.hipro.school.backend.dtos.CreateGroupRequest;
import com.hipro.school.backend.dtos.GroupMessageResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.SendMessageRequest;
import com.hipro.school.backend.dtos.StudentResponse;
import com.hipro.school.backend.entities.ChatRoom;
import com.hipro.school.backend.security.AuthDetailsProvider;
import com.hipro.school.backend.services.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Chat APIs", description = "APIs for chat groups, members and messages")
@RestController
@RequestMapping("/v1/chat")
@Slf4j
public class ChatController {

	@Autowired
	private ChatService chatService;

	@Operation(summary = "Get students by section", description = "Fetches all students for the section represented by the sectionId query param for the current logged-in user.")
	@GetMapping("/all-student")
	public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudentDetailByClass(@RequestParam Long sectionId) {
		log.info(">> getAllStudentDetailByClass({})", sectionId);
		String email = AuthDetailsProvider.getLoggedEmail();
		List<StudentResponse> list = chatService.getAllStudentDetailByClass(email, sectionId);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK, "All Students by class fetched successfully.", list));
	}

	@Operation(summary = "Get all chat groups", description = "Fetches a paginated list of chat groups. Requires the schoolCode query param.")
	@GetMapping("/groups")
	public ResponseEntity<PageResponse<ChatRoom>> getAllGroups(Pageable pageable) {
		log.info(">> getAllGroups({})", pageable);
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(chatService.getAllGroups(email, pageable));
	}

	@Operation(summary = "Create a chat group", description = "Creates a new chat group for the current logged-in user using the CreateGroupRequest request body.")
	@PostMapping("/group")
	public ResponseEntity<ApiResponse<Void>> createGroup(@RequestBody CreateGroupRequest request) {
		log.info(">> createGroup({})", request);
		String email = AuthDetailsProvider.getLoggedEmail();
		chatService.createGroup(request, email);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(HttpStatus.CREATED, "Group created successfully.", null));
	}

	@Operation(summary = "Send a group message", description = "Sends a message to a chat group for the current logged-in user using the SendMessageRequest request body.")
	@PostMapping("/group-message-send")
	public ResponseEntity<ChatMessageResponse> groupSendMessage(@RequestBody SendMessageRequest request) {
		log.info(">> groupSendMessage({})", request);
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(chatService.groupSendMessage(request, email));
	}

	@Operation(summary = "Add a member to a chat group", description = "Adds a member to a chat group for the current logged-in user using the AddMemberRequest request body.")
	@PostMapping("/group-add-member")
	public ResponseEntity<ApiResponse<Void>> addGroupMemeber(@RequestBody AddMemberRequest request) {
		log.info(">> addGroupMember({})", request);
		String email = AuthDetailsProvider.getLoggedEmail();
		chatService.addGroupMember(request, email);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(HttpStatus.CREATED, "Member added successfully.", null));
	}

	@Operation(summary = "Get group messages", description = "Fetches a paginated list of messages for a chat group. Requires the roomId query param.")
	@GetMapping("/group-messages")
	public ResponseEntity<GroupMessageResponse> getMessages(@RequestParam Long roomId, Pageable pageable) {
		log.info(">> getMessages({}, {})", roomId, pageable);
		String email = AuthDetailsProvider.getLoggedEmail();
		return ResponseEntity.ok(chatService.getGroupMessages(roomId, email, pageable));
	}
}
