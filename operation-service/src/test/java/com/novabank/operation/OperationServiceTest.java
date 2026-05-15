package com.novabank.operation;

import com.novabank.operation.customer.AccountServiceClient;
import com.novabank.operation.customer.CustomerServiceClient;
import com.novabank.operation.dto.*;
import com.novabank.operation.exception.InsufficientBalanceException;
import com.novabank.operation.service.OperationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OperationServiceTest {

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private CustomerServiceClient customerServiceClient;

    @InjectMocks
    private OperationServiceImpl operationService;

    private AccountDTO sourceAccount;
    private AccountDTO destinationAccount;
    private TransactionDTO transactionDTO;
    private BigDecimal amount;

    @BeforeEach
    void setUp() {
        amount = new BigDecimal("500.00");

        sourceAccount = new AccountDTO(1L, "ES9121000000000000000002",
                "Pepillo Grillo", new BigDecimal("1000.00"), null);

        destinationAccount = new AccountDTO(2L, "ES9121000000000000000003",
                "Pepilla Grilla", new BigDecimal("500.00"), null);

        transactionDTO = new TransactionDTO(1L, "DEPOSIT", amount, "Depósito", null, 1L);
    }

    @Nested
    class DepositTest {

        @Test
        void deposit_validData_shouldReturnTransactionDTO() {
            when(accountServiceClient.getAccountByNumber("ES9121000000000000000002"))
                    .thenReturn(sourceAccount);
            when(accountServiceClient.createTransaction(any(), any()))
                    .thenReturn(transactionDTO);

            TransactionDTO result = operationService.deposit(
                    new CreateOperationRequest("ES9121000000000000000002", amount));

            assertNotNull(result);
            verify(accountServiceClient).updateBalance(eq(1L), eq(amount));
            verify(accountServiceClient).createTransaction(eq(1L), any(CreateTransactionRequest.class));
        }

        @Test
        void deposit_accountNotFound_shouldThrowException() {
            when(accountServiceClient.getAccountByNumber("ES0000000000000000000000"))
                    .thenReturn(null);

            assertThrows(NullPointerException.class, () ->
                    operationService.deposit(
                            new CreateOperationRequest("ES0000000000000000000000", amount))
            );
            verify(accountServiceClient, never()).updateBalance(any(), any());
        }

        @Test
        void deposit_negativeAmount_shouldThrowException() {
            assertThrows(IllegalArgumentException.class, () ->
                    operationService.deposit(
                            new CreateOperationRequest("ES9121000000000000000002",
                                    new BigDecimal("-100.00")))
            );
            verifyNoInteractions(accountServiceClient);
        }

        @Test
        void deposit_zeroAmount_shouldThrowException() {
            assertThrows(IllegalArgumentException.class, () ->
                    operationService.deposit(
                            new CreateOperationRequest("ES9121000000000000000002",
                                    BigDecimal.ZERO))
            );
            verifyNoInteractions(accountServiceClient);
        }
    }

    @Nested
    class WithdrawTest {

        @Test
        void withdraw_validData_shouldReturnTransactionDTO() {
            when(accountServiceClient.getAccountByNumber("ES9121000000000000000002"))
                    .thenReturn(sourceAccount);
            when(accountServiceClient.createTransaction(any(), any()))
                    .thenReturn(transactionDTO);

            TransactionDTO result = operationService.withdraw(
                    new CreateOperationRequest("ES9121000000000000000002", amount));

            assertNotNull(result);
            verify(accountServiceClient).updateBalance(eq(1L), eq(amount.negate()));
            verify(accountServiceClient).createTransaction(eq(1L), any(CreateTransactionRequest.class));
        }

        @Test
        void withdraw_insufficientBalance_shouldThrowException() {
            when(accountServiceClient.getAccountByNumber("ES9121000000000000000002"))
                    .thenReturn(sourceAccount);

            assertThrows(InsufficientBalanceException.class, () ->
                    operationService.withdraw(
                            new CreateOperationRequest("ES9121000000000000000002",
                                    new BigDecimal("9999.00")))
            );
            verify(accountServiceClient, never()).updateBalance(any(), any());
        }

        @Test
        void withdraw_negativeAmount_shouldThrowException() {
            assertThrows(IllegalArgumentException.class, () ->
                    operationService.withdraw(
                            new CreateOperationRequest("ES9121000000000000000002",
                                    new BigDecimal("-100.00")))
            );
            verifyNoInteractions(accountServiceClient);
        }
    }

    @Nested
    class TransferTest {

        @Test
        void transfer_validData_shouldReturnTwoTransactions() {
            when(accountServiceClient.getAccountByNumber("ES9121000000000000000002"))
                    .thenReturn(sourceAccount);
            when(accountServiceClient.getAccountByNumber("ES9121000000000000000003"))
                    .thenReturn(destinationAccount);
            when(accountServiceClient.createTransaction(any(), any()))
                    .thenReturn(transactionDTO);

            List<TransactionDTO> result = operationService.transfer(
                    new CreateTransferRequest(
                            "ES9121000000000000000002",
                            "ES9121000000000000000003",
                            amount));

            assertEquals(2, result.size());
            verify(accountServiceClient, times(2)).updateBalance(any(), any());
            verify(accountServiceClient, times(2)).createTransaction(any(), any());
        }

        @Test
        void transfer_sameAccount_shouldThrowException() {
            assertThrows(IllegalArgumentException.class, () ->
                    operationService.transfer(
                            new CreateTransferRequest(
                                    "ES9121000000000000000002",
                                    "ES9121000000000000000002",
                                    amount))
            );
            verifyNoInteractions(accountServiceClient);
        }

        @Test
        void transfer_insufficientBalance_shouldThrowException() {
            when(accountServiceClient.getAccountByNumber("ES9121000000000000000002"))
                    .thenReturn(sourceAccount);
            when(accountServiceClient.getAccountByNumber("ES9121000000000000000003"))
                    .thenReturn(destinationAccount);

            assertThrows(InsufficientBalanceException.class, () ->
                    operationService.transfer(
                            new CreateTransferRequest(
                                    "ES9121000000000000000002",
                                    "ES9121000000000000000003",
                                    new BigDecimal("9999.00")))
            );
            verify(accountServiceClient, never()).updateBalance(any(), any());
        }
    }
}