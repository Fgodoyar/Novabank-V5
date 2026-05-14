package com.novabank.operation.service;



import com.novabank.operation.dto.CreateOperationRequest;
import com.novabank.operation.dto.CreateTransferRequest;
import com.novabank.operation.dto.TransactionDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OperationService {
    Mono<TransactionDTO> deposit(CreateOperationRequest request);
    Mono<TransactionDTO> withdraw(CreateOperationRequest request);
    Flux<TransactionDTO> transfer(CreateTransferRequest request);
}
