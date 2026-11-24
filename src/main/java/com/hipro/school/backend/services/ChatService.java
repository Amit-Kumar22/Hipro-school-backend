package com.hipro.school.backend.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hipro.school.backend.dtos.AddMemberRequest;
import com.hipro.school.backend.dtos.ChatMessageResponse;
import com.hipro.school.backend.dtos.CreateGroupRequest;
import com.hipro.school.backend.dtos.GroupMessageResponse;
import com.hipro.school.backend.dtos.PageResponse;
import com.hipro.school.backend.dtos.SendMessageRequest;
import com.hipro.school.backend.dtos.StudentResponse;
import com.hipro.school.backend.entities.ChatMessage;
import com.hipro.school.backend.entities.ChatRoom;
import com.hipro.school.backend.entities.ChatRoomMember;
import com.hipro.school.backend.entities.School;
import com.hipro.school.backend.entities.Student;
import com.hipro.school.backend.entities.User;
import com.hipro.school.backend.enums.ChatRoomType;
import com.hipro.school.backend.exceptions.ResourceNotFoundException;
import com.hipro.school.backend.repositories.ChatMessageRepository;
import com.hipro.school.backend.repositories.ChatRoomMemberRepo;
import com.hipro.school.backend.repositories.ChatRoomRepository;
import com.hipro.school.backend.repositories.SchoolRepository;
import com.hipro.school.backend.repositories.StudentRepository;
import com.hipro.school.backend.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatService {

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private ChatRoomRepository chatRoomRepo;

	@Autowired
	private ChatMessageRepository chatMessageRepo;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ChatRoomMemberRepo chatRoomMemberRepo;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private ModelMapper modelMapper;

	public PageResponse<ChatRoom> getAllGroups(String email, Pageable pageable) {
		log.info("Fetching all chat groups for user: {} in school: {}", email, pageable);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));
		Page<ChatRoom> chatRooms = schoolRepository.findUserChatRooms(user.getId(), pageable);
		return PageResponse.from(chatRooms);
	}

	public ChatRoom createGroup(CreateGroupRequest request, String email) {
		log.info("Creating chat group: {} by user: {}", request.getName(), email);
		ChatRoom room = new ChatRoom();
		User admin = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		School school = schoolRepository.findBySchoolCode(request.getSchoolCode())
				.orElseThrow(() -> new ResourceNotFoundException("School detail not found"));
		room.setAdmin(admin);
		room.setSchool(school);
		room.setName(request.getName());
		room.setType(ChatRoomType.GROUP);
		chatRoomRepo.save(room);

		// Prepare all member IDs (admin + users)
		Set<Long> allUserIds = new HashSet<>();
		allUserIds.add(admin.getId()); // admin
		allUserIds.addAll(request.getUserIds()); // users

		addMembersBulk(room, allUserIds, admin.getId());

		return room;
	}

	public void addGroupMember(AddMemberRequest request, String email) {
		log.info("Adding member(s) to chat room id: {} by user: {}", request.getChatRoomId(), email);
		User admin = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		ChatRoom room = chatRoomRepo.findById(request.getChatRoomId())
				.orElseThrow(() -> new ResourceNotFoundException("Chat room detail not found"));

		// Check if requester is admin of the group
		if (!room.getAdmin().getId().equals(admin.getId())) {
			throw new RuntimeException("Only group admin can add members");
		}
		addMembersBulk(room, new HashSet<>(request.getUserIds()), admin.getId());

	}

	public void addMembersBulk(ChatRoom room, Set<Long> userIds, Long adminId) {
		log.info("Adding {} members to chat room id: {}", userIds.size(), room.getId());
		// Fetch all users in one query
		List<User> users = userRepository.findAllById(userIds);
		if (users.isEmpty()) {
			throw new ResourceNotFoundException("Users not found");
		}

		// Fetch existing members to avoid duplicates
		List<Integer> existingUserIds = chatRoomMemberRepo.findExistingUserIds(room.getId(), userIds);
		Set<Integer> existingSet = new HashSet<>(existingUserIds);
		List<ChatRoomMember> membersToSave = new ArrayList<>();

		for (User user : users) {
			// Skip if already exists
			if (existingSet.contains(user.getId())) {
				continue;
			}
			ChatRoomMember member = new ChatRoomMember();
			member.setChatRoom(room);
			member.setUser(user);
			member.setIsAdmin(user.getId().equals(adminId));
			membersToSave.add(member);
		}

		// Bulk save
		if (!membersToSave.isEmpty()) {
			chatRoomMemberRepo.saveAll(membersToSave);
		}
	}

	public ChatRoom createGroupOld(CreateGroupRequest request, String email) {
		ChatRoom room = new ChatRoom();
		User admin = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		School school = schoolRepository.findBySchoolCode(request.getSchoolCode())
				.orElseThrow(() -> new ResourceNotFoundException("School detail not found"));
		room.setAdmin(admin);
		room.setSchool(school);
		room.setName(request.getName());
		room.setType(ChatRoomType.GROUP);
		chatRoomRepo.save(room);
		// Add admin
		// addMember(room, request.getAdminId(), true);
		// Add users
		for (Long userId : request.getUserIds()) {
			addMember(room, userId, false);
		}
		return room;
	}

	public void addMember(ChatRoom room, Long userId, boolean isAdmin) {
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		// Avoid duplicate
		if (chatRoomMemberRepo.existsByChatRoomIdAndUserId(room.getId(), userId)) {
			return;
		}
		ChatRoomMember member = new ChatRoomMember();
		member.setChatRoom(room);
		member.setUser(user);
		member.setIsAdmin(isAdmin);
		chatRoomMemberRepo.save(member);
	}

	public List<StudentResponse> getAllStudentDetailByClass(String email, Long sectionId) {
		log.info("Fetching students by section id: {}", sectionId);
		List<Student> students = studentRepository.findAll();
		// return students.stream().map(student -> modelMapper.map(student.getUser(),
		// StudentResponse.class)).toList();
		return null;
	}

	public ChatMessageResponse groupSendMessage(SendMessageRequest request, String email) {
		log.info("Sending group message by user: {} to chat room id: {}", email, request.getChatRoomId());
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));
		ChatRoom chatRoom = chatRoomRepo.findById(request.getChatRoomId())
				.orElseThrow(() -> new ResourceNotFoundException("Chat room detail not found."));
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setMessage(request.getMessage());
		chatMessage.setChatRoom(chatRoom);
		chatMessage.setSender(user);
		chatMessageRepo.save(chatMessage);
		return null;
	}

	public ChatMessageResponse sendMessage(SendMessageRequest request) {
		return null;
	}

	public GroupMessageResponse getGroupMessages(Long roomId, String email, Pageable pageable) {
		log.info("Fetching group messages for room id: {} by user: {}", roomId, email);
		// 1. Validate user
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// 2. Validate membership (recommended for production)
		boolean exists = chatRoomMemberRepo.existsByChatRoomIdAndUserId(roomId, user.getId());
		if (!exists) {
			throw new RuntimeException("User is not part of this chat room");
		}
		// 3. Fetch chat room
		ChatRoom chatRoom = chatRoomRepo.findById(roomId)
				.orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));
		// 4. Fetch messages (paged)
		Page<ChatMessage> page = chatMessageRepo.findByChatRoomId(roomId, pageable);
		// 5. Convert Entity → DTO
		List<ChatMessageResponse> messages = page.getContent().stream().map(msg -> {
			ChatMessageResponse res = new ChatMessageResponse();
			res.setMessageId(msg.getId());
			res.setSenderName(msg.getSender().getFullName()); // IMPORTANT
			res.setMessage(msg.getMessage());
			res.setCreatedAt(msg.getCreated());
			return res;
		}).toList();

		// 6. Return final response
		return new GroupMessageResponse(chatRoom, messages);
	}

	public ChatMessageResponse getNewMessages(Long roomId, Long userId, Long lastMessageId) {
		// TODO Auto-generated method stub
		return null;
	}

}
