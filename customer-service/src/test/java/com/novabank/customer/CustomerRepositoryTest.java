package com.novabank.customer;

import com.novabank.customer.config.R2dbcConfig;
import com.novabank.customer.domain.Customer;
import com.novabank.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;


@DataR2dbcTest
@Import(R2dbcConfig.class)
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @Autowired
    DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        databaseClient.sql("DELETE FROM customers")
                .fetch()
                .rowsUpdated()
                .block();

        customer = Customer.builder()
                .customerName("Pepillo")
                .lastName("Grillo")
                .dni("76543210A")
                .email("pepeergrillo@email.com")
                .phoneNumber("654789345")
                .build();

        customerRepository.save(customer).block();
    }

    @Nested
    class FindByDniTest {

        @Test
        void findByDni_existingDni_shouldReturnCustomer() {
            StepVerifier.create(customerRepository.findByDni("76543210A"))
                    .assertNext(c -> {
                        assertEquals("76543210A", c.getDni());
                        assertEquals("pepeergrillo@email.com", c.getEmail());
                    })
                    .verifyComplete();
        }

        @Test
        void findByDni_nonExistingDni_shouldReturnEmpty() {
            Mono<Customer> customerMono = customerRepository.findByDni("00000000X");

            StepVerifier.create(customerMono)
                    .verifyComplete();
        }
    }

    @Nested
    class ExistsByDniTest {

        @Test
        void existsByDni_existingDni_shouldReturnTrue() {
            StepVerifier.create(customerRepository.existsByDni("76543210A"))
                    .expectNext(true)
                    .verifyComplete();
        }

        @Test
        void existsByDni_nonExistingDni_shouldReturnFalse() {
            StepVerifier.create(customerRepository.existsByDni("00000000X"))
                    .expectNext(false)
                    .verifyComplete();
        }
    }

    @Nested
    class ExistsByEmailTest {

        @Test
        void existsByEmail_existingEmail_shouldReturnTrue() {
            StepVerifier.create(customerRepository.existsByEmail("pepeergrillo@email.com"))
                    .expectNext(true)
                    .verifyComplete();
        }

        @Test
        void existsByEmail_nonExistingEmail_shouldReturnFalse() {
            StepVerifier.create(customerRepository.existsByEmail("noexiste@email.com"))
                    .expectNext(false)
                    .verifyComplete();
        }
    }

    @Nested
    class ExistsByPhoneNumberTest {

        @Test
        void existsByPhoneNumber_existingPhone_shouldReturnTrue() {
            StepVerifier.create(customerRepository.existsByPhoneNumber("654789345"))
                    .expectNext(true)
                    .verifyComplete();
        }

        @Test
        void existsByPhoneNumber_nonExistingPhone_shouldReturnFalse() {
            StepVerifier.create(customerRepository.existsByPhoneNumber("000000000"))
                    .expectNext(false)
                    .verifyComplete();
        }
    }

    @Nested
    class SaveTest {

        @Test
        void save_shouldPersistCustomer() {
            Customer newCustomer = Customer.builder()
                    .customerName("Pepilla")
                    .lastName("Grilla")
                    .dni("12345678B")
                    .email("pepilla@email.com")
                    .phoneNumber("600000001")
                    .build();

            Mono<Customer> saved = customerRepository.save(newCustomer);

            StepVerifier.create(saved)
                    .expectNextMatches(customer ->
                            customer.getDni().equals("12345678B") &&
                                    customer.getEmail().equals("pepilla@email.com"))
                    .verifyComplete();
        }

        @Test
        void save_shouldSetCreationDateAutomatically() {

            Customer newCustomer = Customer.builder()
                    .customerName("Pepilla")
                    .lastName("Grilla")
                    .dni("12345678B")
                    .email("pepilla@email.com")
                    .phoneNumber("600000001")
                    .build();

            StepVerifier.create(customerRepository.save(newCustomer))
                    .assertNext(savedCustomer -> {
                        assertNotNull(savedCustomer.getCustomerId());
                        assertNotNull(savedCustomer.getCreationDate());
                    })
                    .verifyComplete();
        }
    }
}