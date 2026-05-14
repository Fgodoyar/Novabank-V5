package com.novabank.operation.exception;

public class AccountNotFoundException extends RuntimeException {
    private final Integer accountId;

    public AccountNotFoundException(Integer id) {
        super("Cuenta no encontrado con ID: " + id);
        this.accountId = id;
    }

    public AccountNotFoundException(String accountNumber) {
        super("Cuenta no encontrado con número: " + accountNumber);
        this.accountId = null;
    }

    public Integer getAccountId() {
        return accountId;
    }
}
