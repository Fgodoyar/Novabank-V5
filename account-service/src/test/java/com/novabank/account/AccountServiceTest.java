package com.novabank.account;

import com.novabank.account.customer.CustomerServiceClient;
import com.novabank.account.domain.Account;
import com.novabank.account.dto.AccountDTO;
import com.novabank.account.dto.CreateAccountRequest;
import com.novabank.account.dto.CustomerDTO;
import com.novabank.account.exception.AccountNotFoundException;
import com.novabank.account.exception.CustomerNotFoundException;
import com.novabank.account.mapper.AccountMapper;
import com.novabank.account.repository.AccountRepository;
import com.novabank.account.service.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerServiceClient customerServiceCustomer;

    @Mock
    private AccountMapper accountMapper;

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
            when(customerServiceCustomer.getCustomer(99L)).thenReturn(null);

            assertThrows(CustomerNotFoundException.class, () ->
                    accountService.createAccount(new CreateAccountRequest(99L))
            );
            verify(accountRepository, never()).save(any());
        }

        @Test
        void createAccount_customerAlreadyHasAccount_shouldThrowException() {
            when(customerServiceCustomer.getCustomer(customerId)).thenReturn(customerDTO);
            when(accountRepository.existsByCustomerId(customerId)).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () ->
                    accountService.createAccount(createAccountRequest)
            );
            verify(accountRepository, never()).save(any());
        }

        @Test
        void createAccount_validData_shouldSaveSuccessfully() {
            when(customerServiceCustomer.getCustomer(customerId)).thenReturn(customerDTO);
            when(accountRepository.existsByCustomerId(customerId)).thenReturn(false);
            when(accountRepository.saveAndFlush(any())).thenReturn(account);
            when(accountMapper.toDTO(account)).thenReturn(accountDTO);

            AccountDTO result = accountService.createAccount(createAccountRequest);

            assertEquals(accountDTO, result);
            verify(accountRepository).saveAndFlush(any());
        }
    }

    @Nested
    class FindByCustomerIdTest {

        @Test
        void findByCustomerId_shouldReturnList() {
            when(accountRepository.findByCustomerId(customerId)).thenReturn(List.of(account));
            when(accountMapper.toDTO(account)).thenReturn(accountDTO);

            List<AccountDTO> result = accountService.findByCustomerId(customerId);

            assertEquals(1, result.size());
            verify(accountRepository).findByCustomerId(customerId);
        }

        @Test
        void findByCustomerId_shouldReturnEmptyList() {
            when(accountRepository.findByCustomerId(customerId)).thenReturn(List.of());

            List<AccountDTO> result = accountService.findByCustomerId(customerId);

            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindByAccountNumberTest {

        @Test
        void findByAccountNumber_existingAccount_shouldReturnAccount() {
            when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
            when(accountMapper.toDTO(account)).thenReturn(accountDTO);

            AccountDTO result = accountService.findByAccountNumber(accountNumber);

            assertEquals(accountDTO, result);
            verify(accountRepository).findByAccountNumber(accountNumber);
        }

        @Test
        void findByAccountNumber_nonExistingAccount_shouldThrowException() {
            when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

            assertThrows(AccountNotFoundException.class, () ->
                    accountService.findByAccountNumber(accountNumber)
            );
        }
    }

    @Nested
    class FindByCustomerIdWithTransactionsTest {

        @Test
        void findByCustomerIdWithTransactions_shouldReturnList() {
            when(accountRepository.findByCustomerIdWithTransactions(customerId)).thenReturn(List.of(account));
            when(accountMapper.toDTO(account)).thenReturn(accountDTO);

            List<AccountDTO> result = accountService.findByCustomerIdWithTransactions(customerId);

            assertEquals(1, result.size());
        }

        @Test
        void findByCustomerIdWithTransactions_shouldReturnEmptyList() {
            when(accountRepository.findByCustomerIdWithTransactions(customerId)).thenReturn(List.of());

            List<AccountDTO> result = accountService.findByCustomerIdWithTransactions(customerId);

            assertEquals(0, result.size());
        }
    }
}