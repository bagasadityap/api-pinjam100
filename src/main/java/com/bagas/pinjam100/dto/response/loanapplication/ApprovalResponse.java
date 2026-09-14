package com.bagas.pinjam100.dto.response.loanapplication;

import com.bagas.pinjam100.entity.loanapplication.LoanApplicationApproval;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalResponse {
    private UUID id;
    private UUID loanApplicationId;
    private String status;
    private String notes;
    private UUID approvedBy;
    private LocalDateTime approvedDate;

    public ApprovalResponse(LoanApplicationApproval approval) {
        this.id = approval.getId();
        this.loanApplicationId = approval.getLoanApplication().getId();
        this.status = approval.getStatus().name();
        this.notes = approval.getNotes();
        this.approvedBy = approval.getApprover().getId();
        this.approvedDate = approval.getApprovedDate();
    }
}
