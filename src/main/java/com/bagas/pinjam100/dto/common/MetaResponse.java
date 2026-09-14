package com.bagas.pinjam100.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MetaResponse {

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}