package com.bagas.pinjam100.dto.response.customer;

import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.customer.CustomerLimit;
import com.bagas.pinjam100.entity.customer.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private UUID id;
    private String customerNumber;
    private String fullName;
    private String email;
    private String phoneNumber;
    private VerificationStatus verificationStatus;
    private LocalDateTime createdDate;
    private LimitResponse limit;

    public CustomerResponse(Customer customer, CustomerLimit customerLimit) {
        this.id = customer.getId();
        this.customerNumber = customer.getCustomerNumber();
        this.fullName = customer.getFullName();
        this.email = customer.getEmail();
        this.phoneNumber = customer.getPhoneNumber();
        this.verificationStatus = customer.getVerificationStatus();
        this.createdDate = customer.getCreatedDate();

        this.limit = customerLimit != null
                ? new LimitResponse(customerLimit)
                : null;
    }

    public CustomerResponse(Customer customer) {
        this.id = customer.getId();
        this.customerNumber = customer.getCustomerNumber();
        this.fullName = customer.getFullName();
        this.email = customer.getEmail();
        this.phoneNumber = customer.getPhoneNumber();
        this.verificationStatus = customer.getVerificationStatus();
        this.createdDate = customer.getCreatedDate();
    }
}