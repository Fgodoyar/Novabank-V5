package com.novabank.account.dto;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        String transactionType,
        BigDecimal amount,
        String description
) {}