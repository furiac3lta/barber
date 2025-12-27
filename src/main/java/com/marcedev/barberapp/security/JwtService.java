package com.marcedev.barberapp.security;

import com.marcedev.barberapp.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET =
            "BARBER_SUPER_SECRET_KEY_2025_CAMBIAR_EN_PROD";

    private static final long EXPIRATION =
            1000 * 60 * 60 * 24; // 24h

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getPhone())
                .claim("role", user.getRole().name())
                .claim("businessId", user.getBusiness().getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
