package com.bagas.pinjam100.dto.response.customer;

import com.bagas.pinjam100.entity.customer.CustomerLimit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LimitResponse {
    private UUID id;
    private UUID customerId;
    private BigDecimal creditLimit;
    private BigDecimal availableLimit;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public LimitResponse(CustomerLimit customerLimit) {
        this.id = customerLimit.getId();
        this.customerId = customerLimit.getCustomer().getId();
        this.creditLimit = customerLimit.getCreditLimit();
        this.availableLimit = customerLimit.getAvailableLimit();
        this.createdDate = customerLimit.getCreatedDate();
        this.updatedDate = customerLimit.getUpdatedDate();
    }
}