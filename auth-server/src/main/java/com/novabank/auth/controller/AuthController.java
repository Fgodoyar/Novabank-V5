package com.novabank.auth.controller;

import com.novabank.auth.dto.LoginRequestDTO;
import com.novabank.auth.dto.LoginResponseDTO;
import com.novabank.auth.dto.RegisterRequest;
import com.novabank.auth.service.JwtService;
import com.novabank.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto"),
            @ApiResponse(responseCode = "401", description = "Usuario o contraseña incorrectos")
    })
    public Mono<ResponseEntity<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO request) {
        return userService.login(request.getUsername(), request.getPassword())
                .map(token -> ResponseEntity.ok(
                        LoginResponseDTO.builder()
                                .token(token)
                                .tipo("Bearer")
                                .expiration(jwtService.getExpiration())
                                .build()
                ));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El nombre de usuario ya está en uso")
    })
    public Mono<Void> register(@RequestBody RegisterRequest request) {
        return userService.register(request.username(), request.password());
    }

    @GetMapping("/validate")
    @Operation(summary = "Validar token JWT",
            description = "Verifica si un token JWT es válido y no ha expirado.")
    @ApiResponse(responseCode = "200", description = "Token válido")
    @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    public Mono<ResponseEntity<Boolean>> validate(@RequestParam String token) {
        return jwtService.extractUsernameMono(token)
                .flatMap(username -> jwtService.isTokenValidMono(token, username))
                .map(valid -> valid
                        ? ResponseEntity.ok(true)
                        : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false)
                );
    }
}