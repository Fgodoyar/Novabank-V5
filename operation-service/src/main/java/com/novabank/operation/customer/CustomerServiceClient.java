package com.novabank.operation.customer;

import com.novabank.operation.dto.AccountDTO;
import com.novabank.operation.dto.CreateTransactionRequest;
import com.novabank.operation.dto.CustomerDTO;
import com.novabank.operation.dto.TransactionDTO;
import com.novabank.operation.exception.CustomerNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@Slf4j
@Component
public class CustomerServiceClient {

    private final WebClient webClient;

    public CustomerServiceClient(@LoadBalanced WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://CUSTOMER-SERVICE")
                .build();
    }

    @CircuitBreaker(name = "customerService", fallbackMethod = "getCustomerFallback")
    public Mono<CustomerDTO> getCustomer(Long id) {
        return webClient.get()
                .uri("/api/customers/{id}", id)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new CustomerNotFoundException(id))
                )
                .bodyToMono(CustomerDTO.class);
    }

    public Mono<CustomerDTO> getCustomerFallback(Long id, Throwable ex) {
        log.error("Servicio de clientes caído para ID: {}. Causa: {}", id, ex.getMessage());
        return Mono.error(new CustomerNotFoundException(
                "Servicio de clientes no disponible, intente más tarde. ID: " + id));
    }

    public Mono<AccountDTO> getAccountByIdFallback(Long accountId, Throwable ex) {
        log.error("Account service caído para accountId: {}. Causa: {}", accountId, ex.getMessage());
        return Mono.error(new RuntimeException("Servicio de cuentas no disponible. ID: " + accountId));
    }

    public Mono<AccountDTO> getAccountByNumberFallback(String accountNumber, Throwable ex) {
        log.error("Account service caído para accountNumber: {}. Causa: {}", accountNumber, ex.getMessage());
        return Mono.error(new RuntimeException("Servicio de cuentas no disponible. Número: " + accountNumber));
    }

    public Mono<AccountDTO> updateBalanceFallback(Long accountId, BigDecimal amount, Throwable ex) {
        log.error("Account service caído al actualizar balance. accountId: {}. Causa: {}", accountId, ex.getMessage());
        return Mono.error(new RuntimeException("No se pudo actualizar el balance. ID: " + accountId));
    }

    public Mono<TransactionDTO> createTransactionFallback(Long accountId, CreateTransactionRequest request, Throwable ex) {
        log.error("Account service caído al crear transacción. accountId: {}. Causa: {}", accountId, ex.getMessage());
        return Mono.error(new RuntimeException("No se pudo registrar la transacción. ID: " + accountId));
    }
}