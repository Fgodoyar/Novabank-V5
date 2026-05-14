package com.novabank.account.service;

import com.novabank.account.domain.Transaction;
import com.novabank.account.dto.CreateTransactionRequest;
import com.novabank.account.dto.TransactionDTO;
import com.novabank.account.exception.AccountNotFoundException;
import com.novabank.account.mapper.AccountMapper;
import com.novabank.account.mapper.TransactionMapper;
import com.novabank.account.repository.AccountRepository;
import com.novabank.account.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    @Transactional
    @Override
    public Mono<TransactionDTO> createTransaction(Long accountId, CreateTransactionRequest request) {
        return accountRepository.findByAccountId(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Cuenta no encontrada con ID: " + accountId)))
                .flatMap(account -> {
                    Transaction transaction = Transaction.builder()
                            .transactionType(request.transactionType())
                            .amount(request.amount())
                            .description(request.description())
                            .accountId(accountId)
                            .build();
                    return transactionRepository.save(transaction);
                })
                .map(transactionMapper::toDTO);

    }

    @Transactional(readOnly = true)
    @Override
    public Flux<TransactionDTO> findByAccountId(Long accountId) {
        return accountRepository.findByAccountId(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Cuenta no encontrada con ID: " + accountId)))
                .flatMapMany(account ->
                    transactionRepository.findByAccountId(accountId))
                .map(transactionMapper::toDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<TransactionDTO> findByRangeBetweenDate(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }

        return accountRepository.findByAccountId(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Cuenta no encontrada con ID: " + accountId)))
                        .flatMapMany(account ->
                                transactionRepository.findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(accountId, startDate, endDate))
                .map(transactionMapper::toDTO);
    }
}
