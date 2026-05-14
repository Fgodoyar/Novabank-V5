package com.novabank.account.controller;


import com.novabank.account.dto.CreateTransactionRequest;
import com.novabank.account.dto.TransactionDTO;
import com.novabank.account.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Gestión de movimientos bancarios de NovaBank")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/{accountId}/transactions")
    public Mono<TransactionDTO> createTransaction(
            @PathVariable Long accountId,
            @RequestBody CreateTransactionRequest request) {
        return transactionService.createTransaction(accountId, request);
    }

    @GetMapping("/{accountId}/transactions")
    @Operation(summary = "Listar movimientos por cuenta",
            description = "Devuelve todos los movimientos asociadas a una cuenta.")
    @ApiResponse(responseCode = "200", description = "Lista de movimientos obtenida")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    public Flux<TransactionDTO> findByAccountId(@PathVariable("accountId") Long accountId) {
        return transactionService.findByAccountId(accountId);
    }


    @GetMapping("/{accountId}/transactions/{startDate}/{endDate}")
    @Operation(summary = "Buscar transacciones por cuenta y rango de fechas",
            description = "Devuelve las transacciones de una cuenta entre dos fechas, ordenadas de más reciente a más antigua.")
    @ApiResponse(responseCode = "200", description = "Transacciones obtenidas correctamente")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    public Flux<TransactionDTO> findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc
            (@PathVariable("accountId") Long accountId,
             @PathVariable("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
             @PathVariable("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        return transactionService.findByRangeBetweenDate(accountId, start, end);
    }
}
