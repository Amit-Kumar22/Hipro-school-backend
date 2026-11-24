package com.hipro.school.backend.dtos;

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
public class LeadDto {

    private String schoolName;
    private String address;
    private String locality;
    private String city;
    private String state;
    private String pincode;

    private String phone;
    private String alternatePhone;
    private String email;
    private String website;

    private String category;
    private String board;
    private String schoolType;

    private String justdialUrl;
    private String justdialId;

    private Double rating;
    private Integer reviewCount;
}