package com.marcedev.barberapp.security;

import com.marcedev.barberapp.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final com.marcedev.barberapp.repository.BarberRepository barberRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return new AntPathMatcher().match("/api/auth/**", request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7).trim();
        if (token.isEmpty() || "null".equalsIgnoreCase(token) || "undefined".equalsIgnoreCase(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long userId = jwtService.extractUserId(token);
            String role = jwtService.extractRole(token);
            Long businessId = jwtService.extractBusinessId(token);
            Long barberId = jwtService.extractBarberId(token);

            if (businessId == null) {
                businessId = userRepository.findById(userId)
                        .map(user -> user.getBusiness() != null ? user.getBusiness().getId() : null)
                        .orElse(null);
            }
            if (barberId == null && "BARBER".equalsIgnoreCase(role)) {
                barberId = barberRepository.findByUserId(userId)
                        .map(com.marcedev.barberapp.entity.Barber::getId)
                        .orElse(null);
            }

            AuthUser authUser = new AuthUser(userId, businessId, role, barberId);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            authUser,
                            null,
                            authUser.authorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception ignored) {
            if (header != null && header.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
