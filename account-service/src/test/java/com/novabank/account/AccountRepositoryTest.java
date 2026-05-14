package com.novabank.account;

import com.novabank.account.domain.Account;
import com.novabank.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .accountNumber("ACC-001")
                .accountHolder("Pepillo Grillo")
                .balance(new BigDecimal("1000.00"))
                .customerId(1L)
                .build();
        entityManager.persistAndFlush(account);
    }

    @Nested
    class FindByAccountNumberTest {

        @Test
        void shouldReturnAccountWhenNumberExists() {
            Optional<Account> result = accountRepository.findByAccountNumber("ACC-001");
            assertThat(result).isPresent();
            assertThat(result.get().getAccountHolder()).isEqualTo("Pepillo Grillo");
        }

        @Test
        void shouldReturnEmptyWhenNumberNotExists() {
            Optional<Account> result = accountRepository.findByAccountNumber("ACC-999");
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class FindByCustomerIdTest {

        @Test
        void shouldReturnAccountsForCustomer() {
            List<Account> result = accountRepository.findByCustomerId(1L);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAccountNumber()).isEqualTo("ACC-001");
        }

        @Test
        void shouldReturnEmptyListWhenCustomerHasNoAccounts() {
            List<Account> result = accountRepository.findByCustomerId(999L);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class ExistsByCustomerIdTest {

        @Test
        void shouldReturnTrueWhenCustomerHasAccount() {
            boolean exists = accountRepository.existsByCustomerId(1L);
            assertThat(exists).isTrue();
        }

        @Test
        void shouldReturnFalseWhenCustomerHasNoAccount() {
            boolean exists = accountRepository.existsByCustomerId(999L);
            assertThat(exists).isFalse();
        }
    }

    @Nested
    class FindByCustomerIdWithTransactionsTest {

        @Test
        void shouldReturnAccountsWithTransactions() {
            List<Account> result = accountRepository.findByCustomerIdWithTransactions(1L);
            assertThat(result).hasSize(1);
        }

        @Test
        void shouldReturnEmptyWhenCustomerHasNoAccounts() {
            List<Account> result = accountRepository.findByCustomerIdWithTransactions(999L);
            assertThat(result).isEmpty();
        }
    }
}