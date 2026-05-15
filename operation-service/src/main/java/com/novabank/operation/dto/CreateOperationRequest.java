package com.novabank.operation.dto;

import java.math.BigDecimal;

public record CreateOperationRequest(
        String accountNumber,
        BigDecimal amount
) {}