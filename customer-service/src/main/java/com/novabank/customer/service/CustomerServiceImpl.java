package com.novabank.customer.service;

import com.novabank.customer.domain.Customer;
import com.novabank.customer.dto.CreateCustomerRequest;
import com.novabank.customer.dto.CustomerDTO;
import com.novabank.customer.exception.CustomerNotFoundException;
import com.novabank.customer.exception.DuplicateDniException;
import com.novabank.customer.exception.DuplicateEmailException;
import com.novabank.customer.exception.DuplicatePhoneNumberException;
import com.novabank.customer.mapper.CustomerMapper;
import com.novabank.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional(readOnly = true)
    @Override
    public Flux<CustomerDTO> listCustomers() {
        return customerRepository.findAll()
                .map(customerMapper::toDTO)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("No hay clientes disponibles")));
    }

    @Transactional(readOnly = true)
    @Override
    public Mono<CustomerDTO> findById(Long customerId) {
        return customerRepository.findById(customerId)
                .map(customerMapper::toDTO)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Cliente no encontrado con ID: " + customerId)));
    }

    @Transactional
    @Override
    public Mono<CustomerDTO> createCustomer(CreateCustomerRequest request) {
        return customerRepository.existsByDni(request.dni())
                .flatMap(dniExists -> {
                    if (dniExists) return Mono.error(new DuplicateDniException(
                            "Ya existe un cliente con este DNI: " + request.dni()));
                    return customerRepository.existsByEmail(request.email());
                })
                .flatMap(emailExists -> {
                    if (emailExists) return Mono.error(new DuplicateEmailException(
                            "Ya existe un cliente con este email: " + request.email()));
                    return customerRepository.existsByPhoneNumber(request.phoneNumber());
                })
                .flatMap(phoneExists -> {
                    if (phoneExists) return Mono.error(new DuplicatePhoneNumberException(
                            "Ya existe un cliente con este teléfono: " + request.phoneNumber()));
                    return customerRepository.save(customerMapper.toEntity(request));
                })
                .map(customerMapper::toDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Mono<CustomerDTO> findByDni(String dni) {
        return customerRepository.findByDni(dni)
                .map(customerMapper::toDTO)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Cliente no encontrado con DNI: " + dni)));
    }

    @Transactional(readOnly = true)
    @Override
    public Mono<CustomerDTO> findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(customerMapper::toDTO)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Cliente no encontrado con email: " + email)));
    }

}