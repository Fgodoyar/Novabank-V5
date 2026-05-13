package com.novabank.account.customer;

import com.novabank.account.dto.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CUSTOMER-SERVICE", fallback = FallbackServiceClient.class)
public interface CustomerServiceClient {

    @GetMapping("/api/customers/{customerId}")
    CustomerDTO getCustomer(@PathVariable("customerId") Long customerId);
}
