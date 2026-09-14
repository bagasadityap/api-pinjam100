package com.bagas.pinjam100.dto.response.customer;

import com.bagas.pinjam100.entity.customer.Document;
import com.bagas.pinjam100.entity.customer.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private UUID id;
    private String type;
    private String fileUrl;
    private VerificationStatus verificationStatus;
    private String verifiedBy;
    private UUID customerId;

    public DocumentResponse(Document response) {
        this.id = response.getId();
        this.type = response.getType();
        this.fileUrl = response.getFileUrl();
        this.verificationStatus = response.getVerificationStatus();
        this.verifiedBy = response.getVerifiedBy();
        this.customerId = response.getCustomer().getId();
    }
}