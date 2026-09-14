package com.bagas.pinjam100.dto.request.loanapplication;

import com.bagas.pinjam100.entity.loanapplication.ReviewResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private ReviewResult reviewResult;
    private String notes;
}
