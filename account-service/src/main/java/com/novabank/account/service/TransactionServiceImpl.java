package com.novabank.account.service;

import com.novabank.account.domain.Account;
import com.novabank.account.domain.Transaction;
import com.novabank.account.dto.CreateTransactionRequest;
import com.novabank.account.dto.TransactionDTO;
import com.novabank.account.mapper.TransactionMapper;
import com.novabank.account.repository.AccountRepository;
import com.novabank.account.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    @Override
    public TransactionDTO createTransaction(Long accountId, CreateTransactionRequest request) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cuenta con ID: " + accountId));

        Transaction transaction = Transaction.builder()
                .transactionType(request.transactionType())
                .amount(request.amount())
                .description(request.description())
                .creationDate(LocalDateTime.now())
                .account(account)
                .build();

        return transactionMapper.toDTO(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionDTO> findByAccountId(Long accountId) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cuenta con el ID: " + accountId));
        return transactionRepository.findByAccount_AccountId(account.getAccountId()).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionDTO> findByRangeBetweenDate(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cuenta con el ID: " + accountId));

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }

        return transactionRepository.findByAccount_AccountIdAndCreationDateBetweenOrderByCreationDateDesc(account.getAccountId(), startDate, endDate).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
