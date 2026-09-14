package com.bagas.pinjam100.dto.response.customer;

import com.bagas.pinjam100.entity.customer.Rekening;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RekeningResponse {
    private UUID id;
    private String namaBank;
    private String noRekening;
    private String accountHolder;

    public RekeningResponse(Rekening rekening) {
        this.id = rekening.getId();
        this.namaBank = rekening.getNamaBank();
        this.noRekening = rekening.getNoRekening();
        this.accountHolder = rekening.getAccountHolder();
    }
}
