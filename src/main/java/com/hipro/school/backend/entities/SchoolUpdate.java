package com.hipro.school.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class SchoolUpdate {

    @NotBlank(message = "Update name is required")
    @Size(max = 150)
    @Column(name = "update_name", length = 150)
    private String name;

    @NotBlank(message = "Update description is required")
    @Size(max = 500)
    @Column(name = "update_description", length = 500)
    private String description;
}