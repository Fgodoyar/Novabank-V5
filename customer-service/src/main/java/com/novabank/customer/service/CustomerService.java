package com.novabank.customer.service;

import com.nov.novabank_v3.dto.CustomerDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomerService {
    List<CustomerDTO> listCustomers();
    CustomerDTO findById(Long customerId);
    CustomerDTO createCustomer(CustomerDTO dto);
    CustomerDTO findByDni(String dni);
}