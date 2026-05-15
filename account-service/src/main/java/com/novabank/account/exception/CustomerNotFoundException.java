package com.novabank.account.exception;

public class CustomerNotFoundException extends RuntimeException {
    private final Integer customer_id;

    public CustomerNotFoundException(Integer id) {
        super("Cliente no encontrado con ID: " + id);
        this.customer_id = id;
    }

    public CustomerNotFoundException(String dni) {
        super("Cliente no encontrado con DNI: " + dni);
        this.customer_id = null;
    }

    public Integer getCustomer_id() {
        return customer_id;
    }
}