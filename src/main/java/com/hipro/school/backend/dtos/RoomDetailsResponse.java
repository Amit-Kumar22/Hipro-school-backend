package com.hipro.school.backend.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDetailsResponse {
    private Long id;
    private String roomCategory;
    private String roomAmenities;
    private double sessionalActualCost;
    private double nonSessionalActualCost;
    private double sessionalTACost;
    private double nonSessionalTACost;
    private double sessionalGroupCost;
    private double nonSessionalGroupCost;
    private double cpCost;
    private double lunchCost;
    private double dinnerCost;
    private boolean vegAvailable;
    private boolean nonVegAvailable;
    private boolean jainFoodAvailable;
    private LocalDate offSeasonStartDate;
    private LocalDate offSeasonEndDate;
    private LocalDate seasonStartDate;
    private LocalDate seasonEndDate;
}
