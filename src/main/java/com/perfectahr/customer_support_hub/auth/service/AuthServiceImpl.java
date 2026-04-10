package com.perfectahr.customer_support_hub.auth.service;

import com.perfectahr.customer_support_hub.auth.dto.LoginRequest;
import com.perfectahr.customer_support_hub.auth.dto.LoginResponse;
import com.perfectahr.customer_support_hub.entity.User;
import com.perfectahr.customer_support_hub.repository.UserRepository;
import com.perfectahr.customer_support_hub.security.jwt.JwtHeader;
import com.perfectahr.customer_support_hub.security.jwt.JwtTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtTokenService jwtTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtHeader jwtHeader = jwtTokenService.generateToken(user, authentication.getAuthorities());

        return new LoginResponse(
                jwtHeader.accessToken(),
                "Bearer",
                jwtHeader.expiresAt(),
                user.getId(),
                user.getUsername(),
                roles
        );
    }
}