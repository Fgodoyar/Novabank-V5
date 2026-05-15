package com.novabank.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("customer-service", r -> r
                        .path("/api/customers/**")
                        .uri("lb://CUSTOMER-SERVICE"))
                .route("account-service", r -> r
                        .path("/api/accounts/**")
                        .uri("lb://ACCOUNT-SERVICE"))
                .route("operation-service", r -> r
                        .path("/api/operations/**")
                        .uri("lb://OPERATION-SERVICE"))
                .route("auth-server", r -> r
                        .path("/api/auth/**")
                        .uri("lb://AUTH-SERVER"))
                .route("exchange-rate-mock-service", r -> r
                        .path("/api/exchange-rate/**")
                        .uri("lb://EXCHANGE-RATE-SERVICE"))
                .build();
    }
}