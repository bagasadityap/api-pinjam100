package com.bagas.pinjam100.controller.userrolepermission;

import com.bagas.pinjam100.dto.request.userrolepermission.PermissionRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.PermissionResponse;
import com.bagas.pinjam100.service.userrolepermission.PermissionService;
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
@DisplayName("PermissionControllerTest")
class PermissionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private PermissionController permissionController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private UUID permissionId;
    private PermissionResponse permissionResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(permissionController).build();
        permissionId = UUID.randomUUID();
        permissionResponse = new PermissionResponse();
    }

    @Nested
    @DisplayName("getAll")
    class GetAllTest {

        @Test
        @DisplayName("should return list of permissions successfully")
        void shouldReturnListOfPermissions() throws Exception {
            when(permissionService.findAllByDeletedDateIsNull()).thenReturn(List.of(permissionResponse));

            mockMvc.perform(get("/permission"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Data permission berhasil diambil"))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("should return permission when id exists")
        void shouldReturnPermissionWhenExists() throws Exception {
            when(permissionService.findByIdAndDeletedDateIsNull(permissionId)).thenReturn(permissionResponse);

            mockMvc.perform(get("/permission/{id}", permissionId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Data permission berhasil diambil"));
        }
    }

    @Nested
    @DisplayName("create")
    class CreateTest {

        @Test
        @DisplayName("should create permission successfully")
        void shouldCreatePermissionSuccessfully() throws Exception {
            PermissionRequest request = new PermissionRequest();
            when(permissionService.save(any(PermissionRequest.class))).thenReturn(permissionResponse);

            mockMvc.perform(post("/permission")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("Permission berhasil dibuat"));
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("should update permission successfully")
        void shouldUpdatePermissionSuccessfully() throws Exception {
            PermissionRequest request = new PermissionRequest();
            when(permissionService.update(eq(permissionId), any(PermissionRequest.class))).thenReturn(permissionResponse);

            mockMvc.perform(put("/permission/{id}", permissionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Permission berhasil diperbarui"));
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("should delete permission successfully")
        void shouldDeletePermissionSuccessfully() throws Exception {
            when(permissionService.delete(permissionId)).thenReturn(permissionResponse);

            mockMvc.perform(delete("/permission/{id}", permissionId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Permission berhasil dihapus"));
        }
    }
}
