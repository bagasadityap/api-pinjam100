package com.bagas.pinjam100.dto.webclient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlowKirimSessionResponse {
    private boolean success;
    private FlowKirimSessionData data;
}