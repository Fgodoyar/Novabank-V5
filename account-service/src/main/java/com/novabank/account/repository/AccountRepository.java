package com.novabank.account.repository;

import com.novabank.account.domain.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account,Long> {
    Mono<Account> findByAccountId(Long accountId);
    Mono<Account> findByAccountNumber(String accountNumber);
    Flux<Account> findByCustomerId(Long customerId);
    Mono<Boolean> existsByCustomerId(Long customerId);
}
