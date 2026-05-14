package com.novabank.account.repository;

import com.novabank.account.domain.Transaction;
import com.novabank.account.dto.TransactionDTO;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, Long> {
    Flux<Transaction> findByAccountId(Long account_id);
    Flux<Transaction> findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(Long account_id, LocalDateTime startDate, LocalDateTime endDate);
}
