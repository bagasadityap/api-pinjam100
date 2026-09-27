package com.bagas.pinjam100.controller.loanapplication;

import com.bagas.pinjam100.dto.request.loanapplication.ApprovalRequest;
import com.bagas.pinjam100.dto.request.loanapplication.LoanApplicationRequest;
import com.bagas.pinjam100.dto.request.loanapplication.ReviewRequest;
import com.bagas.pinjam100.dto.response.loanapplication.LoanApplicationApprovalResponse;
import com.bagas.pinjam100.dto.response.loanapplication.LoanApplicationDisbursementResponse;
import com.bagas.pinjam100.dto.response.loanapplication.LoanApplicationResponse;
import com.bagas.pinjam100.dto.response.loanapplication.LoanApplicationReviewResponse;
import com.bagas.pinjam100.service.loanapplication.LoanApplicationService;
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
@DisplayName("LoanApplicationControllerTest")
class LoanApplicationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoanApplicationService loanApplicationService;

    @InjectMocks
    private LoanApplicationController loanApplicationController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private UUID loanId;
    private UUID customerOrBranchId;
    private LoanApplicationResponse loanResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loanApplicationController).build();
        loanId = UUID.randomUUID();
        customerOrBranchId = UUID.randomUUID();
        loanResponse = new LoanApplicationResponse();
    }

    @Nested
    @DisplayName("getAll")
    class GetAllTest {

        @Test
        @DisplayName("should return list of loan applications successfully")
        void shouldReturnListOfLoanApplications() throws Exception {
            when(loanApplicationService.findAllByDeletedDateIsNull()).thenReturn(List.of(loanResponse));

            mockMvc.perform(get("/loan-application"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Data pengajuan berhasil ditemukan"))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("getAllForReview")
    class GetAllForReviewTest {

        @Test
        @DisplayName("should return list of review applications successfully")
        void shouldReturnListOfReviewApplications() throws Exception {
            when(loanApplicationService.findAllForReview()).thenReturn(List.of(loanResponse));

            mockMvc.perform(get("/loan-application/review"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Data pengajuan review berhasil ditemukan"));
        }
    }

    @Nested
    @DisplayName("getAllForApproval")
    class GetAllForApprovalTest {

        @Test
        @DisplayName("should return list of approval applications successfully")
        void shouldReturnListOfApprovalApplications() throws Exception {
            when(loanApplicationService.findAllForApproval()).thenReturn(List.of(loanResponse));

            mockMvc.perform(get("/loan-application/approval"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Data pengajuan approval berhasil ditemukan"));
        }
    }

    @Nested
    @DisplayName("getAllForDisbursement")
    class GetAllForDisbursementTest {

        @Test
        @DisplayName("should return list of disbursement applications successfully")
        void shouldReturnListOfDisbursementApplications() throws Exception {
            when(loanApplicationService.findAllForDisbursement()).thenReturn(List.of(loanResponse));

            mockMvc.perform(get("/loan-application/disbursement"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Data pengajuan pencairan berhasil ditemukan"));
        }
    }

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("should return loan application when id exists")
        void shouldReturnLoanWhenExists() throws Exception {
            when(loanApplicationService.findByIdAndDeletedDateIsNull(loanId)).thenReturn(loanResponse);

            mockMvc.perform(get("/loan-application/{id}", loanId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Data pengajuan berhasil ditemukan"));
        }
    }

    @Nested
    @DisplayName("getByIdReview")
    class GetByIdReviewTest {

        @Test
        @DisplayName("should return review detail successfully")
        void shouldReturnReviewDetailSuccessfully() throws Exception {
            LoanApplicationReviewResponse reviewResponse = new LoanApplicationReviewResponse();
            when(loanApplicationService.findByIdForReview(loanId)).thenReturn(reviewResponse);

            mockMvc.perform(get("/loan-application/{id}/review", loanId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Detail review pengajuan berhasil ditemukan"));
        }
    }

    @Nested
    @DisplayName("review")
    class ReviewTest {

        @Test
        @DisplayName("should review loan application successfully")
        void shouldReviewSuccessfully() throws Exception {
            ReviewRequest request = new ReviewRequest();
            when(loanApplicationService.review(eq(loanId), any(ReviewRequest.class))).thenReturn(loanResponse);

            mockMvc.perform(post("/loan-application/{id}/review", loanId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Pengajuan berhasil direview"));
        }
    }

    @Nested
    @DisplayName("getByIdApproval")
    class GetByIdApprovalTest {

        @Test
        @DisplayName("should return approval detail successfully")
        void shouldReturnApprovalDetailSuccessfully() throws Exception {
            LoanApplicationApprovalResponse approvalResponse = new LoanApplicationApprovalResponse();
            when(loanApplicationService.findByIdForApproval(loanId)).thenReturn(approvalResponse);

            mockMvc.perform(get("/loan-application/{id}/approval", loanId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Detail approval pengajuan berhasil ditemukan"));
        }
    }

    @Nested
    @DisplayName("approve")
    class ApproveTest {

        @Test
        @DisplayName("should approve loan application successfully")
        void shouldApproveSuccessfully() throws Exception {
            ApprovalRequest request = new ApprovalRequest();
            when(loanApplicationService.approve(eq(loanId), any(ApprovalRequest.class))).thenReturn(loanResponse);

            mockMvc.perform(post("/loan-application/{id}/approval", loanId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Pengajuan berhasil disetujui"));
        }
    }

    @Nested
    @DisplayName("getByIdDisbursement")
    class GetByIdDisbursementTest {

        @Test
        @DisplayName("should return disbursement detail successfully")
        void shouldReturnDisbursementDetailSuccessfully() throws Exception {
            LoanApplicationDisbursementResponse disbursementResponse = new LoanApplicationDisbursementResponse();
            when(loanApplicationService.findByIdForDisbursement(loanId)).thenReturn(disbursementResponse);

            mockMvc.perform(get("/loan-application/{id}/disbursement", loanId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Detail pencairan pengajuan berhasil ditemukan"));
        }
    }

    @Nested
    @DisplayName("disburse")
    class DisburseTest {

        @Test
        @DisplayName("should disburse loan successfully")
        void shouldDisburseSuccessfully() throws Exception {
            when(loanApplicationService.disburse(loanId)).thenReturn(loanResponse);

            mockMvc.perform(post("/loan-application/{id}/disbursement", loanId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Pengajuan berhasil dicairkan"));
        }
    }

    @Nested
    @DisplayName("getByBranch")
    class GetByBranchTest {

        @Test
        @DisplayName("should return list of loan applications by branch successfully")
        void shouldReturnByBranchSuccessfully() throws Exception {
            when(loanApplicationService.findByBranchAndDeletedDateIsNull(customerOrBranchId)).thenReturn(List.of(loanResponse));

            mockMvc.perform(get("/loan-application/{id}/branch", customerOrBranchId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Data pengajuan berdasarkan cabang berhasil ditemukan"));
        }
    }

    @Nested
    @DisplayName("getByCustomer")
    class GetByCustomerTest {

        @Test
        @DisplayName("should return list of loan applications by customer successfully")
        void shouldReturnByCustomerSuccessfully() throws Exception {
            when(loanApplicationService.findByCustomerAndDeletedDateIsNull(customerOrBranchId)).thenReturn(List.of(loanResponse));

            mockMvc.perform(get("/loan-application/{id}/customer", customerOrBranchId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Data pengajuan berdasarkan customer berhasil ditemukan"));
        }
    }

    @Nested
    @DisplayName("create")
    class CreateTest {

        @Test
        @DisplayName("should create loan application successfully")
        void shouldCreateSuccessfully() throws Exception {
            LoanApplicationRequest request = new LoanApplicationRequest();
            when(loanApplicationService.save(any(LoanApplicationRequest.class))).thenReturn(loanResponse);

            mockMvc.perform(post("/loan-application")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("Pengajuan berhasil dibuat"));
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("should delete loan application successfully")
        void shouldDeleteSuccessfully() throws Exception {
            when(loanApplicationService.delete(loanId)).thenReturn(loanResponse);

            mockMvc.perform(delete("/loan-application/{id}", loanId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Pengajuan berhasil dihapus"));
        }
    }
}
