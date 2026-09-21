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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    public static final String CACHE_USER = "user";
    public static final String CACHE_USER_ALL = "user_all";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Cacheable(cacheNames = CACHE_USER_ALL, key = "'all_active'")
    public List<UserResponse> findAllByDeletedDateIsNull() {
        return userRepository.findAllByDeletedDateIsNull()
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    @Cacheable(cacheNames = CACHE_USER, key = "#id")
    public UserResponse findByIdAndDeletedDateIsNull(UUID id) {
        User response = userRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("User tidak ditemukan"));
        return new UserResponse(response);
    }

    @CacheEvict(cacheNames = CACHE_USER_ALL, key = "'all_active'")
    public UserResponse save(UserRequest request) {
        if (userRepository.existsByIdentityNumber(request.getIdentityNumber())) {
            throw new ConflictException("Nomor identitas sudah terdaftar");
        }

        User user = new User();
        user.setIdentityNumber(request.getIdentityNumber());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(true);
        user.setRole(
                roleRepository.findByIdAndDeletedDateIsNull(UUID.fromString(request.getRole()))
                        .orElseThrow(() -> new EntityNotFoundException("Role tidak ditemukan"))
        );

        userRepository.save(user);
        return new UserResponse(user);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_USER, key = "#id"),
            @CacheEvict(cacheNames = CACHE_USER_ALL, key = "'all_active'")
    })
    public UserResponse update(UUID id, UserRequest request) {
        User user = userRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("User tidak ditemukan"));

        // Validasi nomor identitas hanya jika diubah oleh user
        if (!user.getIdentityNumber().equals(request.getIdentityNumber()) &&
                userRepository.existsByIdentityNumber(request.getIdentityNumber())) {
            throw new ConflictException("Nomor identitas sudah terdaftar");
        }

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

    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_USER, key = "#id"),
            @CacheEvict(cacheNames = CACHE_USER_ALL, key = "'all_active'")
    })
    public UserResponse updateActive(UUID id) {
        User user = userRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("User tidak ditemukan"));

        user.setStatus(!user.getStatus());
        userRepository.save(user);
        return new UserResponse(user);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_USER, key = "#id"),
            @CacheEvict(cacheNames = CACHE_USER_ALL, key = "'all_active'")
    })
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

    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_USER, key = "#id"),
            @CacheEvict(cacheNames = CACHE_USER_ALL, key = "'all_active'")
    })
    public UserResponse delete(UUID id) {
        User user = userRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("User tidak ditemukan"));

        user.setDeletedDate(LocalDateTime.now());
        userRepository.save(user);
        return new UserResponse(user);
    }
}