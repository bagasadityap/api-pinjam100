package com.bagas.pinjam100.dto.response.customer;

import com.bagas.pinjam100.entity.customer.CustomerDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DetailResponse {
    private UUID id;
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

    public DetailResponse(CustomerDetail response) {
        this.id = response.getId();
        this.nationalId = response.getNationalId();
        this.birthDate = response.getBirthDate();
        this.placeOfBirth = response.getPlaceOfBirth();
        this.gender = response.getGender();
        this.address = response.getAddress();
        this.province = response.getProvince();
        this.city = response.getCity();
        this.district = response.getDistrict();
        this.village = response.getVillage();
        this.postalCode = response.getPostalCode();
    }
}