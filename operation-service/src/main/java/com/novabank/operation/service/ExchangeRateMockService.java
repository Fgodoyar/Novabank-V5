package com.novabank.operation.service;

import com.novabank.operation.dto.ExchangeRateResponse;
import com.novabank.operation.exception.ExchangeRateUnavailableException;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;

@Service
public class ExchangeRateMockService {

    private final WebClient webClient;

    public ExchangeRateMockService(@LoadBalanced WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://EXCHANGE-RATE-SERVICE")
                .build();
    }

    public Mono<BigDecimal> getRate(String from, String to) {
        if (from.equalsIgnoreCase(to)) {
            return Mono.just(BigDecimal.ONE);
        }

        return webClient.get()
                .uri(uri -> uri.path("/api/exchange-rate")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .map(ExchangeRateResponse::getRate)
                .timeout(Duration.ofSeconds(3))
                .onErrorMap(ex -> new ExchangeRateUnavailableException(from, to, ex));
    }
}
