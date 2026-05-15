package com.novabank.account;

import com.novabank.account.config.SecurityConfig;
import com.novabank.account.controller.AccountController;
import com.novabank.account.dto.AccountDTO;
import com.novabank.account.dto.CreateAccountRequest;
import com.novabank.account.exception.AccountNotFoundException;
import com.novabank.account.exception.CustomerNotFoundException;
import com.novabank.account.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(AccountController.class)
@Import(SecurityConfig.class)
@WithMockUser
@TestPropertySource(properties = {
        "spring.config.import="
})
public class AccountControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccountService accountService;

    private AccountDTO accountDTO;

    private static final String VALID_JSON = """
            {
              "customerId": 1
            }
            """;

    @BeforeEach
    void setUp() {
        accountDTO = AccountDTO.builder()
                .accountId(1L)
                .accountNumber("ES9121000000000000000002")
                .accountHolder("Pepillo Grillo")
                .balance(new BigDecimal("1500.00"))
                .creationDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .customerId(1L)
                .build();
    }

    @Nested
    class CreateAccountTest {

        @Test
        @DisplayName("POST /api/accounts → 201 con cuenta creada")
        void createAccount_shouldReturn201WhenValid() {
            when(accountService.createAccount(any(CreateAccountRequest.class)))
                    .thenReturn(Mono.just(accountDTO));

            webTestClient.post().uri("/api/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_JSON)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.accountId").isEqualTo(1)
                    .jsonPath("$.accountNumber").isEqualTo("ES9121000000000000000002");

            verify(accountService).createAccount(any(CreateAccountRequest.class));
        }

        @Test
        @DisplayName("POST /api/accounts → 400 si el body está vacío")
        void createAccount_shouldReturn400WhenInvalid() {
            webTestClient.post().uri("/api/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("{}")
                    .exchange()
                    .expectStatus().isBadRequest();

            verifyNoInteractions(accountService);
        }

        @Test
        @DisplayName("POST /api/accounts → 404 si el cliente no existe")
        void createAccount_shouldReturn404WhenCustomerNotFound() {
            when(accountService.createAccount(any(CreateAccountRequest.class)))
                    .thenReturn(Mono.error(new CustomerNotFoundException("Cliente no encontrado con ID: 1")));

            webTestClient.post().uri("/api/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_JSON)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("POST /api/accounts → 400 si el cliente ya tiene cuenta")
        void createAccount_shouldReturn400WhenDuplicate() {
            when(accountService.createAccount(any(CreateAccountRequest.class)))
                    .thenReturn(Mono.error(new IllegalArgumentException("El cliente ya tiene una cuenta registrada")));

            webTestClient.post().uri("/api/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_JSON)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    class FindByAccountNumberTest {

        @Test
        @DisplayName("GET /api/accounts/number/{accountNumber} → 200 cuando existe")
        void findByAccountNumber_shouldReturn200WhenFound() {
            when(accountService.findByAccountNumber("ES9121000000000000000002"))
                    .thenReturn(Mono.just(accountDTO));

            webTestClient.get().uri("/api/accounts/number/ES9121000000000000000002")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.accountNumber").isEqualTo("ES9121000000000000000002");
        }

        @Test
        @DisplayName("GET /api/accounts/number/{accountNumber} → 404 cuando no existe")
        void findByAccountNumber_shouldReturn404WhenNotFound() {
            when(accountService.findByAccountNumber("00000000X"))
                    .thenReturn(Mono.error(new AccountNotFoundException("Cuenta no encontrada")));

            webTestClient.get().uri("/api/accounts/number/00000000X")
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class FindByCustomerIdTest {

        @Test
        @DisplayName("GET /api/accounts/customer/{customerId} → 200 con lista")
        void findByCustomerId_shouldReturn200WithList() {
            when(accountService.findByCustomerId(1L))
                    .thenReturn(Flux.just(accountDTO));

            webTestClient.get().uri("/api/accounts/customer/1")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(AccountDTO.class)
                    .hasSize(1);
        }

        @Test
        @DisplayName("GET /api/accounts/customer/{customerId} → 200 lista vacía")
        void findByCustomerId_shouldReturn200WithEmptyList() {
            when(accountService.findByCustomerId(1L))
                    .thenReturn(Flux.empty());

            webTestClient.get().uri("/api/accounts/customer/1")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(AccountDTO.class)
                    .hasSize(0);
        }
    }

    @Nested
    class FindByCustomerIdWithTransactionsTest {

        @Test
        @DisplayName("GET /api/accounts/customer/{customerId}/transactions → 200 con lista")
        void findByCustomerIdWithTransactions_shouldReturn200WhenFound() {
            when(accountService.findByCustomerIdWithTransactions(1L))
                    .thenReturn(Flux.just(accountDTO));

            webTestClient.get().uri("/api/accounts/customer/1/transactions")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(AccountDTO.class)
                    .hasSize(1);
        }

        @Test
        @DisplayName("GET /api/accounts/customer/{customerId}/transactions → 404 cuando no existe")
        void findByCustomerIdWithTransactions_shouldReturn404WhenNotFound() {
            when(accountService.findByCustomerIdWithTransactions(100L))
                    .thenReturn(Flux.error(new AccountNotFoundException("Cuenta no encontrada")));

            webTestClient.get().uri("/api/accounts/customer/100/transactions")
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }
}