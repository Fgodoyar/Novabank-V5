package com.novabank.account.customer;

import com.novabank.account.dto.CustomerDTO;
import com.novabank.account.exception.CustomerNotFoundException;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CustomerServiceClient {

    private final WebClient webClient;

    public CustomerServiceClient(@LoadBalanced WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://CLIENTE-SERVICE")
                .build();
    }

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
}
