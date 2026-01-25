package com.marcedev.barberapp.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public record AuthUser(
        Long userId,
        Long businessId,
        String role,
        Long barberId
) {
    public List<GrantedAuthority> authorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
