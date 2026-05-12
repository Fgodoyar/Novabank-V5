package com.novabank.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDTO {

    private Long customerId;

    @NotBlank(message = "El nombre del cliente es obligatorio.")
    @Schema(description = "Nombre sel cliente", example = "Carlos")
    private String customerName;

    @NotBlank(message = "Los apellidos del cliente son obligatorios.")
    @Schema(description = "Apellidos del cliente", example = "Godoy Sanchez")
    private String lastName;

    @NotBlank(message = "El Documento de identificación es obligatorio.")
    @Size(max = 9, message = "El documento de identificación debe tener 9 caracteres.")
    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "Documento no válido")
    @Schema(description = "DNI del cliente", example = "12345678A")
    private String dni;

    @NotBlank(message = "El email es obligatorio.")
    @Email
    @Schema(description = "Email del cliente", example = "juanfergo98@gmail.com")
    private String email;

    @NotBlank(message = "El número de teléfono es obligatorio.")
    @Pattern(regexp = "^[0-9]{9}$", message = "El formato del teléfono no es válido")
    @Schema(description = "Número de teléfono del cliente", example = "655432117")
    private String phoneNumber;

    @Schema(description = "Fecha de registro del cliente", example = "2026-04-18")
    private LocalDateTime creationDate;

}