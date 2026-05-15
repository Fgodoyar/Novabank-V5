package com.novabank.auth;

import com.novabank.auth.config.SecurityConfig;
import com.novabank.auth.controller.AuthController;
import com.novabank.auth.service.JwtService;
import com.novabank.auth.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    private static final String VALID_LOGIN_JSON = """
            {
              "username": "pepillo",
              "password": "password123"
            }
            """;

    private static final String VALID_REGISTER_JSON = """
            {
              "username": "pepillo",
              "password": "password123"
            }
            """;

    @Nested
    class LoginTest {

        @Test
        @DisplayName("POST /api/auth/login → 200 con token generado")
        void login_shouldReturn200WhenValid() {
            when(userService.login("pepillo", "password123"))
                    .thenReturn(Mono.just("token.jwt.fake"));
            when(jwtService.getExpiration()).thenReturn(86400000L);

            webTestClient.post().uri("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_LOGIN_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.token").isEqualTo("token.jwt.fake")
                    .jsonPath("$.tipo").isEqualTo("Bearer")
                    .jsonPath("$.expiration").isEqualTo(86400000L);
        }

        @Test
        @DisplayName("POST /api/auth/login → 401 con credenciales incorrectas")
        void login_shouldReturn401WhenInvalidCredentials() {
            when(userService.login(any(), any()))
                    .thenReturn(Mono.error(new BadCredentialsException("Credenciales incorrectas")));

            webTestClient.post().uri("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_LOGIN_JSON)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("POST /api/auth/login → 400 si el body está vacío")
        void login_shouldReturn400WhenInvalid() {
            webTestClient.post().uri("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("{}")
                    .exchange()
                    .expectStatus().isBadRequest();

            verifyNoInteractions(userService);
        }
    }

    @Nested
    class RegisterTest {

        @Test
        @DisplayName("POST /api/auth/register → 201 usuario registrado")
        void register_shouldReturn201WhenValid() {
            when(userService.register(any(), any())).thenReturn(Mono.empty());

            webTestClient.post().uri("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_REGISTER_JSON)
                    .exchange()
                    .expectStatus().isCreated();

            verify(userService).register("pepillo", "password123");
        }

        @Test
        @DisplayName("POST /api/auth/register → 400 si el usuario ya existe")
        void register_shouldReturn400WhenDuplicate() {
            when(userService.register(any(), any()))
                    .thenReturn(Mono.error(new IllegalArgumentException("El usuario ya existe: pepillo")));

            webTestClient.post().uri("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_REGISTER_JSON)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }
}