package com.novabank.customer.repository;

import com.novabank.customer.domain.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer,Long> {

    Mono<Customer> findByDni(String dni);
    Mono<Customer> findByEmail(String email);
    Mono<Boolean> existsByDni(String dni);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByPhoneNumber(String phoneNumber);
}
