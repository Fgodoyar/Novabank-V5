package com.novabank.customer.mapper;

import com.novabank.customer.domain.Customer;
import com.novabank.customer.dto.CreateCustomerRequest;
import com.novabank.customer.dto.CustomerDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T14:21:41+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public CustomerDTO toDTO(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerDTO.CustomerDTOBuilder customerDTO = CustomerDTO.builder();

        customerDTO.customerId( customer.getCustomerId() );
        customerDTO.customerName( customer.getCustomerName() );
        customerDTO.lastName( customer.getLastName() );
        customerDTO.dni( customer.getDni() );
        customerDTO.email( customer.getEmail() );
        customerDTO.phoneNumber( customer.getPhoneNumber() );
        customerDTO.creationDate( customer.getCreationDate() );

        return customerDTO.build();
    }

    @Override
    public Customer toEntity(CreateCustomerRequest request) {
        if ( request == null ) {
            return null;
        }

        Customer.CustomerBuilder customer = Customer.builder();

        customer.customerName( request.customerName() );
        customer.lastName( request.lastName() );
        customer.dni( request.dni() );
        customer.email( request.email() );
        customer.phoneNumber( request.phoneNumber() );

        return customer.build();
    }
}
