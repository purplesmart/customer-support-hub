package com.perfectahr.customer_support_hub.auth.dto;

import java.time.Instant;
import java.util.List;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        Long userId,
        String username,
        List<String> roles
) {
}