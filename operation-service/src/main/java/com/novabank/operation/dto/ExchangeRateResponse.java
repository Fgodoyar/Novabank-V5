package com.novabank.operation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponse {
    private String from;
    private String to;
    private BigDecimal rate;
    private Instant date;
}