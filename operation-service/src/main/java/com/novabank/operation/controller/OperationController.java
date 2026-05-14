package com.novabank.operation.controller;

import com.novabank.operation.dto.CreateOperationRequest;
import com.novabank.operation.dto.CreateTransferRequest;
import com.novabank.operation.dto.TransactionDTO;
import com.novabank.operation.service.OperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/operations")
public class OperationController {

    private final OperationService operationService;

    @PostMapping("/deposit")
    @Operation(summary = "Depositar dinero")
    @ApiResponse(responseCode = "200", description = "Depósito realizado")
    @ApiResponse(responseCode = "400", description = "Monto inválido")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    public Mono<TransactionDTO> deposit(@Valid @RequestBody CreateOperationRequest request) {
        return operationService.deposit(request);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Retirar dinero")
    @ApiResponse(responseCode = "200", description = "Retiro realizado")
    @ApiResponse(responseCode = "400", description = "Monto inválido")
    @ApiResponse(responseCode = "422", description = "Saldo insuficiente")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    public Mono<TransactionDTO> withdrawal(@Valid @RequestBody CreateOperationRequest request) {
        return operationService.withdraw(request);
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transferir dinero")
    @ApiResponse(responseCode = "200", description = "Transferencia realizada")
    @ApiResponse(responseCode = "400", description = "Monto inválido o cuentas iguales")
    @ApiResponse(responseCode = "422", description = "Saldo insuficiente")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    public Flux<TransactionDTO> transfer(@Valid @RequestBody CreateTransferRequest request) {
        return operationService.transfer(request);
    }
}