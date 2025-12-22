package com.nnikolaev.beercollection.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.nnikolaev.beercollection.common.Constant.AppConfigValue;

@Component
public class JwtTokenProvider {
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();
    @Autowired
    @Value(AppConfigValue.JWT_SECRET)
    private String secretKey;
    @Autowired
    @Value(AppConfigValue.JWT_EXPIRATION_MS)
    private long jwtValidityInMillis;

    public String generateToken(String email, String roleName) {
        final Date now = new Date();
        final Date expiry = new Date(now.getTime() + this.jwtValidityInMillis);

        return Jwts
                .builder()
                .subject(email)
                .claim("role", roleName)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(this.getSignInKey())
                .compact();
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public Instant getExpiryFromToken(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }

    public boolean validateToken(String token) {
        try {
            this.parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isBlacklisted(String token) {
        Instant exp = blacklist.get(token);
        if (exp == null) return false;

        if (Instant.now().isAfter(exp)) {
            blacklist.remove(token);
            return false;
        }

        return true;
    }

    public void blacklistToken(String token) {
        final Instant expiry = this.getExpiryFromToken(token);
        blacklist.put(token, expiry);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        final byte[] bytes = Base64.getDecoder()
                .decode(this.secretKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(bytes, "HmacSHA256");
    }
}
