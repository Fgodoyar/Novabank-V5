package com.novabank.customer.service;

import com.novabank.customer.dto.CreateCustomerRequest;
import com.novabank.customer.dto.CustomerDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface CustomerService {
    Flux<CustomerDTO> listCustomers();
    Mono<CustomerDTO> findById(Long customerId);
    Mono<CustomerDTO> createCustomer(CreateCustomerRequest request);
    Mono<CustomerDTO> findByDni(String dni);
    Mono<CustomerDTO> findByEmail(String email);
}
