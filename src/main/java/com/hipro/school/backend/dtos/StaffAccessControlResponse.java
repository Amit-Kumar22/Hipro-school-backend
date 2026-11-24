package com.hipro.school.backend.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.hipro.school.backend.entities.AccessPermission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffAccessControlResponse {

	private Long id;

	private LocalDateTime created;

	private LocalDateTime updated;

	private UserResponse staff;

	private List<AccessPermission> permissions = new ArrayList<>();

}
