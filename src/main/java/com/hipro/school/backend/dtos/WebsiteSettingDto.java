package com.hipro.school.backend.dtos;

import java.util.List;

import lombok.Data;

@Data
public class WebsiteSettingDto {
    private Long id;
    private String logoUrl;
    private String logosize;
    private String companyName;
    private String phoneNumber;
    private String emailId;
    private String whatsappNumber;
    private String gstNumber;
    private AddressDto officeAddress;
    private List<AddressDto> branchAddresses;
    private List<ContactDto> supportContacts;
}
