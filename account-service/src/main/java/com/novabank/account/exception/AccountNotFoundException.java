package com.novabank.account.exception;

public class AccountNotFoundException extends RuntimeException {
    private final Long accountId;

    public AccountNotFoundException(Long id) {
        super("Cuenta no encontrado con ID: " + id);
        this.accountId = id;
    }

    public AccountNotFoundException(String accountNumber) {
        super("Cuenta no encontrado con número: " + accountNumber);
        this.accountId = null;
    }

    public Long getAccountId() {
        return accountId;
    }
}
