package com.perfectahr.customer_support_hub.auth.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.perfectahr.customer_support_hub.auth.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final int MIN_SECRET_LENGTH_BYTES = 32;

    @Bean
    public SecretKey jwtSecretKey(JwtProperties jwtProperties) {
        String secret = jwtProperties.secret();

        System.out.println("JWT SECRET LENGTH = " + (secret == null ? "null" : secret.length()));
        System.out.println("JWT SECRET VALUE = " + secret);

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret must not be empty");
        }

        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (secretBytes.length < MIN_SECRET_LENGTH_BYTES) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes long for HS256");
        }

        return new SecretKeySpec(secretBytes, HMAC_ALGORITHM);
    }
    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
        return NimbusJwtDecoder
                .withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}