package com.novabank.account.service;

import com.novabank.account.customer.CustomerServiceClient;
import com.novabank.account.domain.Account;
import com.novabank.account.dto.AccountDTO;
import com.novabank.account.dto.CreateAccountRequest;
import com.novabank.account.exception.AccountNotFoundException;
import com.novabank.account.exception.CustomerNotFoundException;
import com.novabank.account.mapper.AccountMapper;
import com.novabank.account.mapper.TransactionMapper;
import com.novabank.account.repository.AccountRepository;
import com.novabank.account.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerServiceClient customerServiceClient;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    @Transactional
    @Override
    public Mono<AccountDTO> createAccount(CreateAccountRequest request) {
        return customerServiceClient.getCustomer(request.customerId())
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Cliente no encontrado con ID: " + request.customerId())))
                .flatMap(customer -> accountRepository.existsByCustomerId(request.customerId()))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException(
                                "El cliente ya tiene una cuenta registrada"));
                    }
                    Account account = accountMapper.toEntity(request);
                    account.setAccountNumber(generateAccountNumber());
                    account.setBalance(BigDecimal.ZERO);
                    return accountRepository.save(account); // ← misma instancia
                })
                .map(accountMapper::toDTO);
    }

    @Transactional
    @Override
    public Mono<AccountDTO> updateBalance(Long accountId, BigDecimal amount) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId.toString())))
                .flatMap(account -> {
                    account.setBalance(account.getBalance().add(amount));
                    return accountRepository.save(account);
                })
                .map(accountMapper::toDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<AccountDTO> findByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId)
                .map(accountMapper::toDTO); // ← eliminado switchIfEmpty con error
    }

    @Transactional(readOnly = true)
    @Override
    public Mono<AccountDTO> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(accountMapper::toDTO)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountNumber)));
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<AccountDTO> findByCustomerIdWithTransactions(Long customerId) {
        return accountRepository.findByCustomerId(customerId)
                .flatMap(account ->
                        transactionRepository.findByAccountId(account.getAccountId())
                                .map(transactionMapper::toDTO)
                                .collectList()
                                .map(transactions -> AccountDTO.builder()
                                        .accountId(account.getAccountId())
                                        .accountNumber(account.getAccountNumber())
                                        .accountHolder(account.getAccountHolder())
                                        .balance(account.getBalance())
                                        .creationDate(account.getCreationDate())
                                        .customerId(account.getCustomerId())
                                        .transactions(transactions)
                                        .build()
                                )
                );
    }

    private String generateAccountNumber() {
        return "ES91210000" + String.format("%012d", new Random().nextLong(1_000_000_000_000L));
    }
}