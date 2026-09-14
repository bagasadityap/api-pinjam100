package com.bagas.pinjam100.dto.request.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RekeningRequest {
    private String namaBank;
    private String noRekening;
    private String accountHolder;
}
