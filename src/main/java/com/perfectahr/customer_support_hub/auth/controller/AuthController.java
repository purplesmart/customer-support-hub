package com.perfectahr.customer_support_hub.auth.controller;

import com.perfectahr.customer_support_hub.auth.dto.LoginRequest;
import com.perfectahr.customer_support_hub.auth.dto.LoginResponse;
import com.perfectahr.customer_support_hub.auth.service.AuthService;
import jakarta.validation.Valid;
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
}