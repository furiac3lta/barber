package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.dto.AdminCalendarEventResponse;
import com.marcedev.barberapp.service.AdminCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        return calendarService.getCalendar(
                businessId,
                from,
                to,
                barberId
        );
    }
}
