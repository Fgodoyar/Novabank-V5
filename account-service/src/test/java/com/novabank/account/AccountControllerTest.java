package com.novabank.account;

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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(SecurityConfig.class)
@WithMockUser
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
        void createAccount_shouldReturn201WhenValid() throws Exception {
            when(accountService.createAccount(any(CreateAccountRequest.class)))
                    .thenReturn(accountDTO);

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accountId").value(1L))
                    .andExpect(jsonPath("$.accountNumber").value("ES9121000000000000000002"));

            verify(accountService).createAccount(any(CreateAccountRequest.class));
        }

        @Test
        @DisplayName("POST /api/accounts → 400 si el body está vacío")
        void createAccount_shouldReturn400WhenInvalid() throws Exception {

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(accountService);
        }

        @Test
        @DisplayName("POST /api/accounts → 404 si el cliente no existe")
        void createAccount_shouldReturn404WhenCustomerNotFound() throws Exception {

            when(accountService.createAccount(any(CreateAccountRequest.class)))
                    .thenThrow(new CustomerNotFoundException("Cliente no encontrado con ID: 1"));

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("POST /api/accounts → 400 si el cliente ya tiene cuenta")
        void createAccount_shouldReturn400WhenDuplicate() throws Exception {

            when(accountService.createAccount(any(CreateAccountRequest.class)))
                    .thenThrow(new IllegalArgumentException("El cliente ya tiene una cuenta registrada"));

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class FindByAccountNumberTest {

        @Test
        @DisplayName("GET /api/accounts/number/{accountNumber} → 200 cuando existe")
        void findByAccountNumber_shouldReturn200WhenFound() throws Exception {

            when(accountService.findByAccountNumber("ES9121000000000000000002"))
                    .thenReturn(accountDTO);

            mockMvc.perform(get("/api/accounts/number/ES9121000000000000000002"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountNumber").value("ES9121000000000000000002"));
        }

        @Test
        @DisplayName("GET /api/accounts/number/{accountNumber} → 404 cuando no existe")
        void findByAccountNumber_shouldReturn404WhenNotFound() throws Exception {

            when(accountService.findByAccountNumber("00000000X"))
                    .thenThrow(new AccountNotFoundException("Cuenta no encontrada"));

            mockMvc.perform(get("/api/accounts/number/00000000X"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class FindByCustomerIdTest {

        @Test
        @DisplayName("GET /api/accounts/customer/{customerId} → 200 con lista")
        void findByCustomerId_shouldReturn200WithList() throws Exception {

            when(accountService.findByCustomerId(1L))
                    .thenReturn(List.of(accountDTO));

            mockMvc.perform(get("/api/accounts/customer/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("GET /api/accounts/customer/{customerId} → 200 lista vacía")
        void findByCustomerId_shouldReturn200WithEmptyList() throws Exception {

            when(accountService.findByCustomerId(1L))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/accounts/customer/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    class FindByCustomerIdWithTransactionsTest {

        @Test
        @DisplayName("GET /api/accounts/customer/{customerId}/transactions → 200 con lista")
        void findByCustomerIdWithTransactions_shouldReturn200WhenFound() throws Exception {

            when(accountService.findByCustomerIdWithTransactions(1L))
                    .thenReturn(List.of(accountDTO));

            mockMvc.perform(get("/api/accounts/customer/1/transactions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("GET /api/accounts/customer/{customerId}/transactions → 404 cuando no existe")
        void findByCustomerIdWithTransactions_shouldReturn404WhenNotFound() throws Exception {

            when(accountService.findByCustomerIdWithTransactions(100L))
                    .thenThrow(new AccountNotFoundException("Cuenta no encontrada"));

            mockMvc.perform(get("/api/accounts/customer/100/transactions"))
                    .andExpect(status().isNotFound());
        }
    }
}