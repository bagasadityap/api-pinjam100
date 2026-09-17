package com.bagas.pinjam100.config.loan;

import java.math.BigDecimal;

public final class LoanConfig {

    private LoanConfig() {
    }

    public static final BigDecimal DAILY_INTEREST_RATE =
            new BigDecimal("0.10");

    public static final BigDecimal MIN_LOAN_AMOUNT =
            new BigDecimal("500000");

    public static final BigDecimal MAX_LOAN_AMOUNT =
            new BigDecimal("35000000");

    public static final int DAYS_PER_MONTH = 30;
}