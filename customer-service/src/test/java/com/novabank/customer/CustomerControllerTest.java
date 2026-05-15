package com.novabank.customer;

import com.novabank.customer.config.SecurityConfig;
import com.novabank.customer.controller.CustomerController;
import com.novabank.customer.dto.CreateCustomerRequest;
import com.novabank.customer.dto.CustomerDTO;
import com.novabank.customer.exception.CustomerNotFoundException;
import com.novabank.customer.exception.DuplicateDniException;
import com.novabank.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(CustomerController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "spring.config.import=")
public class CustomerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CustomerService customerService;

    private CustomerDTO customerDTO;

    private static final String VALID_JSON = """
            {
              "customerName": "Juan Bartolomeo García",
              "lastName": "García",
              "dni": "12345678A",
              "email": "juanbartolitogarcia@email.com",
              "phoneNumber": "600123456"
            }
            """;

    @BeforeEach
    void setUp() {
        customerDTO = CustomerDTO.builder()
                .customerId(1L)
                .customerName("Juan Bartolomeo")
                .lastName("García")
                .dni("12345678A")
                .email("juanbartolitogarcia@email.com")
                .phoneNumber("600123456")
                .creationDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .build();
    }

    @Nested
    class CreateCustomerTest {

        @Test
        @WithMockUser
        @DisplayName("POST /api/customers → 201 con cliente creado")
        void createCustomer_shouldReturn201WhenValid() {
            when(customerService.createCustomer(any(CreateCustomerRequest.class)))
                    .thenReturn(Mono.just(customerDTO));

            webTestClient.post().uri("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_JSON)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.customerId").isEqualTo(1L)
                    .jsonPath("$.dni").isEqualTo("12345678A");

            verify(customerService).createCustomer(any(CreateCustomerRequest.class));
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/customers → 400 si el body no supera la validación @Valid")
        void createCustomer_shouldReturn400WhenInvalid() {
            webTestClient.post().uri("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("{}")
                    .exchange()
                    .expectStatus().isBadRequest();

            verifyNoInteractions(customerService);
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/customers → 400 si DNI/email/teléfono ya están registrados")
        void createCustomer_shouldReturn400WhenDuplicate() {
            when(customerService.createCustomer(any(CreateCustomerRequest.class)))
                    .thenReturn(Mono.error(new DuplicateDniException("DNI ya registrado")));

            webTestClient.post().uri("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_JSON)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("POST /api/customers → 401 sin autenticación")
        void createCustomer_shouldReturn401WhenUnauthenticated() {
            webTestClient.post().uri("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_JSON)
                    .exchange()
                    .expectStatus().isUnauthorized();

            verifyNoInteractions(customerService);
        }
    }

    @Nested
    class ListCustomersTest {

        @Test
        @WithMockUser
        @DisplayName("GET /api/customers → 200 con lista de clientes")
        void listCustomers_shouldReturn200WithList() {
            when(customerService.listCustomers()).thenReturn(Flux.just(customerDTO));

            webTestClient.get().uri("/api/customers")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(CustomerDTO.class)
                    .hasSize(1);

            verify(customerService, times(1)).listCustomers();
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/customers → 200 con lista vacía")
        void listCustomers_shouldReturn200WithEmptyList() {
            when(customerService.listCustomers()).thenReturn(Flux.empty());

            webTestClient.get().uri("/api/customers")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(CustomerDTO.class)
                    .hasSize(0);
        }
    }

    @Nested
    class GetCustomerByIdTest {

        @Test
        @WithMockUser
        @DisplayName("GET /api/customers/{id} → 200 cuando el cliente existe")
        void findById_shouldReturn200WhenFound() {
            when(customerService.findById(1L)).thenReturn(Mono.just(customerDTO));

            webTestClient.get().uri("/api/customers/1")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.customerId").isEqualTo(1L)
                    .jsonPath("$.customerName").isEqualTo("Juan Bartolomeo");

            verify(customerService).findById(1L);
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/customers/{id} → 404 cuando el cliente no existe")
        void findById_shouldReturn404WhenNotFound() {
            when(customerService.findById(99L))
                    .thenReturn(Mono.error(new CustomerNotFoundException("Cliente no encontrado")));

            webTestClient.get().uri("/api/customers/99")
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class GetCustomerByDniTest {

        @Test
        @WithMockUser
        @DisplayName("GET /api/customers/dni/{dni} → 200 cuando el cliente existe")
        void findByDni_shouldReturn200WhenFound() {
            when(customerService.findByDni("12345678A")).thenReturn(Mono.just(customerDTO));

            webTestClient.get().uri("/api/customers/dni/12345678A")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.dni").isEqualTo("12345678A");

            verify(customerService).findByDni("12345678A");
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/customers/dni/{dni} → 404 cuando no existe")
        void findByDni_shouldReturn404WhenNotFound() {
            when(customerService.findByDni("00000000X"))
                    .thenReturn(Mono.error(new CustomerNotFoundException("Cliente no encontrado")));

            webTestClient.get().uri("/api/customers/dni/00000000X")
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }
}