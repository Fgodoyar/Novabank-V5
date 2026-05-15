package com.novabank.account;

import com.novabank.account.domain.Account;
import com.novabank.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@DataR2dbcTest
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        Account account = Account.builder()
                .accountNumber("ACC-001")
                .accountHolder("Pepillo Grillo")
                .balance(new BigDecimal("1000.00"))
                .customerId(1L)
                .build();

        accountRepository.deleteAll()
                .then(accountRepository.save(account))
                .block();
    }

    @Nested
    class FindByAccountNumberTest {

        @Test
        void shouldReturnAccountWhenNumberExists() {
            StepVerifier.create(accountRepository.findByAccountNumber("ACC-001"))
                    .expectNextMatches(a -> a.getAccountHolder().equals("Pepillo Grillo"))
                    .verifyComplete();
        }

        @Test
        void shouldReturnEmptyWhenNumberNotExists() {
            StepVerifier.create(accountRepository.findByAccountNumber("ACC-999"))
                    .verifyComplete();
        }
    }

    @Nested
    class FindByCustomerIdTest {

        @Test
        void shouldReturnAccountsForCustomer() {
            StepVerifier.create(accountRepository.findByCustomerId(1L))
                    .expectNextMatches(a -> a.getAccountNumber().equals("ACC-001"))
                    .verifyComplete();
        }

        @Test
        void shouldReturnEmptyListWhenCustomerHasNoAccounts() {
            StepVerifier.create(accountRepository.findByCustomerId(999L))
                    .verifyComplete();
        }
    }

    @Nested
    class ExistsByCustomerIdTest {

        @Test
        void shouldReturnTrueWhenCustomerHasAccount() {
            StepVerifier.create(accountRepository.existsByCustomerId(1L))
                    .expectNext(true)
                    .verifyComplete();
        }

        @Test
        void shouldReturnFalseWhenCustomerHasNoAccount() {
            StepVerifier.create(accountRepository.existsByCustomerId(999L))
                    .expectNext(false)
                    .verifyComplete();
        }
    }

    @Nested
    class FindByCustomerIdWithTransactionsTest {

        @Test
        void shouldReturnAccountsForCustomer() {
            StepVerifier.create(accountRepository.findByCustomerIdWithTransactions(1L))
                    .expectNextMatches(a -> a.getAccountNumber().equals("ACC-001"))
                    .verifyComplete();
        }

        @Test
        void shouldReturnEmptyWhenCustomerHasNoAccounts() {
            StepVerifier.create(accountRepository.findByCustomerIdWithTransactions(999L))
                    .verifyComplete();
        }
    }
}