package com.bagas.pinjam100.dto.request.userrolepermission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String identityNumber;
    private String name;
    private String password;
    private Boolean status;
    private String role;
    private String branch;
}
