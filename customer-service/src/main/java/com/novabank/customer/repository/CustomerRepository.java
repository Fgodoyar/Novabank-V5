package com.novabank.customer.repository;

import com.novabank.customer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{

    Optional<Customer> findByDni(String dni);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phone_number);
}