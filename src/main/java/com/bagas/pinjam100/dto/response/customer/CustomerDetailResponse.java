package com.bagas.pinjam100.dto.response.customer;

import com.bagas.pinjam100.entity.customer.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailResponse {
    private UUID id;
    private String customerNumber;
    private String nationalId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private VerificationStatus verificationStatus;
    private boolean profileCompleted;
    private LocalDateTime createdDate;

    private DetailResponse detail;
    private EmploymentResponse employment;
    private LimitResponse limit;
    private List<DocumentResponse> documents;
    private List<RekeningResponse> rekening;

    public CustomerDetailResponse(
            Customer customer,
            CustomerDetail customerDetail,
            CustomerEmployment customerEmployment,
            CustomerLimit customerLimit,
            List<Document> documents,
            List<Rekening> rekening
    ) {
        this.id = customer.getId();
        this.customerNumber = customer.getCustomerNumber();
        this.nationalId = customer.getNationalId();
        this.fullName = customer.getFullName();
        this.email = customer.getEmail();
        this.phoneNumber = customer.getPhoneNumber();
        this.verificationStatus = customer.getVerificationStatus();
        this.profileCompleted = customer.isProfileCompleted();
        this.createdDate = customer.getCreatedDate();

        this.detail = customerDetail != null
                ? new DetailResponse(customerDetail)
                : null;

        this.employment = customerEmployment != null
                ? new EmploymentResponse(customerEmployment)
                : null;

        this.limit = customerLimit != null
                ? new LimitResponse(customerLimit)
                : null;

        this.documents = documents != null
                ? documents.stream()
                .map(DocumentResponse::new)
                .toList()
                : List.of();

        this.rekening = rekening != null
                ? rekening.stream()
                .map(RekeningResponse::new)
                .toList()
                : List.of();
    }
}