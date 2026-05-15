package com.novabank.customer.exception;

public class DuplicatePhoneNumberException extends RuntimeException {
    public DuplicatePhoneNumberException(String value) {
        super("Ya existe un cliente con el número de teléfono: " + value);
    }
}
