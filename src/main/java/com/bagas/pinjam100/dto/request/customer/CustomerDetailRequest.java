package com.bagas.pinjam100.dto.request.customer;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailRequest {
    private String nationalId;
    private LocalDate birthDate;
    private String placeOfBirth;
    private String gender;
    private String address;
    private String province;
    private String city;
    private String district;
    private String village;
    private String postalCode;
}