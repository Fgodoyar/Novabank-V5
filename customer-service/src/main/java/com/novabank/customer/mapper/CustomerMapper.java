package com.novabank.customer.mapper;

import com.novabank.customer.domain.Customer;
import com.novabank.customer.dto.CreateCustomerRequest;
import com.novabank.customer.dto.CustomerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    CustomerDTO toDTO(Customer customer);

    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    Customer toEntity(CreateCustomerRequest request);
}