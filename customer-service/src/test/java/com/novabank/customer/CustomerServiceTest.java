package com.novabank.customer;

import com.novabank.customer.domain.Customer;
import com.novabank.customer.dto.CreateCustomerRequest;
import com.novabank.customer.dto.CustomerDTO;
import com.novabank.customer.exception.CustomerNotFoundException;
import com.novabank.customer.exception.DuplicateDniException;
import com.novabank.customer.exception.DuplicateEmailException;
import com.novabank.customer.exception.DuplicatePhoneNumberException;
import com.novabank.customer.mapper.CustomerMapper;
import com.novabank.customer.repository.CustomerRepository;
import com.novabank.customer.service.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CreateCustomerRequest createCustomerRequest;
    private Long customerId;
    private String name;
    private String lastName;
    private String dni;
    private String email;
    private String phone;
    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customerId = 1L;
        name = "Pepillo";
        lastName = "Grillo";
        dni = "76543210A";
        email = "pepeergrillo@email.com";
        phone = "654789345";

        customer = Customer.builder()
                .customerId(customerId)
                .customerName(name)
                .lastName(lastName)
                .dni(dni)
                .email(email)
                .phoneNumber(phone)
                .creationDate(LocalDateTime.now())
                .build();

        customerDTO = CustomerDTO.builder()
                .customerId(customerId)
                .customerName(name)
                .lastName(lastName)
                .dni(dni)
                .email(email)
                .phoneNumber(phone)
                .build();

        createCustomerRequest = new CreateCustomerRequest(
                name, lastName, dni, email, phone
        );
    }

    @Nested
    class CreateCustomerTest {

        @Test
        void createCustomer_duplicateDni_shouldThrowException() {
            when(customerRepository.existsByDni(dni)).thenReturn(Mono.just(true));

            StepVerifier.create(customerService.createCustomer(createCustomerRequest))
                    .expectError(DuplicateDniException.class)
                    .verify();

            verify(customerRepository, never()).save(any());
        }

        @Test
        void createCustomer_duplicateEmail_shouldThrowException() {
            when(customerRepository.existsByDni(dni)).thenReturn(Mono.just(false));
            when(customerRepository.existsByEmail(email)).thenReturn(Mono.just(true));

            StepVerifier.create(customerService.createCustomer(createCustomerRequest))
                    .expectError(DuplicateEmailException.class)
                    .verify();

            verify(customerRepository, never()).save(any());
        }

        @Test
        void createCustomer_duplicatePhone_shouldThrowException() {
            when(customerRepository.existsByDni(dni)).thenReturn(Mono.just(false));
            when(customerRepository.existsByEmail(email)).thenReturn(Mono.just(false));
            when(customerRepository.existsByPhoneNumber(phone)).thenReturn(Mono.just(true));

            StepVerifier.create(customerService.createCustomer(createCustomerRequest))
                    .expectError(DuplicatePhoneNumberException.class)
                    .verify();

            verify(customerRepository, never()).save(any());
        }

        @Test
        void createCustomer_validData_shouldSaveSuccessfully() {
            when(customerRepository.existsByDni(dni)).thenReturn(Mono.just(false));
            when(customerRepository.existsByEmail(email)).thenReturn(Mono.just(false));
            when(customerRepository.existsByPhoneNumber(phone)).thenReturn(Mono.just(false));
            when(customerMapper.toEntity(createCustomerRequest)).thenReturn(customer);
            when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(customer));
            when(customerMapper.toDTO(any(Customer.class))).thenReturn(customerDTO);

            StepVerifier.create(customerService.createCustomer(createCustomerRequest))
                    .expectNext(customerDTO)
                    .verifyComplete();

            verify(customerRepository).save(any(Customer.class));
        }
    }

    @Nested
    class FindByIdTest {

        @Test
        void findById_existingCustomer_shouldReturnCustomer() {
            when(customerRepository.findById(customerId)).thenReturn(Mono.just(customer));
            when(customerMapper.toDTO(customer)).thenReturn(customerDTO);

            StepVerifier.create(customerService.findById(customerId))
                    .expectNext(customerDTO)
                    .verifyComplete();

            verify(customerRepository).findById(customerId);
        }

        @Test
        void findById_nonExistingCustomer_shouldThrowException() {
            when(customerRepository.findById(customerId)).thenReturn(Mono.empty());

            StepVerifier.create(customerService.findById(customerId))
                    .expectError(CustomerNotFoundException.class)
                    .verify();

            verify(customerRepository).findById(customerId);
        }
    }

    @Nested
    class FindByDniTest {

        @Test
        void findByDni_existingCustomer_shouldReturnCustomer() {
            when(customerRepository.findByDni(dni)).thenReturn(Mono.just(customer));
            when(customerMapper.toDTO(customer)).thenReturn(customerDTO);

            StepVerifier.create(customerService.findByDni(dni))
                    .expectNext(customerDTO)
                    .verifyComplete();

            verify(customerRepository).findByDni(dni);
        }

        @Test
        void findByDni_nonExistingCustomer_shouldThrowException() {
            when(customerRepository.findByDni(dni)).thenReturn(Mono.empty());

            StepVerifier.create(customerService.findByDni(dni))
                    .expectError(CustomerNotFoundException.class)
                    .verify();

            verify(customerRepository).findByDni(dni);
        }
    }

    @Nested
    class ListAllTest {

        @Test
        void listAll_shouldReturnCustomerList() {
            Customer customer2 = Customer.builder()
                    .customerId(2L)
                    .customerName("Pepilla")
                    .lastName("Pili")
                    .dni("77654321L")
                    .email("pepilla54@gmail.com")
                    .phoneNumber("654789345")
                    .creationDate(LocalDateTime.now())
                    .build();

            when(customerRepository.findAll()).thenReturn(Flux.just(customer, customer2));
            when(customerMapper.toDTO(any())).thenReturn(customerDTO);

            StepVerifier.create(customerService.listCustomers())
                    .expectNextCount(2)
                    .verifyComplete();

            verify(customerRepository).findAll();
        }
    }
}