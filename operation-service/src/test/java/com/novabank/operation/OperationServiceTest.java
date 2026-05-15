package com.novabank.operation;

import com.novabank.operation.customer.AccountServiceClient;
import com.novabank.operation.dto.*;
import com.novabank.operation.exception.AccountNotFoundException;
import com.novabank.operation.exception.InsufficientBalanceException;
import com.novabank.operation.service.ExchangeRateMockService;
import com.novabank.operation.service.OperationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OperationServiceTest {

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private ExchangeRateMockService exchangeRateService;

    @InjectMocks
    private OperationServiceImpl operationService;

    private AccountDTO sourceAccount;
    private AccountDTO destinationAccount;
    private TransactionDTO transactionDTO;
    private BigDecimal amount;

    @BeforeEach
    void setUp() {
        amount = new BigDecimal("500.00");

        sourceAccount = AccountDTO.builder()
                .accountId(1L)
                .accountNumber("ES9121000000000000000002")
                .accountHolder("Pepillo Grillo")
                .balance(new BigDecimal("1000.00"))
                .creationDate(LocalDateTime.now())
                .build();

        destinationAccount = AccountDTO.builder()
                .accountId(2L)
                .accountNumber("ES9121000000000000000003")
                .accountHolder("Pepilla Grilla")
                .balance(new BigDecimal("500.00"))
                .creationDate(LocalDateTime.now())
                .build();

        transactionDTO = TransactionDTO.builder()
                .transactionId(1L)
                .transactionType("DEPOSIT")
                .amount(amount)
                .description("Depósito")
                .creationDate(LocalDateTime.now())
                .accountId(1L)
                .build();
    }

    @Nested
    class DepositTest {

        @Test
        void deposit_validData_shouldReturnTransactionDTO() {
            when(accountServiceClient.getAccountByNumber("ES9121000000000000000002"))
                    .thenReturn(Mono.just(sourceAccount));
            when(accountServiceClient.updateBalance(eq(1L), eq(amount)))
                    .thenReturn(Mono.just(sourceAccount));
            when(accountServiceClient.createTransaction(eq(1L), any(CreateTransactionRequest.class)))
                    .thenReturn(Mono.just(transactionDTO));

            StepVerifier.create(operationService.deposit(
                            new CreateOperationRequest("ES9121000000000000000002", amount)))
                    .expectNext(transactionDTO)
                    .verifyComplete();

            verify(accountServiceClient).updateBalance(eq(1L), eq(amount));
            verify(accountServiceClient).createTransaction(eq(1L), any(CreateTransactionRequest.class));
        }

        @Test
        void deposit_accountNotFound_shouldThrowException() {
            when(accountServiceClient.getAccountByNumber("ES0000000000000000000000"))
                    .thenReturn(Mono.empty());

            StepVerifier.create(operationService.deposit(
                            new CreateOperationRequest("ES0000000000000000000000", amount)))
                    .expectError(AccountNotFoundException.class)
                    .verify();

            verify(accountServiceClient, never()).updateBalance(any(), any());
        }

        @Test
        void deposit_negativeAmount_shouldThrowException() {
            StepVerifier.create(operationService.deposit(
                            new CreateOperationRequest("ES9121000000000000000002",
                                    new BigDecimal("-100.00"))))
                    .expectError(IllegalArgumentException.class)
                    .verify();

            verifyNoInteractions(accountServiceClient);
        }

        @Test
        void deposit_zeroAmount_shouldThrowException() {
            StepVerifier.create(operationService.deposit(
                            new CreateOperationRequest("ES9121000000000000000002", BigDecimal.ZERO)))
                    .expectError(IllegalArgumentException.class)
                    .verify();

            verifyNoInteractions(accountServiceClient);
        }
    }

    @Nested
    class WithdrawTest {

        @Test
        void withdraw_validData_shouldReturnTransactionDTO() {
            when(accountServiceClient.getAccountByNumber("ES9121000000000000000002"))
                    .thenReturn(Mono.just(sourceAccount));
            when(accountServiceClient.updateBalance(eq(1L), eq(amount.negate())))
                    .thenReturn(Mono.just(sourceAccount));
            when(accountServiceClient.createTransaction(eq(1L), any(CreateTransactionRequest.class)))
                    .thenReturn(Mono.just(transactionDTO));

            StepVerifier.create(operationService.withdraw(
                            new CreateOperationRequest("ES9121000000000000000002", amount)))
                    .expectNext(transactionDTO)
                    .verifyComplete();

            verify(accountServiceClient).updateBalance(eq(1L), eq(amount.negate()));
            verify(accountServiceClient).createTransaction(eq(1L), any(CreateTransactionRequest.class));
        }

        @Test
        void withdraw_insufficientBalance_shouldThrowException() {
            when(accountServiceClient.getAccountByNumber("ES9121000000000000000002"))
                    .thenReturn(Mono.just(sourceAccount));

            StepVerifier.create(operationService.withdraw(
                            new CreateOperationRequest("ES9121000000000000000002",
                                    new BigDecimal("9999.00"))))
                    .expectError(InsufficientBalanceException.class)
                    .verify();

            verify(accountServiceClient, never()).updateBalance(any(), any());
        }

        @Test
        void withdraw_negativeAmount_shouldThrowException() {
            StepVerifier.create(operationService.withdraw(
                            new CreateOperationRequest("ES9121000000000000000002",
                                    new BigDecimal("-100.00"))))
                    .expectError(IllegalArgumentException.class)
                    .verify();

            verifyNoInteractions(accountServiceClient);
        }
    }

    @Nested
    class TransferTest {

        @Test
        void transfer_validData_shouldReturnTwoTransactions() {
            when(exchangeRateService.getRate("EUR", "EUR"))
                    .thenReturn(Mono.just(BigDecimal.ONE));
            when(accountServiceClient.getAccountByNumber("ES9121000000000000000002"))
                    .thenReturn(Mono.just(sourceAccount));
            when(accountServiceClient.getAccountByNumber("ES9121000000000000000003"))
                    .thenReturn(Mono.just(destinationAccount));
            when(accountServiceClient.updateBalance(any(), any()))
                    .thenReturn(Mono.just(sourceAccount));
            when(accountServiceClient.createTransaction(any(), any()))
                    .thenReturn(Mono.just(transactionDTO));

            StepVerifier.create(operationService.transfer(
                            new CreateTransferRequest(
                                    "ES9121000000000000000002",
                                    "ES9121000000000000000003",
                                    amount, "EUR")))
                    .expectNextCount(2)
                    .verifyComplete();
        }

        @Test
        void transfer_sameAccount_shouldThrowException() {
            StepVerifier.create(operationService.transfer(
                            new CreateTransferRequest(
                                    "ES9121000000000000000002",
                                    "ES9121000000000000000002",
                                    amount, "EUR")))
                    .expectError(IllegalArgumentException.class)
                    .verify();

            verifyNoInteractions(accountServiceClient);
        }

        @Test
        void transfer_insufficientBalance_shouldThrowException() {
            when(exchangeRateService.getRate("EUR", "EUR"))
                    .thenReturn(Mono.just(BigDecimal.ONE));
            when(accountServiceClient.getAccountByNumber("ES9121000000000000000002"))
                    .thenReturn(Mono.just(sourceAccount));

            StepVerifier.create(operationService.transfer(
                            new CreateTransferRequest(
                                    "ES9121000000000000000002",
                                    "ES9121000000000000000003",
                                    new BigDecimal("9999.00"), "EUR")))
                    .expectError(InsufficientBalanceException.class)
                    .verify();

            verify(accountServiceClient, never()).updateBalance(any(), any());
        }
    }
}