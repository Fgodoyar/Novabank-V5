package com.novabank.operation.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String account_number, BigDecimal balance, BigDecimal amount) {
        super("Saldo insuficiente en la cuenta " + account_number +
                ". Saldo disponible: " + balance + ", cantidad solicitada: " + amount);
    }
}
