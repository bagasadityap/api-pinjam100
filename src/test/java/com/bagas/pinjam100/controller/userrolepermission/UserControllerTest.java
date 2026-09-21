package com.bagas.pinjam100.controller.userrolepermission;

import com.bagas.pinjam100.dto.request.userrolepermission.UserRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.UserResponse;
import com.bagas.pinjam100.service.userrolepermission.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserControllerTest")
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private UUID userId;
    private UUID roleId;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        userId = UUID.randomUUID();
        roleId = UUID.randomUUID();
        userResponse = new UserResponse();
    }

    @Nested
    @DisplayName("getAll")
    class GetAllTest {

        @Test
        @DisplayName("should return list of users successfully")
        void shouldReturnListOfUsers() throws Exception {
            when(userService.findAllByDeletedDateIsNull()).thenReturn(List.of(userResponse));

            mockMvc.perform(get("/user"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Data user berhasil diambil"))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("should return user when id exists")
        void shouldReturnUserWhenExists() throws Exception {
            when(userService.findByIdAndDeletedDateIsNull(userId)).thenReturn(userResponse);

            mockMvc.perform(get("/user/{id}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Data user berhasil diambil"));
        }
    }

    @Nested
    @DisplayName("create")
    class CreateTest {

        @Test
        @DisplayName("should create user successfully")
        void shouldCreateUserSuccessfully() throws Exception {
            UserRequest request = new UserRequest();
            when(userService.save(any(UserRequest.class))).thenReturn(userResponse);

            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("User berhasil dibuat"));
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("should update user successfully")
        void shouldUpdateUserSuccessfully() throws Exception {
            UserRequest request = new UserRequest();
            when(userService.update(eq(userId), any(UserRequest.class))).thenReturn(userResponse);

            mockMvc.perform(put("/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User berhasil diperbarui"));
        }
    }

    @Nested
    @DisplayName("updateActive")
    class UpdateActiveTest {

        @Test
        @DisplayName("should update user active status successfully")
        void shouldUpdateActiveSuccessfully() throws Exception {
            when(userService.updateActive(userId)).thenReturn(userResponse);

            mockMvc.perform(patch("/user/{id}/active", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Status user berhasil diperbarui"));
        }
    }

    @Nested
    @DisplayName("updateRole")
    class UpdateRoleTest {

        @Test
        @DisplayName("should update user role successfully")
        void shouldUpdateRoleSuccessfully() throws Exception {
            when(userService.changeRole(userId, roleId)).thenReturn(userResponse);

            mockMvc.perform(patch("/user/{id}/role", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(roleId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Role user berhasil diperbarui"));
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("should delete user successfully")
        void shouldDeleteUserSuccessfully() throws Exception {
            when(userService.delete(userId)).thenReturn(userResponse);

            mockMvc.perform(delete("/user/{id}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User berhasil dihapus"));
        }
    }
}
