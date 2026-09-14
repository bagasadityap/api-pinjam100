package com.bagas.pinjam100.entity.customer;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_employment", schema = "loan")
public class CustomerEmployment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "employment_type", nullable = false)
    private String employmentType;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "position")
    private String position;
    @Column(name = "monthly_income", precision = 19, scale = 2)
    private BigDecimal monthlyIncome;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "company_address")
    private String companyAddress;
    @Column(name = "company_phone")
    private String companyPhone;
    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}