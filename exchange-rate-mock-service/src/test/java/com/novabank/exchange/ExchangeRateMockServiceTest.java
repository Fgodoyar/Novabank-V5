package com.novabank.exchange;

import com.novabank.exchange.exception.UnsupportedCurrencyException;
import com.novabank.exchange.service.ExchangeRateMockService;

import com.novabank.exchange.service.ExchangeRateMockServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateMockServiceTest {

    private final ExchangeRateMockService exchangeRateMockService = new ExchangeRateMockServiceImpl();

    @Test
    void getRate_whenCurrencyExists_shouldReturnRate(){
        StepVerifier.create(exchangeRateMockService.getExchangeRate("USD", "EUR"))
                .expectNextMatches(exchangeRateResponse ->
                        exchangeRateResponse.getFrom().equals("USD") &&
                                exchangeRateResponse.getTo().equals("EUR") &&
                        exchangeRateResponse.getRate().compareTo(new BigDecimal("0.92")) == 0 &&
                        exchangeRateResponse.getDate() != null)
                .verifyComplete();
    }

    @Test
    void getRate_whenCurrencyNotSupported_shouldEmitError() {
        StepVerifier.create(exchangeRateMockService.getExchangeRate("ABC", "EUR"))
                .expectError(UnsupportedCurrencyException.class)
                .verify();
    }
}
