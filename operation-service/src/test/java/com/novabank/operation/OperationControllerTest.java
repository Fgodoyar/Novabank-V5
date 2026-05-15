package com.novabank.operation;

import com.novabank.operation.config.SecurityConfig;
import com.novabank.operation.controller.OperationController;
import com.novabank.operation.dto.CreateOperationRequest;
import com.novabank.operation.dto.CreateTransferRequest;
import com.novabank.operation.dto.TransactionDTO;
import com.novabank.operation.exception.AccountNotFoundException;
import com.novabank.operation.exception.InsufficientBalanceException;
import com.novabank.operation.service.ExchangeRateMockService;
import com.novabank.operation.service.OperationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@WebFluxTest(OperationController.class)
@WithMockUser
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "spring.config.import=",
        "spring.main.allow-bean-definition-overriding=true"
})
public class OperationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

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
              "amount": 500.00,
              "currency": "EUR"
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
        void deposit_shouldReturn200WhenValid() {
            when(operationService.deposit(any(CreateOperationRequest.class)))
                    .thenReturn(Mono.just(transactionDTO));

            webTestClient.post().uri("/api/operations/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_OPERATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.transactionId").isEqualTo(1L);
        }

        @Test
        @DisplayName("POST /api/operations/deposit → 404 si cuenta no existe")
        void deposit_shouldReturn404WhenAccountNotFound() {
            when(operationService.deposit(any(CreateOperationRequest.class)))
                    .thenReturn(Mono.error(new AccountNotFoundException("ES9121000000000000000002")));

            webTestClient.post().uri("/api/operations/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_OPERATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class WithdrawTest {

        @Test
        @DisplayName("POST /api/operations/withdraw → 200 con transacción creada")
        void withdrawal_shouldReturn200WhenValid() {
            when(operationService.withdraw(any(CreateOperationRequest.class)))
                    .thenReturn(Mono.just(transactionDTO));

            webTestClient.post().uri("/api/operations/withdraw")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_OPERATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.transactionId").isEqualTo(1L);
        }

        @Test
        @DisplayName("POST /api/operations/withdraw → 422 si saldo insuficiente")
        void withdrawal_shouldReturn422WhenInsufficientBalance() {
            when(operationService.withdraw(any(CreateOperationRequest.class)))
                    .thenReturn(Mono.error(new InsufficientBalanceException(
                            "ES9121000000000000000002",
                            new BigDecimal("100.00"),
                            new BigDecimal("500.00"))));

            webTestClient.post().uri("/api/operations/withdraw")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_OPERATION_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(422);
        }
    }

    @Nested
    class TransferTest {

        @Test
        @DisplayName("POST /api/operations/transfer → 200 con dos transacciones")
        void transfer_shouldReturn200WhenValid() {
            when(operationService.transfer(any(CreateTransferRequest.class)))
                    .thenReturn(Flux.just(transactionDTO, transactionDTO));

            webTestClient.post().uri("/api/operations/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_TRANSFER_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(TransactionDTO.class)
                    .hasSize(2);
        }

        @Test
        @DisplayName("POST /api/operations/transfer → 422 si saldo insuficiente")
        void transfer_shouldReturn422WhenInsufficientBalance() {
            when(operationService.transfer(any(CreateTransferRequest.class)))
                    .thenReturn(Flux.error(new InsufficientBalanceException(
                            "ES9121000000000000000002",
                            new BigDecimal("100.00"),
                            new BigDecimal("500.00"))));

            webTestClient.post().uri("/api/operations/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_TRANSFER_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(422);
        }
    }
}