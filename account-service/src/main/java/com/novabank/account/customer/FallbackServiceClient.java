package com.novabank.account.customer;

import com.novabank.account.dto.CustomerDTO;
import org.springframework.stereotype.Component;

@Component
public class FallbackServiceClient implements CustomerServiceClient {

    @Override
    public CustomerDTO getCustomer(Long customerId) {
        return new CustomerDTO(customerId, "Cliente no disponible", "", "", "", "");
    }
}