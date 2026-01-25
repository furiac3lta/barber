package com.marcedev.barberapp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expMinutes}") long expMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expMinutes * 60 * 1000;
    }

    public String generateToken(Long userId, String role) {
        return generateToken(userId, role, null);
    }

    public String generateToken(Long userId, String role, Long businessId) {
        return generateToken(userId, role, businessId, null);
    }

    public String generateToken(Long userId, String role, Long businessId, Long barberId) {
        JwtBuilder builder = Jwts.builder()
                .claim("role", role)
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256);

        if (businessId != null) {
            builder.claim("businessId", businessId);
        }
        if (barberId != null) {
            builder.claim("barberId", barberId);
        }

        return builder.compact();
    }

    public Long extractUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public Long extractBusinessId(String token) {
        Object raw = getClaims(token).get("businessId");
        if (raw == null) return null;
        if (raw instanceof Long l) return l;
        if (raw instanceof Integer i) return i.longValue();
        return Long.parseLong(String.valueOf(raw));
    }

    public Long extractBarberId(String token) {
        Object raw = getClaims(token).get("barberId");
        if (raw == null) return null;
        if (raw instanceof Long l) return l;
        if (raw instanceof Integer i) return i.longValue();
        return Long.parseLong(String.valueOf(raw));
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
