package com.bagas.pinjam100.dto.request.loanapplication;

import com.bagas.pinjam100.entity.loanapplication.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequest {
    private ApprovalStatus approvalStatus;
    private String notes;
}
