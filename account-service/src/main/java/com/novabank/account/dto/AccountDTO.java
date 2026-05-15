package com.novabank.account.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDTO {

    private Long accountId;

    @Size(min = 10, max = 34, message = "El número de cuenta debe tener entre 10 y 34 caracteres.")
    @Schema(description = "Número de la cuenta", example = "ES9121000000000000000002")
    private String accountNumber;

    @Schema(description = "Nombre del titular de la cuenta", example = "Carlos")
    private String accountHolder;

    @PositiveOrZero(message = "El sueldo no puede ser negativo.")
    @Schema(description = "Saldo disponible de la cuenta")
    private BigDecimal balance;

    @Schema(description = "Fecha de creación de la cuenta", example = "2026-05-18")
    private LocalDateTime creationDate;

    @Schema(description = "ID del cliente vinculado a la cuenta")
    private Long customerId;

    private List<TransactionDTO> transactions;

    public AccountDTO(String accountNumber, String accountHolder, BigDecimal balance, LocalDateTime creationDate, Long customerId) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.creationDate = creationDate;
        this.customerId = customerId;
    }
}
