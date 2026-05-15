package com.novabank.account.service;


import com.novabank.account.dto.CreateTransactionRequest;
import com.novabank.account.dto.TransactionDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public interface TransactionService {
    Mono<TransactionDTO> createTransaction(Long accountId, CreateTransactionRequest request);
    Flux<TransactionDTO> findByAccountId(Long accountId);
    Flux<TransactionDTO> findByRangeBetweenDate(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
}

