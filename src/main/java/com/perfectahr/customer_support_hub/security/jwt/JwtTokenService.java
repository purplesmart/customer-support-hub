package com.perfectahr.customer_support_hub.security.jwt;

import com.perfectahr.customer_support_hub.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public JwtTokenService(JwtEncoder jwtEncoder, JwtProperties jwtProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    public JwtHeader generateToken(User user, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(jwtProperties.accessTokenExpirationMinutes() * 60);

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("userId", user.getId())
                .claim("roles", roles)
                .build();

        String token = jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        return new JwtHeader(token, expiresAt);
    }
}