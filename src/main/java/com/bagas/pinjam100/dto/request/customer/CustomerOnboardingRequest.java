package com.bagas.pinjam100.dto.request.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOnboardingRequest {

    private String nationalId;
    private String birthDate;
    private String placeOfBirth;
    private String gender;
    private String address;
    private String province;
    private String city;
    private String district;
    private String village;
    private String postalCode;

    private String employmentType;
    private String companyName;
    private String position;
    private Long monthlyIncome;
    private String startDate;
    private String companyAddress;
    private String companyPhone;

    private List<RekeningRequest> rekenings;
}