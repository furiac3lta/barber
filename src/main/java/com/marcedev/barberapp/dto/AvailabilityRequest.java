package com.marcedev.barberapp.dto;

import java.time.DayOfWeek;

public record AvailabilityRequest(
        Long businessId,
        DayOfWeek dayOfWeek,
        String startTime,
        String endTime,
        String startTime2,
        String endTime2
) {}
