package com.bagas.pinjam100.controller.userrolepermission;

import com.bagas.pinjam100.dto.request.userrolepermission.RoleRequest;
import com.bagas.pinjam100.dto.request.userrolepermission.UserRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.UserResponse;
import com.bagas.pinjam100.service.userrolepermission.UserService;
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
    public List<UserResponse> getAll() {
        return userService.findAllByDeletedDateIsNull();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return userService.findByIdAndDeletedDateIsNull(id);
    }

    @PostMapping
    public UserResponse create(@RequestBody UserRequest userRequest) {
        return userService.save(userRequest);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable UUID id, @RequestBody UserRequest userRequest) {
        return userService.update(id, userRequest);
    }

    @PatchMapping("/{id}/active")
    public UserResponse updateActive(@PathVariable UUID id) {
        return userService.updateActive(id);
    }

    @PatchMapping("/{id}/role")
    public UserResponse updateRole(@PathVariable UUID id, @RequestBody UUID roleId) {
        return userService.changeRole(id, roleId);
    }

    @DeleteMapping("/{id}")
    public UserResponse delete(@PathVariable UUID id) {
        return userService.delete(id);
    }
}
