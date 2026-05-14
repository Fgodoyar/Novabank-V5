package com.novabank.operation.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AccountDTO(
        Long accountId,
        String accountNumber,
        String accountHolder,
        BigDecimal balance,
        LocalDateTime creationDate
) {}