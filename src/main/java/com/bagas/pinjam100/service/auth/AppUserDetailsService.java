package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.entity.userrolepermission.User;
import com.bagas.pinjam100.repository.userrolepermission.UserRepository;
import com.bagas.pinjam100.security.AppUser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @NonNull
    @Override
    public AppUser loadUserByUsername(String identityNumber) throws UsernameNotFoundException {
        Optional<AppUser> optionalUser = findUser(identityNumber);

        return optionalUser.orElseThrow(
                () -> new UsernameNotFoundException("Pengguna tidak dikenal: " + identityNumber)
        );
    }

    public Optional<AppUser> findUser(String identityNumber) {
        return userRepository
                .findByIdentityNumberWithRoleAndPermissions(identityNumber)
                .filter(user -> user.getPassword() != null)
                .map(this::toAppUser);
    }

    private AppUser toAppUser(User user) {
        AppUser appUser = new AppUser();
        appUser.setIdUser(user.getId());
        appUser.setIdentityNumber(user.getIdentityNumber());
        appUser.setPassword(user.getPassword());
        appUser.setStatus(Boolean.TRUE.equals(user.getStatus()));
        appUser.setRole(user.getRole());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (user.getRole() != null) {
            if (user.getRole().getRoleName() != null) {
                authorities.add(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().getRoleName()
                        )
                );
            }

            if (user.getRole().getRolePermissions() != null) {
                user.getRole().getRolePermissions().forEach(rolePermission -> {
                    if (rolePermission.getPermission() != null
                            && rolePermission.getPermission().getPermissionName() != null) {
                        authorities.add(
                                new SimpleGrantedAuthority(
                                        rolePermission.getPermission().getPermissionName()
                                )
                        );
                    }
                });
            }
        }

        appUser.setAuthorities(authorities);
        return appUser;
    }
}