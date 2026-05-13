package com.novabank.account.service;


import com.novabank.account.dto.CustomerDTO;
import com.novabank.account.customer.CustomerServiceClient;
import com.novabank.account.domain.Account;
import com.novabank.account.dto.AccountDTO;
import com.novabank.account.dto.CreateAccountRequest;
import com.novabank.account.exception.AccountNotFoundException;
import com.novabank.account.exception.CustomerNotFoundException;
import com.novabank.account.mapper.AccountMapper;
import com.novabank.account.repository.AccountRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CustomerServiceClient customerServiceClient;
    private final AccountMapper accountMapper;

    @Transactional
    @Override
    public AccountDTO createAccount(CreateAccountRequest request) {

        CustomerDTO customer = customerServiceClient.getCustomer(request.customerId());

        if (customer == null) {
            throw new CustomerNotFoundException("Cliente no encontrado con ID: " + request.customerId());
        }

        if (accountRepository.existsByCustomerId(request.customerId())) {
            throw new IllegalArgumentException("El cliente ya tiene una cuenta registrada");
        }

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .accountHolder(customer.customerName() + " " + customer.lastName())
                .customerId(request.customerId())
                .creationDate(LocalDateTime.now())
                .balance(BigDecimal.ZERO)
                .build();

        Account saved = accountRepository.saveAndFlush(account);
        return accountMapper.toDTO(saved);
    }

    @Transactional
    @Override
    public AccountDTO updateBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId.toString()));
        account.setBalance(account.getBalance().add(amount));
        return accountMapper.toDTO(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccountDTO> findByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId).stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public AccountDTO findByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        return accountMapper.toDTO(account);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccountDTO> findByCustomerIdWithTransactions(Long customerId) {
        return accountRepository.findByCustomerIdWithTransactions(customerId).stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }

    private String generateAccountNumber() {
        return "ES91210000" + String.format("%012d", new
                Random().nextLong(1_000_000_000_000L));
    }
}
