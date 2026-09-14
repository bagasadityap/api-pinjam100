package com.bagas.pinjam100.dto.response.customer;

import com.bagas.pinjam100.entity.customer.CustomerEmployment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentResponse {
    private UUID id;
    private String employmentType;
    private String companyName;
    private String position;
    private BigDecimal monthlyIncome;
    private LocalDate startDate;
    private String companyAddress;
    private String companyPhone;

    public EmploymentResponse(CustomerEmployment response) {
        this.id = response.getId();
        this.employmentType = response.getEmploymentType();
        this.companyName = response.getCompanyName();
        this.position = response.getPosition();
        this.monthlyIncome = response.getMonthlyIncome();
        this.startDate = response.getStartDate();
        this.companyAddress = response.getCompanyAddress();
        this.companyPhone = response.getCompanyPhone();
    }
}