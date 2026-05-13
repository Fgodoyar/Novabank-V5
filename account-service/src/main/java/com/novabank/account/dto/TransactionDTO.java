package com.novabank.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(
        Long transactionId,
        String transactionType,
        BigDecimal amount,
        String description,
        LocalDateTime creationDate,
        Long accountId
) {}