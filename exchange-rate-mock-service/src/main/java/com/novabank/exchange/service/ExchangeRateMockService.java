package com.novabank.exchange.service;

import com.novabank.exchange.dto.ExchangeRateResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface ExchangeRateMockService {
    Mono<ExchangeRateResponse> getExchangeRate(String from, String to);
}