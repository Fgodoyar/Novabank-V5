package com.novabank.auth;

import com.novabank.auth.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private static final String SECRET = "patatamcfly1234567890123456789012";
    private static final long EXPIRATION = 86400000L;
    private static final String USERNAME = "admin";

    @BeforeEach
    void setUp() throws Exception {
        Field secretField = JwtService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtService, SECRET);

        Field expirationField = JwtService.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, EXPIRATION);
    }

    @Nested
    class GenerateTokenTest {

        @Test
        void generateToken_shouldReturnValidToken() {
            String token = jwtService.generateToken(USERNAME);
            assertNotNull(token);
            assertFalse(token.isEmpty());
        }

        @Test
        void generateToken_shouldContainUsername() {
            String token = jwtService.generateToken(USERNAME);
            assertEquals(USERNAME, jwtService.extractUsername(token));
        }
    }

    @Nested
    class ExtractUsernameTest {

        @Test
        void extractUsername_shouldReturnCorrectUsername() {
            String token = jwtService.generateToken(USERNAME);
            assertEquals(USERNAME, jwtService.extractUsername(token));
        }
    }

    @Nested
    class IsTokenValidTest {

        @Test
        void isTokenValid_validToken_shouldReturnTrue() {
            String token = jwtService.generateToken(USERNAME);
            assertTrue(jwtService.isTokenValid(token, USERNAME));
        }

        @Test
        void isTokenValid_wrongUsername_shouldReturnFalse() {
            String token = jwtService.generateToken(USERNAME);
            assertFalse(jwtService.isTokenValid(token, "otroUsuario"));
        }

        @Test
        void isTokenValid_expiredToken_shouldReturnFalse() throws Exception {
            Field expirationField = JwtService.class.getDeclaredField("expiration");
            expirationField.setAccessible(true);
            expirationField.set(jwtService, -1000L);

            String token = jwtService.generateToken(USERNAME);

            assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, USERNAME));
        }
    }

    @Nested
    class GetExpirationTest {

        @Test
        void getExpiration_shouldReturnCorrectValue() {
            assertEquals(EXPIRATION, jwtService.getExpiration());
        }
    }
}