package com.novabank.account.mapper;

import com.novabank.account.domain.Account;
import com.novabank.account.dto.AccountDTO;
import com.novabank.account.dto.CreateAccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    AccountDTO toDTO(Account account);
    Account toEntity(AccountDTO accountDTO);
    Account toEntity(CreateAccountRequest request);
}
