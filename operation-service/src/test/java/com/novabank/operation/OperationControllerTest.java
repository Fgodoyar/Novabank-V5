package com.novabank.operation;

import com.novabank.operation.controller.OperationController;
import com.novabank.operation.dto.CreateOperationRequest;
import com.novabank.operation.dto.CreateTransferRequest;
import com.novabank.operation.dto.TransactionDTO;
import com.novabank.operation.exception.AccountNotFoundException;
import com.novabank.operation.exception.InsufficientBalanceException;
import com.novabank.operation.service.OperationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OperationController.class)
public class OperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OperationService operationService;

    private TransactionDTO transactionDTO;

    private static final String VALID_OPERATION_JSON = """
            {
              "accountNumber": "ES9121000000000000000002",
              "amount": 500.00
            }
            """;

    private static final String VALID_TRANSFER_JSON = """
            {
              "fromAccountNumber": "ES9121000000000000000002",
              "toAccountNumber": "ES9121000000000000000003",
              "amount": 500.00
            }
            """;

    @BeforeEach
    void setUp() {
        transactionDTO = new TransactionDTO(
                1L, "DEPOSIT", new BigDecimal("500.00"),
                "Operación realizada", LocalDateTime.of(2026, 1, 1, 0, 0), 1L);
    }

    @Nested
    class DepositTest {

        @Test
        @DisplayName("POST /api/operations/deposit → 200 con transacción creada")
        void deposit_shouldReturn200WhenValid() throws Exception {
            when(operationService.deposit(any(CreateOperationRequest.class)))
                    .thenReturn(transactionDTO);

            mockMvc.perform(post("/api/operations/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_OPERATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.transactionId").value(1L));
        }

        @Test
        @DisplayName("POST /api/operations/deposit → 404 si cuenta no existe")
        void deposit_shouldReturn404WhenAccountNotFound() throws Exception {
            when(operationService.deposit(any(CreateOperationRequest.class)))
                    .thenThrow(new AccountNotFoundException("ES9121000000000000000002"));

            mockMvc.perform(post("/api/operations/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_OPERATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class WithdrawTest {

        @Test
        @DisplayName("POST /api/operations/withdraw → 200 con transacción creada")
        void withdrawal_shouldReturn200WhenValid() throws Exception {
            when(operationService.withdraw(any(CreateOperationRequest.class)))
                    .thenReturn(transactionDTO);

            mockMvc.perform(post("/api/operations/withdraw")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_OPERATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.transactionId").value(1L));
        }

        @Test
        @DisplayName("POST /api/operations/withdraw → 422 si saldo insuficiente")
        void withdrawal_shouldReturn422WhenInsufficientBalance() throws Exception {
            when(operationService.withdraw(any(CreateOperationRequest.class)))
                    .thenThrow(new InsufficientBalanceException(
                            "ES9121000000000000000002",
                            new BigDecimal("100.00"),
                            new BigDecimal("500.00")));

            mockMvc.perform(post("/api/operations/withdraw")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_OPERATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    class TransferTest {

        @Test
        @DisplayName("POST /api/operations/transfer → 200 con dos transacciones")
        void transfer_shouldReturn200WhenValid() throws Exception {
            when(operationService.transfer(any(CreateTransferRequest.class)))
                    .thenReturn(List.of(transactionDTO, transactionDTO));

            mockMvc.perform(post("/api/operations/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_TRANSFER_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("POST /api/operations/transfer → 422 si saldo insuficiente")
        void transfer_shouldReturn422WhenInsufficientBalance() throws Exception {
            when(operationService.transfer(any(CreateTransferRequest.class)))
                    .thenThrow(new InsufficientBalanceException(
                            "ES9121000000000000000002",
                            new BigDecimal("100.00"),
                            new BigDecimal("500.00")));

            mockMvc.perform(post("/api/operations/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_TRANSFER_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }
}