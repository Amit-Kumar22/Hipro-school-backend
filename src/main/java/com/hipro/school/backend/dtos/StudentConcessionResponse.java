package com.hipro.school.backend.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hipro.school.backend.enums.ConcessionStatus;
import com.hipro.school.backend.enums.ConcessionType;
import com.hipro.school.backend.enums.DiscountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentConcessionResponse {

    private Long id;

    private Long studentId;

    private Long feeStructureId;

    private ConcessionType type;

    private DiscountType discountType;

    private BigDecimal discountValue;

    private ConcessionStatus status;

    private String remarks;

    private LocalDateTime createdAt;
    
    private LocalDateTime updated;
}