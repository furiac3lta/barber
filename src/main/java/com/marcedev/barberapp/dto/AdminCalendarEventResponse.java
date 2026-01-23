package com.marcedev.barberapp.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AdminCalendarEventResponse(
        Long id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String status,
        Long barberId,
        String barberName,
        String serviceName,
        String clientName,
        // AdminCalendarEventResponse
        String clientPhone

) {}
