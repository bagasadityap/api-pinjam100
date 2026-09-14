package com.bagas.pinjam100.dto.request.customer;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEmploymentRequest {
    private String employmentType;
    private String companyName;
    private String position;
    private BigDecimal monthlyIncome;
    private LocalDate startDate;
    private String companyAddress;
    private String companyPhone;
}