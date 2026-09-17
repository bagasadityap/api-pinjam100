package com.bagas.pinjam100.service.dashboard;

import com.bagas.pinjam100.dto.dashboard.CreditAnalystDashboardResponse;
import com.bagas.pinjam100.dto.dashboard.DashboardResponse;
import com.bagas.pinjam100.dto.dashboard.DocumentCheckerDashboardResponse;
import com.bagas.pinjam100.dto.dashboard.MarketingDashboardResponse;
import com.bagas.pinjam100.dto.dashboard.PaymentDashboardResponse;
import com.bagas.pinjam100.dto.response.customer.CustomerResponse;
import com.bagas.pinjam100.dto.response.loanapplication.LoanApplicationResponse;
import com.bagas.pinjam100.entity.customer.VerificationStatus;
import com.bagas.pinjam100.entity.loanapplication.LoanApplicationStatus;
import com.bagas.pinjam100.entity.userrolepermission.User;
import com.bagas.pinjam100.repository.customer.CustomerLimitRepository;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationRepository;
import com.bagas.pinjam100.repository.loanapplication.summary.LoanApplicationBranchSummaryRepository;
import com.bagas.pinjam100.repository.loanapplication.summary.LoanApplicationSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Jakarta");

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanApplicationSummaryRepository loanApplicationSummaryRepository;
    private final LoanApplicationBranchSummaryRepository loanApplicationBranchSummaryRepository;
    private final CustomerRepository customerRepository;
    private final CustomerLimitRepository customerLimitRepository;

    public Object dashboard() {
        String role = getRole();

        return switch (role) {
            case "SUPER_ADMIN" -> superAdminDashboard();
            case "MARKETING" -> marketingDashboard();
            case "BRANCH_MARKETING" -> branchMarketingDashboard();
            case "PAYMENT" -> paymentDashboard();
            case "DOCUMENT_CHECKER" -> documentCheckerDashboard();
            case "CREDIT_ANALYST" -> creditAnalystDashboard();
            default -> throw new IllegalArgumentException(
                    "Role tidak memiliki akses dashboard"
            );
        };
    }

    private DashboardResponse superAdminDashboard() {
        LocalDate today = LocalDate.now(ZONE_ID);

        LocalDateTime currentMonthStart = today
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime previousMonthStart = currentMonthStart
                .minusMonths(1);

        LocalDateTime previousMonthEnd = currentMonthStart;

        long totalRequests =
                loanApplicationSummaryRepository.countAll();

        long previousMonthRequests =
                loanApplicationSummaryRepository.countCreatedBetween(
                        previousMonthStart,
                        previousMonthEnd
                );

        BigDecimal totalRequestAmount =
                loanApplicationSummaryRepository.sumLoanAmount();

        BigDecimal previousMonthRequestAmount =
                loanApplicationSummaryRepository.sumLoanAmountBetween(
                        previousMonthStart,
                        previousMonthEnd
                );

        long pendingRequests =
                loanApplicationSummaryRepository.countByStatus(
                        LoanApplicationStatus.UNDER_REVIEW
                );

        long approvedRequests =
                loanApplicationSummaryRepository.countByStatus(
                        LoanApplicationStatus.APPROVED
                );

        long rejectedRequests =
                loanApplicationSummaryRepository.countByStatus(
                        LoanApplicationStatus.REJECTED
                );

        long totalCustomers =
                customerRepository.countByDeletedDateIsNull();

        long previousMonthCustomers =
                customerRepository.countCreatedBetween(
                        previousMonthStart,
                        previousMonthEnd
                );

        long overdueLoans =
                loanApplicationSummaryRepository.countOverdueLoans(today);

        BigDecimal overdueLoanAmount =
                loanApplicationSummaryRepository.sumOverdueLoanAmount(today);

        double approvalRate =
                calculateApprovalRate(
                        approvedRequests,
                        rejectedRequests
                );

        List<LoanApplicationResponse> recentRequest = loanApplicationRepository.findTop5ByDeletedDateIsNullOrderByCreatedDateDesc()
                .stream()
                .map(loanApplication -> new LoanApplicationResponse(
                        loanApplication,
                        customerLimitRepository
                                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                                .orElse(null)
                ))
                .toList();


        return new DashboardResponse(
                (int) totalRequests,
                calculateGrowthRate(
                        totalRequests,
                        previousMonthRequests
                ),
                (int) pendingRequests,
                (int) approvedRequests,
                approvalRate,
                totalRequestAmount,
                calculateGrowthRate(
                        totalRequestAmount,
                        previousMonthRequestAmount
                ),
                (int) totalCustomers,
                calculateGrowthRate(
                        totalCustomers,
                        previousMonthCustomers
                ),
                (int) overdueLoans,
                overdueLoanAmount,
                recentRequest
        );
    }

    private MarketingDashboardResponse marketingDashboard() {
        return branchDashboard(
                LoanApplicationStatus.UNDER_REVIEW
        );
    }

    private MarketingDashboardResponse branchMarketingDashboard() {
        return branchDashboard(
                LoanApplicationStatus.PASS_REVIEW
        );
    }

    private PaymentDashboardResponse paymentDashboard() {
        UUID branchId = getAuthUser().getBranch().getId();

        LocalDateTime now = LocalDateTime.now(ZONE_ID);

        LocalDateTime currentMonthStart = now
                .withDayOfMonth(1)
                .with(LocalTime.MIN);

        LocalDateTime previousMonthStart = currentMonthStart
                .minusMonths(1);

        LocalDateTime previousMonthEnd = currentMonthStart;

        long totalRequests =
                loanApplicationBranchSummaryRepository.countAllByBranch(
                        branchId
                );

        long approvedRequests =
                loanApplicationBranchSummaryRepository.countByStatusAndBranch(
                        branchId,
                        LoanApplicationStatus.APPROVED
                );

        long pendingDisbursements =
                loanApplicationBranchSummaryRepository.countByStatusAndBranch(
                        branchId,
                        LoanApplicationStatus.APPROVED
                );

        BigDecimal totalApprovedAmount =
                loanApplicationBranchSummaryRepository.sumLoanAmountByBranch(
                        branchId
                );

        BigDecimal previousMonthDisbursementAmount =
                loanApplicationBranchSummaryRepository.sumLoanAmountCreatedBetweenByBranch(
                        branchId,
                        previousMonthStart,
                        previousMonthEnd
                );

        long totalDisbursements =
                approvedRequests;

        double totalDisbursementGrowthRate =
                calculateGrowthRate(
                        totalApprovedAmount,
                        previousMonthDisbursementAmount
                );

        return new PaymentDashboardResponse(
                (int) totalRequests,
                (int) approvedRequests,
                (int) pendingDisbursements,
                totalApprovedAmount.doubleValue(),
                (double) totalDisbursements,
                totalDisbursementGrowthRate,
                null
        );
    }

    private MarketingDashboardResponse branchDashboard(
            LoanApplicationStatus pendingStatus
    ) {
        UUID branchId = getAuthUser().getBranch().getId();

        LocalDate today = LocalDate.now(ZONE_ID);

        LocalDateTime currentMonthStart = today
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime previousMonthStart = currentMonthStart
                .minusMonths(1);

        LocalDateTime previousMonthEnd = currentMonthStart;

        long totalRequests =
                loanApplicationBranchSummaryRepository.countAllByBranch(
                        branchId
                );

        long previousMonthRequests =
                loanApplicationBranchSummaryRepository.countCreatedBetweenByBranch(
                        branchId,
                        previousMonthStart,
                        previousMonthEnd
                );

        BigDecimal totalRequestAmount =
                loanApplicationBranchSummaryRepository.sumLoanAmountByBranch(
                        branchId
                );

        BigDecimal previousMonthRequestAmount =
                loanApplicationBranchSummaryRepository.sumLoanAmountCreatedBetweenByBranch(
                        branchId,
                        previousMonthStart,
                        previousMonthEnd
                );

        long pendingRequests =
                loanApplicationBranchSummaryRepository.countByStatusAndBranch(
                        branchId,
                        pendingStatus
                );

        long approvedRequests =
                loanApplicationBranchSummaryRepository.countByStatusAndBranch(
                        branchId,
                        LoanApplicationStatus.APPROVED
                );

        long rejectedRequests =
                loanApplicationBranchSummaryRepository.countByStatusAndBranch(
                        branchId,
                        LoanApplicationStatus.REJECTED
                );

        long totalCustomers =
                customerRepository.countByDeletedDateIsNull();

        long overdueLoans =
                loanApplicationBranchSummaryRepository.countOverdueLoansByBranch(
                        branchId,
                        today
                );

        BigDecimal overdueLoanAmount =
                loanApplicationBranchSummaryRepository.sumOverdueLoanAmountByBranch(
                        branchId,
                        today
                );

        double approvalRate =
                calculateApprovalRate(
                        approvedRequests,
                        rejectedRequests
                );

        List<LoanApplicationResponse> recentRequest = loanApplicationRepository.findTop5ByBranch_IdAndDeletedDateIsNullOrderByCreatedDateDesc(branchId)
                .stream()
                .map(loanApplication -> new LoanApplicationResponse(
                        loanApplication,
                        customerLimitRepository
                                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                                .orElse(null)
                ))
                .toList();

        return new MarketingDashboardResponse(
                (int) totalRequests,
                calculateGrowthRate(
                        totalRequests,
                        previousMonthRequests
                ),
                (int) pendingRequests,
                (int) approvedRequests,
                (int) rejectedRequests,
                approvalRate,
                totalRequestAmount.doubleValue(),
                calculateGrowthRate(
                        totalRequestAmount,
                        previousMonthRequestAmount
                ),
                (int) totalCustomers,
                0D,
                (int) overdueLoans,
                overdueLoanAmount,
                recentRequest
        );
    }

    private DocumentCheckerDashboardResponse documentCheckerDashboard() {
        long totalCustomers =
                customerRepository.countByDeletedDateIsNull();

        long pendingVerification =
                customerRepository.countByVerificationStatusAndDeletedDateIsNull(
                        VerificationStatus.PENDING
                );

        long verifiedCustomers =
                customerRepository.countByVerificationStatusAndDeletedDateIsNull(
                        VerificationStatus.VERIFIED
                );

        long rejectedCustomers =
                customerRepository.countByVerificationStatusAndDeletedDateIsNull(
                        VerificationStatus.REJECTED
                );

        return new DocumentCheckerDashboardResponse(
                (int) totalCustomers,
                (int) pendingVerification,
                (int) verifiedCustomers,
                (int) rejectedCustomers,
                null
        );
    }

    private CreditAnalystDashboardResponse creditAnalystDashboard() {
        long totalCustomers =
                customerRepository.countByDeletedDateIsNull();

        long verifiedCustomers =
                customerRepository.countByVerificationStatusAndDeletedDateIsNull(
                        VerificationStatus.VERIFIED
                );

        long pendingLimitAnalysis =
                customerRepository.countVerifiedAndLimitIsNull(
                        VerificationStatus.VERIFIED
                );

        long customersWithLimit =
                verifiedCustomers - pendingLimitAnalysis;

        return new CreditAnalystDashboardResponse(
                (int) totalCustomers,
                (int) verifiedCustomers,
                (int) pendingLimitAnalysis,
                (int) customersWithLimit,
                null
        );
    }

    private double calculateGrowthRate(
            long current,
            long previous
    ) {
        if (previous == 0) {
            return current == 0 ? 0D : 100D;
        }

        return ((double) (current - previous) / previous) * 100D;
    }

    private double calculateGrowthRate(
            BigDecimal current,
            BigDecimal previous
    ) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current == null || current.compareTo(BigDecimal.ZERO) == 0
                    ? 0D
                    : 100D;
        }

        return current
                .subtract(previous)
                .divide(
                        previous,
                        6,
                        java.math.RoundingMode.HALF_UP
                )
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private double calculateApprovalRate(
            long approved,
            long rejected
    ) {
        long total = approved + rejected;

        if (total == 0) {
            return 0D;
        }

        return ((double) approved / total) * 100D;
    }

    private String getRole() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority ->
                        authority.startsWith("ROLE_")
                                ? authority.substring(5)
                                : authority
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role tidak ditemukan"
                        )
                );
    }

    private User getAuthUser() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User user)) {
            throw new IllegalArgumentException(
                    "User tidak ditemukan"
            );
        }

        return user;
    }
}