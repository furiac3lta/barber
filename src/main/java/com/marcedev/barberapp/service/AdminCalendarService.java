package com.marcedev.barberapp.service;

import com.marcedev.barberapp.dto.AdminCalendarEventResponse;

import java.time.LocalDate;
import java.util.List;

public interface AdminCalendarService {
    List<AdminCalendarEventResponse> getCalendar(
            Long businessId,
            LocalDate from,
            LocalDate to,
            Long barberId
    );
}
