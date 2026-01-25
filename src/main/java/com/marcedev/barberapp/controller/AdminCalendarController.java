package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.dto.AdminCalendarEventResponse;
import com.marcedev.barberapp.service.AdminCalendarService;
import com.marcedev.barberapp.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/calendar")
@RequiredArgsConstructor
public class AdminCalendarController {

    private final AdminCalendarService calendarService;

    @GetMapping
    public List<AdminCalendarEventResponse> getCalendar(
            @RequestParam Long businessId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(required = false) Long barberId
    ) {
        AuthUser authUser = getAuthUser();
        if (authUser != null && "BARBER".equalsIgnoreCase(authUser.role())) {
            if (authUser.businessId() != null) {
                businessId = authUser.businessId();
            }
            barberId = authUser.barberId();
        }
        return calendarService.getCalendar(
                businessId,
                from,
                to,
                barberId
        );
    }

    private AuthUser getAuthUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof AuthUser user) return user;
        return null;
    }
}
