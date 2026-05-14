package com.novabank.account.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "account")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "transactions")
public class Transaction {
    @Id
    @Column("transaction_id")
    private Long transactionId;

    @Column("transaction_type")
    private String transactionType;

    @Column("amount")
    private BigDecimal amount;

    @Column("description")
    private String description;

    @Column("account_id")
    private Long accountId;

    @CreatedDate
    @Column("creation_date")
    private LocalDateTime creationDate;

}
