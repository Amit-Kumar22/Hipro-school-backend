package com.hipro.school.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private Long id;
    private String officeNo;
    private String buildingName;
    private String streetName;
    private String location;
    private String district;
    private String city;
    private String pin;
}
