package com.novabank.exchange.service;

import com.novabank.exchange.dto.ExchangeRateResponse;
import com.novabank.exchange.exception.UnsupportedCurrencyException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class ExchangeRateMockServiceImpl implements ExchangeRateMockService {

    private static final Map<String, BigDecimal> RATES = Map.of(
            "USD", new BigDecimal("0.92"),
            "GBP", new BigDecimal("1.17"),
            "JPY", new BigDecimal("0.0061"),
            "CHF", new BigDecimal("1.04")
    );

    @Override
    public Mono<ExchangeRateResponse> getExchangeRate(String from, String to) {
        String key = from.toUpperCase();

        if (!RATES.containsKey(key)) {
            return Mono.error(new UnsupportedCurrencyException(from));
        }

        return Mono.just(new ExchangeRateResponse(
                from.toUpperCase(),
                to.toUpperCase(),
                RATES.get(key),
                LocalDateTime.now()
        ));
    }
}
