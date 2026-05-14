package com.novabank.account.exception;

public class CustomerNotFoundException extends RuntimeException {
    private final Long customer_id;

    public CustomerNotFoundException(Long id) {
        super("Cliente no encontrado con ID: " + id);
        this.customer_id = id;
    }

    public CustomerNotFoundException(String dni) {
        super("Cliente no encontrado con DNI: " + dni);
        this.customer_id = null;
    }

    public Long getCustomer_id() {
        return customer_id;
    }
}