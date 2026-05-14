package com.novabank.account.service;

import com.novabank.account.dto.AccountDTO;
import com.novabank.account.dto.CreateAccountRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public interface AccountService {
    Mono<AccountDTO> createAccount(CreateAccountRequest request);
    Mono<AccountDTO> updateBalance(Long accountId, BigDecimal amount);
    Flux<AccountDTO> findByCustomerId(Long customerId);
    Mono<AccountDTO> findByAccountNumber(String accountNumber);

    Flux<AccountDTO> findByCustomerIdWithTransactions(Long customerId);
}
