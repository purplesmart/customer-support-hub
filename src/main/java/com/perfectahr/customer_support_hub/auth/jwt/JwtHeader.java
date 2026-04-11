package com.perfectahr.customer_support_hub.auth.jwt;

import java.time.Instant;

public record JwtHeader(
        String accessToken,
        Instant expiresAt
) {
}