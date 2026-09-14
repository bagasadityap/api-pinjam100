package com.bagas.pinjam100.dto.response;

import com.bagas.pinjam100.entity.Branch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BranchResponse {
    private UUID id;
    private String name;
    private String province;
    private String city;
    private String postalCode;

    public BranchResponse(Branch branch) {
        this.id = branch.getId();
        this.name = branch.getName();
        this.province = branch.getProvince();
        this.city = branch.getCity();
        this.postalCode = branch.getPostalCode();
    }
}

