package com.bagas.pinjam100.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingCustomerRegistration {

    private String fullName;
    private String nationalId;
    private String email;
    private String phoneNumber;
    private String password;
}