package com.novabank.auth.dto;

public record RegisterRequest(
        String username,
        String password
) {}
