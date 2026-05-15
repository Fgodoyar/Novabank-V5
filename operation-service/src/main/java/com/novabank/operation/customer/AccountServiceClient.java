package com.novabank.operation.customer;

import com.novabank.operation.dto.AccountDTO;
import com.novabank.operation.dto.CreateTransactionRequest;
import com.novabank.operation.dto.TransactionDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Component
public class AccountServiceClient {

    private final WebClient webClient;

    public AccountServiceClient(@LoadBalanced WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://ACCOUNT-SERVICE")
                .build();
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "getAccountByNumberFallback")
    public Mono<AccountDTO> getAccountByNumber(String accountNumber) {
        return webClient.get()
                .uri("/api/accounts/number/{accountNumber}", accountNumber)
                .retrieve()
                .bodyToMono(AccountDTO.class);
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "updateBalanceFallback")
    public Mono<AccountDTO> updateBalance(Long accountId, BigDecimal amount) {
        return webClient.put()
                .uri("/api/accounts/{accountId}/balance?amount={amount}", accountId, amount)
                .retrieve()
                .bodyToMono(AccountDTO.class);
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "createTransactionFallback")
    public Mono<TransactionDTO> createTransaction(Long accountId, CreateTransactionRequest request) {
        return webClient.post()
                .uri("/api/accounts/{accountId}/transactions", accountId)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TransactionDTO.class);
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