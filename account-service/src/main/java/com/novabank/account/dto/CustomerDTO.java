package com.novabank.account.dto;

public record CustomerDTO(
        Long customerId,
        String customerName,
        String lastName,
        String dni,
        String email,
        String phoneNumber
) {}
