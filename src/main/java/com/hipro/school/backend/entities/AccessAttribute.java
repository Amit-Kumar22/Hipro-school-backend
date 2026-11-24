package com.hipro.school.backend.entities;

import com.hipro.school.backend.enums.AccessAttributeName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "access_attribute")
public class AccessAttribute extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(name = "attribute_name", unique = true, nullable = false)
	private AccessAttributeName attributeName;

}
