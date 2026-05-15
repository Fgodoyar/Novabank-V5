package com.novabank.customer.controller;

import com.novabank.customer.dto.CreateCustomerRequest;
import com.novabank.customer.dto.CustomerDTO;
import com.novabank.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Gestión de clientes de NovaBank")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Devuelve todos los clientes registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public Flux<CustomerDTO> listCustomers() {
        return customerService.listCustomers();

    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public Mono<CustomerDTO> findById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Crear cliente",
            description = "Registra un nuevo cliente. DNI, email y teléfono deben ser únicos.")
    @ApiResponse(responseCode = "201", description = "Cliente creado correctamente")
    @ApiResponse(responseCode = "400", description = "DNI, email o teléfono ya registrado")
    public Mono<CustomerDTO> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return customerService.createCustomer(request);
    }

    @GetMapping("/dni/{dni}")
    @Operation(summary = "Buscar cliente por DNI")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public Mono<CustomerDTO> findByDni(@PathVariable String dni) {
        return customerService.findByDni(dni);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar cliente por email")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public Mono<CustomerDTO> findByEmail(@PathVariable String email) {
        return customerService.findByEmail(email);
    }
}