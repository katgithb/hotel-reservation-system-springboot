package com.zenhotel.hrs_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private SecretKey key;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.issuer}")
    private String issuer;

    @Value("${jwt.token.expiry-duration-seconds}")
    private int expirationTimeInSeconds;

    @PostConstruct
    private void init() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = new SecretKeySpec(keyBytes, HMAC_ALGORITHM);
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        // Get the current Instant
        Instant now = Instant.now();

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationTimeInSeconds, ChronoUnit.SECONDS)))
                .signWith(key)
                .compact();
    }

    public String generateToken(String subject) {
        return generateToken(subject, Map.of());
    }

    public String generateToken(String subject, String... scopes) {
        return generateToken(subject, Map.of("scopes", scopes));
    }

    public String generateToken(String subject, List<String> scopes) {
        return generateToken(subject, Map.of("scopes", scopes));
    }

    public String getSubjectFromToken(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date getExpirationFromToken(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String subject = getSubjectFromToken(token);
        return (subject.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date today = new Date();
        return getExpirationFromToken(token).before(today);
    }

}
