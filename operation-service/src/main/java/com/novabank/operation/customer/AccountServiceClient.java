package com.novabank.operation.customer;

import com.novabank.operation.dto.AccountDTO;
import com.novabank.operation.dto.CreateTransactionRequest;
import com.novabank.operation.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AccountServiceClient {

    private final WebClient webClient;

    public AccountServiceClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://ACCOUNT-SERVICE")
                .build();
    }

    public Mono<AccountDTO> getAccountById(Long accountId) {
        return webClient.get()
                .uri("/api/accounts/{accountId}", accountId)
                .retrieve()
                .bodyToMono(AccountDTO.class);
    }

    public Mono<AccountDTO> getAccountByNumber(String accountNumber) {
        return webClient.get()
                .uri("/api/accounts/number/{accountNumber}", accountNumber)
                .retrieve()
                .bodyToMono(AccountDTO.class);
    }

    public Mono<AccountDTO> updateBalance(Long accountId, BigDecimal amount) {
        return webClient.put()
                .uri("/api/accounts/{accountId}/balance?amount={amount}", accountId, amount)
                .retrieve()
                .bodyToMono(AccountDTO.class);
    }

    public Mono<TransactionDTO> createTransaction(Long accountId, CreateTransactionRequest request) {
        return webClient.post()
                .uri("/api/accounts/{accountId}/transactions", accountId)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TransactionDTO.class);
    }
}