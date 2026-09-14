//package com.bagas.pinjam100.dto.response.loanapplication;
//
//import com.bagas.pinjam100.dto.response.BranchResponse;
//import com.bagas.pinjam100.dto.response.branch.BranchResponse;
//import com.bagas.pinjam100.dto.response.customer.CustomerResponse;
//import com.bagas.pinjam100.dto.response.loanapplication.approval.LoanApplicationApprovalResponse;
//import com.bagas.pinjam100.dto.response.loanapplication.disbursement.LoanDisbursementResponse;
//import com.bagas.pinjam100.dto.response.loanapplication.review.LoanApplicationReviewResponse;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.UUID;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class LoanApplicationDetailResponse {
//
//    private UUID id;
//    private String applicationId;
//    private BigDecimal loanAmount;
//    private Integer tenorMonths;
//    private BigDecimal interestRate;
//    private String purpose;
//    private String status;
//
//    private CustomerResponse customer;
//    private BranchResponse branch;
//
//    private List<LoanApplicationReviewResponse> reviews;
//    private List<LoanApplicationApprovalResponse> approvals;
//
//    private LoanDisbursementResponse disbursement;
//}