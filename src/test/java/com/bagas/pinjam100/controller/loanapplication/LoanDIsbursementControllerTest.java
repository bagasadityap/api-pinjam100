package com.bagas.pinjam100.controller.loanapplication;

import com.bagas.pinjam100.dto.response.loanapplication.DisbursementResponse;
import com.bagas.pinjam100.service.loanapplication.LoanDisbursementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanDisbursementControllerTest")
class LoanDisbursementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoanDisbursementService loanDisbursementService;

    @InjectMocks
    private LoanDisbursementController loanDisbursementController;

    private UUID disbursementId;
    private DisbursementResponse disbursementResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loanDisbursementController).build();
        disbursementId = UUID.randomUUID();
        disbursementResponse = new DisbursementResponse();
    }

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("should return disbursement response when id exists")
        void shouldReturnDisbursementWhenExists() throws Exception {
            when(loanDisbursementService.getById(disbursementId)).thenReturn(disbursementResponse);

            mockMvc.perform(get("/disbursement/{id}", disbursementId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Data pencairan berhasil ditemukan"));
        }
    }
}
