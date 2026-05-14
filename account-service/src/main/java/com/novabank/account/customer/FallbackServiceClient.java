package com.novabank.account.customer;

import com.novabank.account.dto.CustomerDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class FallbackServiceClient extends CustomerServiceClient {

    public FallbackServiceClient(WebClient.Builder builder) {
        super(builder);
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) {
        return new CustomerDTO(customerId, "Cliente no disponible", "", "", "", "");
    }
}