package com.perfectahr.customer_support_hub.auth.controller;

import com.perfectahr.customer_support_hub.auth.dto.CreateAdminRequest;
import com.perfectahr.customer_support_hub.auth.dto.LoginRequest;
import com.perfectahr.customer_support_hub.auth.dto.LoginResponse;
import com.perfectahr.customer_support_hub.auth.service.AuthService;
import com.perfectahr.customer_support_hub.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/create-admin")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        return authService.createAdmin(request);
    }

    @GetMapping("/me")
    public String me(org.springframework.security.core.Authentication authentication) {
        if (authentication == null) {
            return "authentication is null";
        }

        return "name=" + authentication.getName() + ", authorities=" + authentication.getAuthorities();
    }
}