package com.novabank.customer.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "customers")
public class Customer {
    @Id
    @Column("customer_id")
    private Long customerId;

    @Column("customer_name")
    private String customerName;

    @Column("last_name")
    private String lastName;

    @Column("dni")
    private String dni;

    @Column("email")
    private String email;

    @Column("phone_number")
    private String phoneNumber;

    @CreatedDate
    @Column("creation_date")
    private LocalDateTime creationDate;

}
