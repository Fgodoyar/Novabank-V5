package com.novabank.auth.service;

import com.novabank.auth.domain.Role;
import com.novabank.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Usuario no encontrado con nombre: " + username)))
                .map(user -> (UserDetails) org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRole().name())
                        .build()
                );
    }

    public Mono<Void> register(String username, String password) {
        return userRepository.existsByUsername(username)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("El usuario ya existe: " + username));
                    }

                    com.novabank.auth.domain.User user = com.novabank.auth.domain.User.builder()
                            .username(username)
                            .password(passwordEncoder.encode(password))
                            .role(Role.ROLE_USER)
                            .creationDate(LocalDateTime.now())
                            .build();

                    return userRepository.save(user).then();
                });
    }

    public Mono<String> login(String username, String password) {
        return findByUsername(username)
                .flatMap(userDetails -> {
                    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                        return Mono.error(new BadCredentialsException("Contraseña incorrecta"));
                    }
                    return Mono.just(jwtService.generateToken(username));
                });
    }
}