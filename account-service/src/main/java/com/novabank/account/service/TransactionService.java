package com.novabank.account.service;


import com.novabank.account.dto.CreateTransactionRequest;
import com.novabank.account.dto.TransactionDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TransactionService {
    TransactionDTO createTransaction(Long accountId, CreateTransactionRequest request);
    List<TransactionDTO> findByAccountId(Long accountId);
    List<TransactionDTO> findByRangeBetweenDate(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
}

