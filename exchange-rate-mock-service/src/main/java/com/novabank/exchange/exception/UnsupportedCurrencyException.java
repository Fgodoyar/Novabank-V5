package com.novabank.exchange.exception;

public class UnsupportedCurrencyException extends RuntimeException {
    public UnsupportedCurrencyException(String currency) {
        super("Unsupported currency: " + currency);
    }
}
