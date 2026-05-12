package com.novabank.customer.exception;

public class DuplicateDniException extends RuntimeException {
    public DuplicateDniException(String value) {
        super("Ya existe un cliente con el DNI: " + value);
    }
}
