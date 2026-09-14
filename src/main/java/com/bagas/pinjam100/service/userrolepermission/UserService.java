package com.bagas.pinjam100.service.userrolepermission;

import com.bagas.pinjam100.dto.request.userrolepermission.UserRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.UserResponse;
import com.bagas.pinjam100.entity.userrolepermission.Role;
import com.bagas.pinjam100.entity.userrolepermission.User;
import com.bagas.pinjam100.exception.ConflictException;
import com.bagas.pinjam100.repository.userrolepermission.RoleRepository;
import com.bagas.pinjam100.repository.userrolepermission.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    public List<UserResponse> findAllByDeletedDateIsNull() {
        return userRepository.findAllByDeletedDateIsNull()
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    public UserResponse findById(UUID id) {
        User response = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User tidak ditemukan"));
        return new UserResponse(response);
    }

    public UserResponse findByIdAndDeletedDateIsNull(UUID id) {
        User response = userRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("User tidak ditemukan"));
        return new UserResponse(response);
    }

    public UserResponse save(UserRequest request) {

        if (userRepository.existsByIdentityNumber(request.getIdentityNumber())) {
            if (userRepository.existsByIdentityNumber(request.getIdentityNumber())) {
                throw new ConflictException("Nomor identitas sudah terdaftar");
            }
        }

        User user = new User();
        user.setIdentityNumber(request.getIdentityNumber());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(true);
        user.setRole(
                roleRepository.findByIdAndDeletedDateIsNull(UUID.fromString(request.getRole()))
                        .orElseThrow(() ->
                                new EntityNotFoundException("Role tidak ditemukan")
                        )
        );

        userRepository.save(user);

        return new UserResponse(user);
    }

    public UserResponse update(UUID id, UserRequest request) {
        User user = userRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("User tidak ditemukan"));

        user.setName(request.getName());
        user.setIdentityNumber(request.getIdentityNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(request.getStatus());
        user.setRole(
                roleRepository.findByIdAndDeletedDateIsNull(UUID.fromString(request.getRole()))
                        .orElseThrow(() -> new EntityNotFoundException("Role tidak ditemukan"))
        );
        userRepository.save(user);

        return new UserResponse(user);
    }

    public UserResponse updateActive(UUID id) {
        User user = userRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("User tidak ditemukan"));

        user.setStatus(!user.getStatus());
        userRepository.save(user);
        return new UserResponse(user);
    }
    public UserResponse changeRole(UUID id, UUID roleId) {
        User user = userRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("User tidak ditemukan"));

        user.setRole(
                roleRepository.findByIdAndDeletedDateIsNull(roleId)
                        .orElseThrow(() -> new EntityNotFoundException("Role tidak ditemukan"))
        );

        userRepository.save(user);
        return new UserResponse(user);
    }

    public UserResponse delete(UUID id) {
        User user = userRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("User tidak ditemukan"));

        user.setDeletedDate(LocalDateTime.now());
        userRepository.save(user);
        return new UserResponse(user);
    }
}
