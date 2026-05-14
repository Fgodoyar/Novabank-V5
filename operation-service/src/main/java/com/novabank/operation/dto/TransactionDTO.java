package com.novabank.operation.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionDTO(
        Long transactionId,
        String transactionType,
        BigDecimal amount,
        String description,
        LocalDateTime creationDate,
        Long accountId
) {}