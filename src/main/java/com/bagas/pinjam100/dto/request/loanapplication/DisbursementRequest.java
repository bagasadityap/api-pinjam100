package com.bagas.pinjam100.dto.request.loanapplication;

import com.bagas.pinjam100.entity.customer.Rekening;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DisbursementRequest {
    private BigDecimal disbursementAmount;
    private BigDecimal adminFee;
    private BigDecimal otherFee;
    private BigDecimal netAmount;
    private Rekening rekening;
}
