package com.bagas.pinjam100.service.dashboard;

import com.bagas.pinjam100.dto.dashboard.CreditAnalystDashboardResponse;
import com.bagas.pinjam100.dto.dashboard.DashboardResponse;
import com.bagas.pinjam100.dto.dashboard.DocumentCheckerDashboardResponse;
import com.bagas.pinjam100.dto.dashboard.MarketingDashboardResponse;
import com.bagas.pinjam100.dto.dashboard.PaymentDashboardResponse;
import com.bagas.pinjam100.entity.Branch;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.customer.CustomerLimit;
import com.bagas.pinjam100.entity.customer.VerificationStatus;
import com.bagas.pinjam100.entity.loanapplication.LoanApplication;
import com.bagas.pinjam100.entity.loanapplication.LoanApplicationStatus;
import com.bagas.pinjam100.entity.userrolepermission.User;
import com.bagas.pinjam100.repository.customer.CustomerLimitRepository;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationRepository;
import com.bagas.pinjam100.repository.loanapplication.summary.LoanApplicationBranchSummaryRepository;
import com.bagas.pinjam100.repository.loanapplication.summary.LoanApplicationSummaryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardServiceTest")
class DashboardServiceTest {

    private static final UUID BRANCH_ID = UUID.randomUUID();
    private static final UUID CUSTOMER_ID = UUID.randomUUID();

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanApplicationSummaryRepository loanApplicationSummaryRepository;

    @Mock
    private LoanApplicationBranchSummaryRepository loanApplicationBranchSummaryRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerLimitRepository customerLimitRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("dashboard")
    class DashboardTest {

        @Test
        @DisplayName("should route to superAdminDashboard when role is SUPER_ADMIN")
        void shouldRouteToSuperAdminDashboard() {
            setupSecurityContext("ROLE_SUPER_ADMIN", createUser());

            when(loanApplicationSummaryRepository.sumLoanAmount()).thenReturn(BigDecimal.ZERO);
            when(loanApplicationSummaryRepository.sumLoanAmountBetween(any(), any())).thenReturn(BigDecimal.ZERO);
            when(loanApplicationSummaryRepository.sumOverdueLoanAmount(any())).thenReturn(BigDecimal.ZERO);

            Object result = dashboardService.dashboard();

            assertNotNull(result);
            assertInstanceOf(DashboardResponse.class, result);
        }

        @Test
        @DisplayName("should route to marketingDashboard when role is MARKETING")
        void shouldRouteToMarketingDashboard() {
            setupSecurityContext("ROLE_MARKETING", createUser());

            when(loanApplicationBranchSummaryRepository.sumLoanAmountByBranch(BRANCH_ID)).thenReturn(BigDecimal.ZERO);
            when(loanApplicationBranchSummaryRepository.sumLoanAmountCreatedBetweenByBranch(eq(BRANCH_ID), any(), any())).thenReturn(BigDecimal.ZERO);
            when(loanApplicationBranchSummaryRepository.sumOverdueLoanAmountByBranch(eq(BRANCH_ID), any())).thenReturn(BigDecimal.ZERO);

            Object result = dashboardService.dashboard();

            assertNotNull(result);
            assertInstanceOf(MarketingDashboardResponse.class, result);
        }

        @Test
        @DisplayName("should route to branchMarketingDashboard when role is BRANCH_MARKETING")
        void shouldRouteToBranchMarketingDashboard() {
            setupSecurityContext("ROLE_BRANCH_MARKETING", createUser());

            when(loanApplicationBranchSummaryRepository.sumLoanAmountByBranch(BRANCH_ID)).thenReturn(BigDecimal.ZERO);
            when(loanApplicationBranchSummaryRepository.sumLoanAmountCreatedBetweenByBranch(eq(BRANCH_ID), any(), any())).thenReturn(BigDecimal.ZERO);
            when(loanApplicationBranchSummaryRepository.sumOverdueLoanAmountByBranch(eq(BRANCH_ID), any())).thenReturn(BigDecimal.ZERO);

            Object result = dashboardService.dashboard();

            assertNotNull(result);
            assertInstanceOf(MarketingDashboardResponse.class, result);
        }

        @Test
        @DisplayName("should route to paymentDashboard when role is PAYMENT")
        void shouldRouteToPaymentDashboard() {
            setupSecurityContext("ROLE_PAYMENT", createUser());

            when(loanApplicationSummaryRepository.sumLoanAmount()).thenReturn(BigDecimal.ZERO);
            when(loanApplicationSummaryRepository.sumLoanAmountBetween(any(), any())).thenReturn(BigDecimal.ZERO);
            when(loanApplicationSummaryRepository.sumLoanAmountByStatus(any())).thenReturn(BigDecimal.ZERO);
            when(loanApplicationRepository.findTop5ByStatusAndDeletedDateIsNullOrderByCreatedDateDesc(any()))
                    .thenReturn(List.of());

            Object result = dashboardService.dashboard();

            assertNotNull(result);
            assertInstanceOf(PaymentDashboardResponse.class, result);
        }

        @Test
        @DisplayName("should route to documentCheckerDashboard when role is DOCUMENT_CHECKER")
        void shouldRouteToDocumentCheckerDashboard() {
            setupSecurityContext("ROLE_DOCUMENT_CHECKER", createUser());

            Object result = dashboardService.dashboard();

            assertNotNull(result);
            assertInstanceOf(DocumentCheckerDashboardResponse.class, result);
        }

        @Test
        @DisplayName("should route to creditAnalystDashboard when role is CREDIT_ANALYST")
        void shouldRouteToCreditAnalystDashboard() {
            setupSecurityContext("ROLE_CREDIT_ANALYST", createUser());

            Object result = dashboardService.dashboard();

            assertNotNull(result);
            assertInstanceOf(CreditAnalystDashboardResponse.class, result);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when role is invalid")
        void shouldThrowExceptionWhenRoleIsInvalid() {
            setupSecurityContext("ROLE_INVALID", createUser());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> dashboardService.dashboard()
            );

            assertEquals("Role tidak memiliki akses dashboard", exception.getMessage());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when authority is missing")
        void shouldThrowExceptionWhenAuthorityIsMissing() {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            doReturn(List.of()).when(authentication).getAuthorities();
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> dashboardService.dashboard()
            );

            assertEquals("Role tidak ditemukan", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("superAdminDashboard")
    class SuperAdminDashboardTest {

        @Test
        @DisplayName("should return super admin dashboard response successfully")
        void shouldReturnSuperAdminDashboardResponse() {
            LoanApplication loanApplication = createLoanApplication();
            CustomerLimit customerLimit = new CustomerLimit();

            when(loanApplicationSummaryRepository.countAll()).thenReturn(10L);
            when(loanApplicationSummaryRepository.countCreatedBetween(any(), any())).thenReturn(5L);
            when(loanApplicationSummaryRepository.sumLoanAmount()).thenReturn(new BigDecimal("10000000"));
            when(loanApplicationSummaryRepository.sumLoanAmountBetween(any(), any())).thenReturn(new BigDecimal("5000000"));
            when(loanApplicationSummaryRepository.countByStatus(LoanApplicationStatus.UNDER_REVIEW)).thenReturn(2L);
            when(loanApplicationSummaryRepository.countByStatus(LoanApplicationStatus.APPROVED)).thenReturn(6L);
            when(loanApplicationSummaryRepository.countByStatus(LoanApplicationStatus.REJECTED)).thenReturn(2L);
            when(customerRepository.countByDeletedDateIsNull()).thenReturn(20L);
            when(customerRepository.countCreatedBetween(any(), any())).thenReturn(10L);
            when(loanApplicationSummaryRepository.countOverdueLoans(any(LocalDate.class))).thenReturn(1L);
            when(loanApplicationSummaryRepository.sumOverdueLoanAmount(any(LocalDate.class))).thenReturn(new BigDecimal("1000000"));

            when(loanApplicationRepository.findTop5ByDeletedDateIsNullOrderByCreatedDateDesc())
                    .thenReturn(List.of(loanApplication));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(customerLimit));

            DashboardResponse response = dashboardService.superAdminDashboard();

            assertNotNull(response);
            assertEquals(10, response.getTotalRequests());
            assertEquals(100.0, response.getTotalRequestGrowthRate());
            assertEquals(2, response.getPendingRequests());
            assertEquals(6, response.getApprovedRequests());
            assertEquals(75.0, response.getApprovalRate());
            assertEquals(20, response.getTotalCustomers());
            assertEquals(1, response.getTotalLoanOverdue());
            assertEquals(1, response.getRecentRequests().size());
        }

        @Test
        @DisplayName("should handle zero previous values correctly without division by zero")
        void shouldHandleZeroPreviousValuesCorrectly() {
            when(loanApplicationSummaryRepository.countAll()).thenReturn(0L);
            when(loanApplicationSummaryRepository.countCreatedBetween(any(), any())).thenReturn(0L);
            when(loanApplicationSummaryRepository.sumLoanAmount()).thenReturn(BigDecimal.ZERO);
            when(loanApplicationSummaryRepository.sumLoanAmountBetween(any(), any())).thenReturn(BigDecimal.ZERO);
            when(loanApplicationSummaryRepository.countByStatus(any())).thenReturn(0L);
            when(customerRepository.countByDeletedDateIsNull()).thenReturn(0L);
            when(customerRepository.countCreatedBetween(any(), any())).thenReturn(0L);
            when(loanApplicationSummaryRepository.countOverdueLoans(any())).thenReturn(0L);
            when(loanApplicationSummaryRepository.sumOverdueLoanAmount(any())).thenReturn(BigDecimal.ZERO);
            when(loanApplicationRepository.findTop5ByDeletedDateIsNullOrderByCreatedDateDesc())
                    .thenReturn(List.of());

            DashboardResponse response = dashboardService.superAdminDashboard();

            assertNotNull(response);
            assertEquals(0.0, response.getTotalRequestGrowthRate());
            assertEquals(0.0, response.getTotalDisbursementGrowthRate());
            assertEquals(0.0, response.getApprovalRate());
        }
    }

    @Nested
    @DisplayName("marketingDashboard & branchMarketingDashboard")
    class BranchMarketingDashboardTest {

        @Test
        @DisplayName("should return marketing dashboard response for branch")
        void shouldReturnMarketingDashboardResponse() {
            setupSecurityContext("ROLE_MARKETING", createUser());
            LoanApplication loanApplication = createLoanApplication();

            when(loanApplicationBranchSummaryRepository.countAllByBranch(BRANCH_ID)).thenReturn(10L);
            when(loanApplicationBranchSummaryRepository.countCreatedBetweenByBranch(eq(BRANCH_ID), any(), any())).thenReturn(5L);
            when(loanApplicationBranchSummaryRepository.sumLoanAmountByBranch(BRANCH_ID)).thenReturn(new BigDecimal("10000000"));
            when(loanApplicationBranchSummaryRepository.sumLoanAmountCreatedBetweenByBranch(eq(BRANCH_ID), any(), any()))
                    .thenReturn(new BigDecimal("5000000"));
            when(loanApplicationBranchSummaryRepository.countByStatusAndBranch(BRANCH_ID, LoanApplicationStatus.UNDER_REVIEW))
                    .thenReturn(3L);
            when(loanApplicationBranchSummaryRepository.countByStatusAndBranch(BRANCH_ID, LoanApplicationStatus.APPROVED))
                    .thenReturn(5L);
            when(loanApplicationBranchSummaryRepository.countByStatusAndBranch(BRANCH_ID, LoanApplicationStatus.REJECTED))
                    .thenReturn(2L);
            when(customerRepository.countByDeletedDateIsNull()).thenReturn(15L);
            when(loanApplicationBranchSummaryRepository.countOverdueLoansByBranch(eq(BRANCH_ID), any())).thenReturn(1L);
            when(loanApplicationBranchSummaryRepository.sumOverdueLoanAmountByBranch(eq(BRANCH_ID), any()))
                    .thenReturn(new BigDecimal("2000000"));

            when(loanApplicationRepository.findTop5ByBranch_IdAndDeletedDateIsNullOrderByCreatedDateDesc(BRANCH_ID))
                    .thenReturn(List.of(loanApplication));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            MarketingDashboardResponse response = dashboardService.marketingDashboard();

            assertNotNull(response);
            assertEquals(10, response.getTotalRequests());
            assertEquals(3, response.getPendingRequests());
            assertEquals(5, response.getApprovedRequests());
            assertEquals(2, response.getRejectedRequests());
            assertEquals(71.42857142857143, response.getApprovalRate());
            assertEquals(10000000.0, response.getTotalDisbursements());
            assertEquals(1, response.getRecentRequests().size());
        }

        @Test
        @DisplayName("should return branch marketing dashboard response")
        void shouldReturnBranchMarketingDashboardResponse() {
            setupSecurityContext("ROLE_BRANCH_MARKETING", createUser());

            when(loanApplicationBranchSummaryRepository.countAllByBranch(BRANCH_ID)).thenReturn(5L);
            when(loanApplicationBranchSummaryRepository.sumLoanAmountByBranch(BRANCH_ID)).thenReturn(new BigDecimal("5000000"));
            when(loanApplicationBranchSummaryRepository.sumLoanAmountCreatedBetweenByBranch(eq(BRANCH_ID), any(), any())).thenReturn(BigDecimal.ZERO);
            when(loanApplicationBranchSummaryRepository.sumOverdueLoanAmountByBranch(eq(BRANCH_ID), any())).thenReturn(BigDecimal.ZERO);

            when(loanApplicationRepository.findTop5ByBranch_IdAndDeletedDateIsNullOrderByCreatedDateDesc(BRANCH_ID))
                    .thenReturn(List.of());

            MarketingDashboardResponse response = dashboardService.branchMarketingDashboard();

            assertNotNull(response);
            verify(loanApplicationBranchSummaryRepository).countByStatusAndBranch(BRANCH_ID, LoanApplicationStatus.PASS_REVIEW);
        }
    }

    @Nested
    @DisplayName("paymentDashboard")
    class PaymentDashboardTest {

        @Test
        @DisplayName("should return payment dashboard response with positive growth rate")
        void shouldReturnPaymentDashboardResponseWithGrowth() {
            setupSecurityContext("ROLE_PAYMENT", createUser());

            LoanApplication loanApplication = createLoanApplication();
            CustomerLimit customerLimit = new CustomerLimit();

            when(loanApplicationSummaryRepository.countAll()).thenReturn(8L);
            when(loanApplicationSummaryRepository.countByStatus(LoanApplicationStatus.DISBURSED))
                    .thenReturn(6L);
            when(loanApplicationSummaryRepository.countByStatus(LoanApplicationStatus.APPROVED))
                    .thenReturn(6L);
            when(loanApplicationSummaryRepository.sumLoanAmount())
                    .thenReturn(new BigDecimal("12000000"));
            // Total current period = 6M, previous period = 3M -> growth rate = 100%
            when(loanApplicationSummaryRepository.sumLoanAmountBetween(any(), any()))
                    .thenReturn(new BigDecimal("6000000"));
            when(loanApplicationSummaryRepository.sumLoanAmountByStatus(LoanApplicationStatus.DISBURSED))
                    .thenReturn(new BigDecimal("12000000"));

            when(loanApplicationRepository.findTop5ByStatusAndDeletedDateIsNullOrderByCreatedDateDesc(LoanApplicationStatus.APPROVED))
                    .thenReturn(List.of(loanApplication));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(CUSTOMER_ID))
                    .thenReturn(Optional.of(customerLimit));

            PaymentDashboardResponse response = dashboardService.paymentDashboard();

            assertNotNull(response);
            assertEquals(8, response.getTotalRequests());
            assertEquals(6, response.getApprovedRequests());
            assertEquals(6, response.getPendingDisbursements());
            assertEquals(12000000.0, response.getTotalDisbursements());
            assertEquals(100.0, response.getTotalDisbursementGrowthRate());
        }

        @Test
        @DisplayName("should handle zero and negative growth rate scenarios in paymentDashboard")
        void shouldHandleZeroGrowthRateInPaymentDashboard() {
            setupSecurityContext("ROLE_PAYMENT", createUser());

            when(loanApplicationSummaryRepository.countAll()).thenReturn(0L);
            when(loanApplicationSummaryRepository.countByStatus(any()))
                    .thenReturn(0L);
            when(loanApplicationSummaryRepository.sumLoanAmount())
                    .thenReturn(BigDecimal.ZERO);
            // Current period = 0, previous period = 5M -> growth rate should trigger negative / zero branches
            when(loanApplicationSummaryRepository.sumLoanAmountBetween(any(), any()))
                    .thenReturn(BigDecimal.ZERO);
            when(loanApplicationSummaryRepository.sumLoanAmountByStatus(any()))
                    .thenReturn(BigDecimal.ZERO);

            when(loanApplicationRepository.findTop5ByStatusAndDeletedDateIsNullOrderByCreatedDateDesc(any()))
                    .thenReturn(List.of());

            PaymentDashboardResponse response = dashboardService.paymentDashboard();

            assertNotNull(response);
            assertEquals(0.0, response.getTotalDisbursementGrowthRate());
        }
    }

    @Nested
    @DisplayName("documentCheckerDashboard")
    class DocumentCheckerDashboardTest {

        @Test
        @DisplayName("should return document checker dashboard response")
        void shouldReturnDocumentCheckerDashboardResponse() {
            when(customerRepository.countByDeletedDateIsNull()).thenReturn(50L);
            when(customerRepository.countByVerificationStatusAndDeletedDateIsNull(VerificationStatus.PENDING))
                    .thenReturn(10L);
            when(customerRepository.countByVerificationStatusAndDeletedDateIsNull(VerificationStatus.VERIFIED))
                    .thenReturn(35L);
            when(customerRepository.countByVerificationStatusAndDeletedDateIsNull(VerificationStatus.REJECTED))
                    .thenReturn(5L);

            DocumentCheckerDashboardResponse response = dashboardService.documentCheckerDashboard();

            assertNotNull(response);
            assertEquals(50, response.getTotalCustomers());
            assertEquals(10, response.getPendingVerification());
            assertEquals(35, response.getVerifiedCustomers());
            assertEquals(5, response.getRejectedCustomers());
        }
    }

    @Nested
    @DisplayName("creditAnalystDashboard")
    class CreditAnalystDashboardTest {

        @Test
        @DisplayName("should return credit analyst dashboard response")
        void shouldReturnCreditAnalystDashboardResponse() {
            when(customerRepository.countByDeletedDateIsNull()).thenReturn(50L);
            when(customerRepository.countByVerificationStatusAndDeletedDateIsNull(VerificationStatus.VERIFIED))
                    .thenReturn(35L);
            when(customerRepository.countVerifiedAndLimitIsNull(VerificationStatus.VERIFIED))
                    .thenReturn(10L);

            CreditAnalystDashboardResponse response = dashboardService.creditAnalystDashboard();

            assertNotNull(response);
            assertEquals(50, response.getTotalCustomers());
            assertEquals(35, response.getVerifiedCustomers());
            assertEquals(10, response.getPendingLimitAnalysis());
            assertEquals(25, response.getCustomersWithLimit());
        }
    }

    @Nested
    @DisplayName("clearDashboardCache")
    class ClearDashboardCacheTest {

        @Test
        @DisplayName("should execute clearDashboardCache without exception")
        void shouldExecuteClearDashboardCache() {
            assertDoesNotThrow(() -> dashboardService.clearDashboardCache());
        }
    }

    @Nested
    @DisplayName("getAuthUser")
    class GetAuthUserTest {

        @Test
        @DisplayName("should return authenticated user when principal is User instance")
        void shouldReturnAuthenticatedUser() {
            User user = createUser();
            setupSecurityContext("ROLE_SUPER_ADMIN", user);

            User result = dashboardService.getAuthUser();

            assertNotNull(result);
            assertEquals(user, result);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when principal is not User instance")
        void shouldThrowExceptionWhenPrincipalIsNotUser() {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            when(authentication.getPrincipal()).thenReturn("anonymousUser");
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> dashboardService.getAuthUser()
            );

            assertEquals("User tidak ditemukan", exception.getMessage());
        }
    }

    private void setupSecurityContext(String role, User user) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().doReturn(List.of(new SimpleGrantedAuthority(role))).when(authentication).getAuthorities();
        lenient().when(authentication.getPrincipal()).thenReturn(user);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    private User createUser() {
        Branch branch = new Branch();
        branch.setId(BRANCH_ID);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Bagas Aditya");
        user.setBranch(branch);
        return user;
    }

    private LoanApplication createLoanApplication() {
        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setId(UUID.randomUUID());
        loanApplication.setCustomer(customer);
        loanApplication.setCreatedDate(LocalDateTime.now());
        loanApplication.setStatus(LoanApplicationStatus.UNDER_REVIEW);
        return loanApplication;
    }
}