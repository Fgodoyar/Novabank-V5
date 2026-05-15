package com.novabank.account;

import com.novabank.account.customer.CustomerServiceClient;
import com.novabank.account.domain.Account;
import com.novabank.account.dto.AccountDTO;
import com.novabank.account.dto.CreateAccountRequest;
import com.novabank.account.dto.CustomerDTO;
import com.novabank.account.exception.AccountNotFoundException;
import com.novabank.account.exception.CustomerNotFoundException;
import com.novabank.account.mapper.AccountMapper;
import com.novabank.account.mapper.TransactionMapper;
import com.novabank.account.repository.AccountRepository;
import com.novabank.account.repository.TransactionRepository;
import com.novabank.account.service.AccountServiceImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerServiceClient customerServiceClient;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Long customerId;
    private String accountNumber;
    private CustomerDTO customerDTO;
    private Account account;
    private AccountDTO accountDTO;
    private CreateAccountRequest createAccountRequest;

    @BeforeEach
    void setUp() {
        customerId = 1L;
        accountNumber = "ES9121000000000000000002";

        customerDTO = new CustomerDTO(
                customerId,
                "Pepillo",
                "Grillo",
                "76543210A",
                "pepeergrillo@email.com",
                "654789345"
        );

        account = Account.builder()
                .accountId(1L)
                .accountNumber(accountNumber)
                .accountHolder("Pepillo Grillo")
                .balance(BigDecimal.ZERO)
                .customerId(customerId)
                .build();

        accountDTO = AccountDTO.builder()
                .accountId(1L)
                .accountNumber(accountNumber)
                .accountHolder("Pepillo Grillo")
                .balance(BigDecimal.ZERO)
                .customerId(customerId)
                .build();

        createAccountRequest = new CreateAccountRequest(customerId);
    }

    @Nested
    class CreateAccountTest {

        @Test
        void createAccount_customerNotFound_shouldThrowException() {
            when(customerServiceClient.getCustomer(99L))
                    .thenReturn(Mono.error(new CustomerNotFoundException("Cliente no encontrado con ID: 99")));

            StepVerifier.create(accountService.createAccount(new CreateAccountRequest(99L)))
                    .expectError(CustomerNotFoundException.class)
                    .verify();

            verify(accountRepository, never()).save(any());
        }

        @Test
        void createAccount_customerAlreadyHasAccount_shouldThrowException() {
            when(customerServiceClient.getCustomer(customerId)).thenReturn(Mono.just(customerDTO));
            when(accountRepository.existsByCustomerId(customerId)).thenReturn(Mono.just(true));

            StepVerifier.create(accountService.createAccount(createAccountRequest))
                    .expectError(IllegalArgumentException.class)
                    .verify();

            verify(accountRepository, never()).save(any());
        }

        @Test
        void createAccount_validData_shouldSaveSuccessfully() {
            when(customerServiceClient.getCustomer(anyLong())).thenReturn(Mono.just(customerDTO));
            when(accountRepository.existsByCustomerId(anyLong())).thenReturn(Mono.just(false));
            when(accountRepository.save(any())).thenReturn(Mono.just(account));
            when(accountMapper.toDTO(any())).thenReturn(accountDTO);

            StepVerifier.create(accountService.createAccount(createAccountRequest))
                    .expectNext(accountDTO)
                    .verifyComplete();

            verify(accountRepository).save(any());
        }
    }

    @Nested
    class FindByCustomerIdTest {

        @Test
        void findByCustomerId_shouldReturnList() {
            when(accountRepository.findByCustomerId(customerId)).thenReturn(Flux.just(account));
            when(accountMapper.toDTO(account)).thenReturn(accountDTO);

            StepVerifier.create(accountService.findByCustomerId(customerId))
                    .expectNext(accountDTO)
                    .verifyComplete();

            verify(accountRepository).findByCustomerId(customerId);
        }

        @Test
        void findByCustomerId_shouldReturnEmptyList() {
            when(accountRepository.findByCustomerId(customerId)).thenReturn(Flux.empty());

            StepVerifier.create(accountService.findByCustomerId(customerId))
                    .verifyComplete();
        }
    }

    @Nested
    class FindByAccountNumberTest {

        @Test
        void findByAccountNumber_existingAccount_shouldReturnAccount() {
            when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Mono.just(account));
            when(accountMapper.toDTO(account)).thenReturn(accountDTO);

            StepVerifier.create(accountService.findByAccountNumber(accountNumber))
                    .expectNext(accountDTO)
                    .verifyComplete();

            verify(accountRepository).findByAccountNumber(accountNumber);
        }

        @Test
        void findByAccountNumber_nonExistingAccount_shouldThrowException() {
            when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Mono.empty());

            StepVerifier.create(accountService.findByAccountNumber(accountNumber))
                    .expectError(AccountNotFoundException.class)
                    .verify();
        }
    }

    @Nested
    class FindByCustomerIdWithTransactionsTest {

        @Test
        void findByCustomerIdWithTransactions_shouldReturnList() {
            System.out.println("accountId: " + account.getAccountId()); // ← añade esto
            when(accountRepository.findByCustomerId(customerId)).thenReturn(Flux.just(account));
            when(transactionRepository.findByAccountId(any(Long.class))).thenReturn(Flux.empty());
            StepVerifier.create(accountService.findByCustomerIdWithTransactions(customerId))
                    .expectNextMatches(dto ->
                            dto.getAccountNumber().equals(accountNumber) &&
                                    dto.getTransactions().isEmpty()
                    )
                    .verifyComplete();
        }

        @Test
        void findByCustomerIdWithTransactions_shouldReturnEmptyList() {
            when(accountRepository.findByCustomerId(customerId)).thenReturn(Flux.empty());

            StepVerifier.create(accountService.findByCustomerIdWithTransactions(customerId))
                    .verifyComplete();
        }
    }
}