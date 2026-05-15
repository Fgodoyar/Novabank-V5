package com.novabank.exchange;

import com.novabank.exchange.controller.ExchangeRateMockController;
import com.novabank.exchange.dto.ExchangeRateResponse;
import com.novabank.exchange.exception.UnsupportedCurrencyException;
import com.novabank.exchange.service.ExchangeRateMockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebFluxTest(
        controllers = ExchangeRateMockController.class,
        excludeAutoConfiguration = ReactiveSecurityAutoConfiguration.class)
public class ExchangeRateNockControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ExchangeRateMockService exchangeRateMockService;

    @Test
    void getRate_whenCurrencyExists_shouldReturn200(){
        when(exchangeRateMockService.getExchangeRate("USD", "EUR"))
                .thenReturn(Mono.just(new ExchangeRateResponse("USD", "EUR", new BigDecimal("0.92"), Instant.now())));

        webTestClient.get()
                .uri("/api/exchange-rate?from=USD&to=EUR")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExchangeRateResponse.class)
                .value(exchangeRate -> {
                    assertThat(exchangeRate.getFrom()).isEqualTo("USD");
                    assertThat(exchangeRate.getTo()).isEqualTo("EUR");
                    assertThat(exchangeRate.getRate()).isEqualByComparingTo("0.92");
                    assertThat(exchangeRate.getDate() != null);
                });
    }

    @Test
    void getRate_whenCurrencyNotSupported_shouldReturn400() {
        when(exchangeRateMockService.getExchangeRate("ABC", "EUR"))
                .thenReturn(Mono.error(new UnsupportedCurrencyException("ABC")));

        webTestClient.get()
                .uri("/api/exchange-rate?from=ABC&to=EUR")
                .exchange()
                .expectStatus().isBadRequest();
    }
}
