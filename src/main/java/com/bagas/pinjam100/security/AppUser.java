package com.bagas.pinjam100.security;

import com.bagas.pinjam100.entity.Branch;
import com.bagas.pinjam100.entity.userrolepermission.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class AppUser implements UserDetails {
    private UUID idUser;
    private String identityNumber;
    private String password;
    private boolean status;
    private Role role;
    private Branch branch;

    private List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    public AppUser() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return identityNumber;
    }

    @Override
    public boolean isEnabled() {
        return status;
    }
}