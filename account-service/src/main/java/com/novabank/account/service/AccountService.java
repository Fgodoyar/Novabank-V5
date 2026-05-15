package com.novabank.account.service;

import com.novabank.account.dto.AccountDTO;
import com.novabank.account.dto.CreateAccountRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface AccountService {
    AccountDTO createAccount(CreateAccountRequest request);
    AccountDTO updateBalance(Long accountId, BigDecimal amount);
    List<AccountDTO> findByCustomerId(Long customerId);
    AccountDTO findByAccountNumber(String accountNumber);
    List<AccountDTO> findByCustomerIdWithTransactions(Long customerId);
}
