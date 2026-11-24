package com.hipro.school.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "access_permission")
public class AccessPermission extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "staff_access_control_id", nullable = false)
	@JsonIgnore
	private StaffAccessControl staffAccessControl;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "access_attribute_id", nullable = false)
	private AccessAttribute attribute;

	private boolean canAdd;
	private boolean canUpdate;
	private boolean canDelete;
	private boolean canRead;

}
