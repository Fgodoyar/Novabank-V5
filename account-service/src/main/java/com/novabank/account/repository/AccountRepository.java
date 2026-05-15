package com.novabank.account.repository;

import com.novabank.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountId(Long accountId);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByCustomerId(Long customerId);
    boolean existsByCustomerId(Long customerId);

    // Carga cuentas con sus movimientos en una sola consulta (evita N+1)
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.transactions WHERE a.customerId = :customerId")
    List<Account> findByCustomerIdWithTransactions(@Param("customerId") Long customerId);
}
