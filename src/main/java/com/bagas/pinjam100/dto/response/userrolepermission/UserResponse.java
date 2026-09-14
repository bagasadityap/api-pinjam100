package com.bagas.pinjam100.dto.response.userrolepermission;

import com.bagas.pinjam100.entity.userrolepermission.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    UUID id;
    String identityNumber;
    String name;
    String status;
    String role;

    public UserResponse(User user) {
        this.id = user.getId();
        this.identityNumber = user.getIdentityNumber();
        this.name = user.getName();
        this.status = user.getStatus().toString();
        if (user.getRole() != null) {
            this.role = user.getRole().getRoleName();
        }
    }
}
