package com.novabank.operation.dto;

import java.math.BigDecimal;

public record CreateTransferRequest(
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount
) {}