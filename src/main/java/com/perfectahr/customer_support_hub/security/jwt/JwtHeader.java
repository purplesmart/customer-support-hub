package com.perfectahr.customer_support_hub.security.jwt;

import java.time.Instant;

public record JwtHeader(
        String accessToken,
        Instant expiresAt
) {
}