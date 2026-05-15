package com.novabank.account.domain;

import com.novabank.account.dto.TransactionDTO;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"transactions"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "accounts")
public class Account {
    @Id
    @Column("account_id")
    private Long accountId;

    @Column("account_number")
    private String accountNumber;

    @Column("account_holder")
    private String accountHolder;

    @Column("balance")
    private BigDecimal balance;

    @CreatedDate
    @Column("creation_date")
    private LocalDateTime creationDate;

    @Column("customer_id")
    private Long customerId;

    private List<TransactionDTO> transactions;

}
