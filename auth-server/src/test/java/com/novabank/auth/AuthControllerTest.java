package com.novabank.auth;

import com.novabank.auth.config.SecurityConfig;
import com.novabank.auth.controller.AuthController;
import com.novabank.auth.service.JwtService;
import com.novabank.auth.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
        void login_shouldReturn200WhenValid() throws Exception {
            when(userService.login("pepillo", "password123"))
                    .thenReturn("token.jwt.fake");
            when(jwtService.getExpiration()).thenReturn(86400000L);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_LOGIN_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("token.jwt.fake"))
                    .andExpect(jsonPath("$.tipo").value("Bearer"))
                    .andExpect(jsonPath("$.expiration").value(86400000L));
        }

        @Test
        @DisplayName("POST /api/auth/login → 401 con credenciales incorrectas")
        void login_shouldReturn401WhenInvalidCredentials() throws Exception {
            when(userService.login(any(), any()))
                    .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_LOGIN_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/auth/login → 400 si el body está vacío")
        void login_shouldReturn400WhenInvalid() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(userService);
        }
    }

    @Nested
    class RegisterTest {

        @Test
        @DisplayName("POST /api/auth/register → 201 usuario registrado")
        void register_shouldReturn201WhenValid() throws Exception {
            doNothing().when(userService).register(any(), any());

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_REGISTER_JSON))
                    .andExpect(status().isCreated());

            verify(userService).register("pepillo", "password123");
        }

        @Test
        @DisplayName("POST /api/auth/register → 400 si el usuario ya existe")
        void register_shouldReturn400WhenDuplicate() throws Exception {
            doThrow(new IllegalArgumentException("El usuario ya existe: pepillo"))
                    .when(userService).register(any(), any());

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_REGISTER_JSON))
                    .andExpect(status().isBadRequest());
        }
    }
}