package com.bagas.pinjam100.dto.request.customer;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;
}
