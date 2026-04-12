package com.perfectahr.customer_support_hub.auth.service;

import com.perfectahr.customer_support_hub.auth.dto.CreateAdminRequest;
import com.perfectahr.customer_support_hub.auth.dto.LoginRequest;
import com.perfectahr.customer_support_hub.auth.dto.LoginResponse;
import com.perfectahr.customer_support_hub.auth.jwt.JwtHeader;
import com.perfectahr.customer_support_hub.auth.jwt.JwtTokenService;
import com.perfectahr.customer_support_hub.entity.Role;
import com.perfectahr.customer_support_hub.entity.User;
import com.perfectahr.customer_support_hub.exception.DuplicateResourceException;
import com.perfectahr.customer_support_hub.exception.NotFoundException;
import com.perfectahr.customer_support_hub.repository.UserRepository;
import com.perfectahr.customer_support_hub.user.dto.UserResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public AuthServiceImpl(UserRepository userRepository,
                           JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (user.getPassword() == null || !user.getPassword().equals(request.password())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        JwtHeader jwtHeader = jwtTokenService.generateToken(user, authorities);

        List<String> roles = authorities.stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();

        return new LoginResponse(
                jwtHeader.accessToken(),
                "Bearer",
                jwtHeader.expiresAt(),
                user.getId(),
                user.getUsername(),
                roles
        );
    }

    @Override
    public UserResponse createAdmin(CreateAdminRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User admin = new User();
        admin.setUsername(request.username());
        admin.setPassword(request.password());
        admin.setFirstName(request.firstName());
        admin.setLastName(request.lastName());
        admin.setEmail(request.email());
        admin.setRole(Role.ADMIN);

        User saved = userRepository.save(admin);

        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getEmail(),
                saved.getRole()
        );
    }
}