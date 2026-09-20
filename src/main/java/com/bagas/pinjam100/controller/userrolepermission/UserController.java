package com.bagas.pinjam100.controller.userrolepermission;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.userrolepermission.UserRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.UserResponse;
import com.bagas.pinjam100.service.userrolepermission.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<UserResponse>>> getAll() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data user berhasil diambil",
                        userService.findAllByDeletedDateIsNull()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data user berhasil diambil",
                        userService.findByIdAndDeletedDateIsNull(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<UserResponse>> create(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "User berhasil dibuat",
                        userService.save(userRequest)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponse>> update(@PathVariable UUID id, @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "User berhasil diperbarui",
                        userService.update(id, userRequest)
                )
        );
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<BaseResponse<UserResponse>> updateActive(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Status user berhasil diperbarui",
                        userService.updateActive(id)
                )
        );
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<BaseResponse<UserResponse>> updateRole(@PathVariable UUID id, @RequestBody UUID roleId) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Role user berhasil diperbarui",
                        userService.changeRole(id, roleId)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponse>> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "User berhasil dihapus",
                        userService.delete(id)
                )
        );
    }
}