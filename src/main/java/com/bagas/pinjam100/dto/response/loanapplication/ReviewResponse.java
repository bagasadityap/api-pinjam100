package com.bagas.pinjam100.dto.response.loanapplication;

import com.bagas.pinjam100.entity.loanapplication.LoanApplicationReview;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private UUID id;
    private UUID loanApplicationId;
    private String result;
    private String notes;
    private UUID reviewedBy;
    private LocalDateTime reviewedDate;

    public ReviewResponse(LoanApplicationReview review) {
        this.id = review.getId();
        this.loanApplicationId = review.getLoanApplication().getId();
        this.result = review.getResult().toString();
        this.notes = review.getNotes();
        this.reviewedBy = review.getReviewer().getId();
        this.reviewedDate = review.getReviewedDate();
    }
}
