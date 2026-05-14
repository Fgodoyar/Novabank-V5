package com.novabank.auth.dto;

import com.novabank.auth.domain.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long userId;
    private String username;
    private Role role;
    private LocalDateTime creationDate;

}