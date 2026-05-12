package com.novabank.customer.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String value) {
        super("Ya existe un cliente con el email: " + value);
    }
}
