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
public class AnnNoticeBoard {

    @NotBlank(message = "Board name is required")
    @Size(max = 100)
    @Column(name = "board_name")
    private String boardName;

    @Size(max = 300)
    @Column(name = "board_description")
    private String description;
}