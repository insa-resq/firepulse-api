package org.resq.firepulseapi.accountsservice.services;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.resq.firepulseapi.accountsservice.dtos.AuthenticatedUserDetailsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final TemporalAmount jwtValidity;

    public TokenService(@Value("${jwt.secret-key}") String jwtSecretKey) {
        SecretKey key = new SecretKeySpec(jwtSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        JWKSource<SecurityContext> immutableSecret = new ImmutableSecret<>(key);
        this.jwtEncoder = new NimbusJwtEncoder(immutableSecret);

        this.jwtValidity = ChronoUnit.HOURS.getDuration().multipliedBy(6); // 6 hours
    }

    public String generateToken(Authentication authentication) {
        AuthenticatedUserDetailsDto userDetails = (AuthenticatedUserDetailsDto) authentication.getPrincipal();

        if (userDetails == null) {
            throw new AuthenticationCredentialsNotFoundException("User details not found in authentication principal");
        }

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("firepulse-api-accounts-service")
                .issuedAt(now)
                .expiresAt(now.plus(jwtValidity))
                .subject(userDetails.getId())
                .claim("email", userDetails.getUsername())
                .claim("roles", scope)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}
