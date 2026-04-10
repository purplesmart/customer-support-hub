package com.perfectahr.customer_support_hub.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        String issuer,
        long accessTokenExpirationMinutes
) {
}