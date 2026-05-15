package com.novabank.account.mapper;

import com.novabank.account.domain.Account;
import com.novabank.account.dto.AccountDTO;
import com.novabank.account.dto.CreateAccountRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-15T17:24:30+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public AccountDTO toDTO(Account account) {
        if ( account == null ) {
            return null;
        }

        AccountDTO.AccountDTOBuilder accountDTO = AccountDTO.builder();

        accountDTO.accountId( account.getAccountId() );
        accountDTO.accountNumber( account.getAccountNumber() );
        accountDTO.accountHolder( account.getAccountHolder() );
        accountDTO.balance( account.getBalance() );
        accountDTO.creationDate( account.getCreationDate() );
        accountDTO.customerId( account.getCustomerId() );

        return accountDTO.build();
    }

    @Override
    public Account toEntity(AccountDTO accountDTO) {
        if ( accountDTO == null ) {
            return null;
        }

        Account.AccountBuilder account = Account.builder();

        account.accountId( accountDTO.getAccountId() );
        account.accountNumber( accountDTO.getAccountNumber() );
        account.accountHolder( accountDTO.getAccountHolder() );
        account.balance( accountDTO.getBalance() );
        account.creationDate( accountDTO.getCreationDate() );
        account.customerId( accountDTO.getCustomerId() );

        return account.build();
    }

    @Override
    public Account toEntity(CreateAccountRequest request) {
        if ( request == null ) {
            return null;
        }

        Account.AccountBuilder account = Account.builder();

        account.customerId( request.customerId() );

        return account.build();
    }
}
