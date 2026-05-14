package com.novabank.exchange.controller;

import com.novabank.exchange.dto.ExchangeRateResponse;
import com.novabank.exchange.service.ExchangeRateMockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/exchange-rate")
@RequiredArgsConstructor
public class ExchangeRateMockController {

    private final ExchangeRateMockService exchangeRateMockService;

    @GetMapping
    public Mono<ExchangeRateResponse> getExchangeRate(@RequestParam String from,
                                                      @RequestParam String to) {
        return exchangeRateMockService.getExchangeRate(from, to);
    }

}
