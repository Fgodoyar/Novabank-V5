package com.novabank.auth.mapper;

import com.novabank.auth.domain.User;
import com.novabank.auth.dto.UserDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-15T12:03:15+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.userId( user.getUserId() );
        userDTO.username( user.getUsername() );
        userDTO.role( user.getRole() );
        userDTO.creationDate( user.getCreationDate() );

        return userDTO.build();
    }

    @Override
    public User toEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.userId( userDTO.getUserId() );
        user.username( userDTO.getUsername() );
        user.role( userDTO.getRole() );
        user.creationDate( userDTO.getCreationDate() );

        return user.build();
    }
}
