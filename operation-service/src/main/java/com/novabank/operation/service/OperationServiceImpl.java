package com.novabank.operation.service;


import com.novabank.operation.customer.AccountServiceClient;
import com.novabank.operation.dto.*;
import com.novabank.operation.exception.AccountNotFoundException;
import com.novabank.operation.exception.InsufficientBalanceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationService {

    private final AccountServiceClient accountServiceClient;
    private final ExchangeRateMockService exchangeRateMockService;

    @Override
    public Mono<TransactionDTO> deposit(CreateOperationRequest request) {

        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("El monto tiene que ser mayor que 0."));
        }

        return accountServiceClient.getAccountByNumber(request.accountNumber())
                .switchIfEmpty(Mono.error(new AccountNotFoundException(request.accountNumber())))
                .flatMap(account ->
                        accountServiceClient.updateBalance(account.accountId(), request.amount())
                                .flatMap(updated -> accountServiceClient.createTransaction(
                                        account.accountId(),
                                        new CreateTransactionRequest("DEPOSITO", request.amount(),
                                                "Depósito en cuenta " + account.accountNumber()))));
    }

    @Override
    public Mono<TransactionDTO> withdraw(CreateOperationRequest request) {

        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("El monto tiene que ser mayor que 0."));
        }

        return accountServiceClient.getAccountByNumber(request.accountNumber())
                .switchIfEmpty(Mono.error(new AccountNotFoundException(request.accountNumber())))
                .flatMap(account -> {
                    if (account.balance().compareTo(request.amount()) < 0) {
                        return Mono.error(new InsufficientBalanceException(
                                account.accountNumber(), account.balance(), request.amount()));
                    }
                    return accountServiceClient.updateBalance(account.accountId(), request.amount().negate())
                            .flatMap(updated -> accountServiceClient.createTransaction(
                                    account.accountId(),
                                    new CreateTransactionRequest("RETIRO", request.amount(),
                                            "Retiro de cuenta " + account.accountNumber())));
                });
    }

    @Override
    public Flux<TransactionDTO> transfer(CreateTransferRequest request) {

        if (request.fromAccountNumber().equals(request.toAccountNumber())) {
            return Flux.error(new IllegalArgumentException(
                    "La cuenta origen no puede ser igual a la cuenta destino."));
        }

        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            return Flux.error(new IllegalArgumentException(
                    "El monto tiene que ser mayor que 0."));
        }

        return exchangeRateMockService.getRate(request.currency(), "EUR")
                .map(rate -> request.amount().multiply(rate))
                .flatMapMany(amountInEuros ->
                        accountServiceClient.getAccountByNumber(request.fromAccountNumber())
                                .switchIfEmpty(Mono.error(new AccountNotFoundException(
                                        request.fromAccountNumber())))
                                .flatMapMany(source -> {
                                    if (source.balance().compareTo(amountInEuros) < 0) {
                                        return Flux.error(new InsufficientBalanceException(
                                                source.accountNumber(), source.balance(), amountInEuros));
                                    }
                                    return accountServiceClient.getAccountByNumber(request.toAccountNumber())
                                            .switchIfEmpty(Mono.error(new AccountNotFoundException(
                                                    request.toAccountNumber())))
                                            .flatMapMany(destination -> Mono.zip(
                                                            accountServiceClient.updateBalance(
                                                                    source.accountId(), amountInEuros.negate()),
                                                            accountServiceClient.updateBalance(
                                                                    destination.accountId(), amountInEuros))
                                                    .thenMany(Flux.merge(
                                                            accountServiceClient.createTransaction(
                                                                    source.accountId(),
                                                                    new CreateTransactionRequest(
                                                                            "TRANSFERENCIA_SALIENTE", amountInEuros,
                                                                            "Transferencia a " + request.toAccountNumber() +
                                                                                    " (" + request.amount() + " " + request.currency() + ")")),
                                                            accountServiceClient.createTransaction(
                                                                    destination.accountId(),
                                                                    new CreateTransactionRequest(
                                                                            "TRANSFERENCIA_ENTRANTE", amountInEuros,
                                                                            "Transferencia de " + request.fromAccountNumber() +
                                                                                    " (" + request.amount() + " " + request.currency() + ")"))
                                                    )));
                                })
                );
    }
}