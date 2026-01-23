package com.marcedev.barberapp.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class BusinessAccessGuard {

    public void assertBusinessAccess(Long businessId) {
        if (businessId == null) return;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return;

        Object principal = auth.getPrincipal();
        if (!(principal instanceof AuthUser user)) return;

        if (!"ADMIN".equalsIgnoreCase(user.role())) return;

        Long userBusinessId = user.businessId();
        if (userBusinessId == null || !userBusinessId.equals(businessId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado para este negocio");
        }
    }
}
