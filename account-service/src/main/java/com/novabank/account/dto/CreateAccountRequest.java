package com.novabank.account.dto;

import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(
        @NotNull(message = "customerId es obligatorio")
        Long customerId
) {}