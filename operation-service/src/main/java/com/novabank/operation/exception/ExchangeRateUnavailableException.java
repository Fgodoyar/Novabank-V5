package com.novabank.operation.exception;

public class ExchangeRateUnavailableException extends RuntimeException {

    public ExchangeRateUnavailableException(String from, String to, Throwable cause) {
        super("Tipo de cambio no disponible para " + from + " -> " + to +
                ". Operación abortada.", cause);
    }
}