package com.novabank.customer.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "accounts")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "Customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "last_name")
    private String lastName;

    @Column
    private String dni;

    @Column
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    /*@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    public Set<Account> accounts;*/

    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
    }

}