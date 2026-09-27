package com.bagas.pinjam100.controller.userrolepermission;

import com.bagas.pinjam100.dto.request.userrolepermission.RolePermissionsRequest;
import com.bagas.pinjam100.dto.request.userrolepermission.RoleRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.RoleResponse;
import com.bagas.pinjam100.service.userrolepermission.RoleService;
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
@DisplayName("RoleControllerTest")
class RoleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private UUID roleId;
    private RoleResponse roleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();
        roleId = UUID.randomUUID();
        roleResponse = new RoleResponse();
    }

    @Nested
    @DisplayName("getAll")
    class GetAllTest {

        @Test
        @DisplayName("should return list of roles successfully")
        void shouldReturnListOfRoles() throws Exception {
            when(roleService.findAllByDeletedDateIsNull()).thenReturn(List.of(roleResponse));

            mockMvc.perform(get("/role"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Data role berhasil diambil"))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("should return role when id exists")
        void shouldReturnRoleWhenExists() throws Exception {
            when(roleService.findByIdAndDeletedDateIsNull(roleId)).thenReturn(roleResponse);

            mockMvc.perform(get("/role/{id}", roleId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Data role berhasil diambil"));
        }
    }

    @Nested
    @DisplayName("create")
    class CreateTest {

        @Test
        @DisplayName("should create role successfully")
        void shouldCreateRoleSuccessfully() throws Exception {
            RoleRequest request = new RoleRequest();
            when(roleService.save(any(RoleRequest.class))).thenReturn(roleResponse);

            mockMvc.perform(post("/role")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("Role berhasil dibuat"));
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("should update role successfully")
        void shouldUpdateRoleSuccessfully() throws Exception {
            RoleRequest request = new RoleRequest();
            when(roleService.update(eq(roleId), any(RoleRequest.class))).thenReturn(roleResponse);

            mockMvc.perform(put("/role/{id}", roleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Role berhasil diperbarui"));
        }
    }

    @Nested
    @DisplayName("updatePermission")
    class UpdatePermissionTest {

        @Test
        @DisplayName("should update role permissions successfully")
        void shouldUpdatePermissionSuccessfully() throws Exception {
            RolePermissionsRequest request = new RolePermissionsRequest();
            request.setPermissions(List.of(UUID.randomUUID()));

            when(roleService.updatePermission(eq(roleId), any())).thenReturn(roleResponse);

            mockMvc.perform(patch("/role/{id}/permission", roleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Permission role berhasil diperbarui"));
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("should delete role successfully")
        void shouldDeleteRoleSuccessfully() throws Exception {
            when(roleService.delete(roleId)).thenReturn(roleResponse);

            mockMvc.perform(delete("/role/{id}", roleId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Role berhasil dihapus"));
        }
    }
}