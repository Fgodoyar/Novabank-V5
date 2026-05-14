package com.novabank.account.customer;

import com.novabank.account.dto.CustomerDTO;
import com.novabank.account.exception.CustomerNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomerServiceClient {

    private final WebClient webClient;

    public CustomerServiceClient(@LoadBalanced WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://CLIENTE-SERVICE")
                .build();
    }

    @CircuitBreaker(name = "customerService", fallbackMethod = "getCustomerFallback")
    public Mono<CustomerDTO> getCustomer(Long id) {
        return webClient.get()
                .uri("/api/customers/{id}", id)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new CustomerNotFoundException(id))
                )
                .bodyToMono(CustomerDTO.class);
    }

    public Mono<CustomerDTO> getCustomerFallback(Long id, Throwable ex) {
        log.error("Servicio de clientes caído para ID: {}. Causa: {}", id, ex.getMessage());
        return Mono.error(new CustomerNotFoundException(
                "Servicio de clientes no disponible, intente más tarde. ID: " + id));
    }
}
