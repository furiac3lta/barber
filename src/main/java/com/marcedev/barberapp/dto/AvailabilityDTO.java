package com.marcedev.barberapp.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record AvailabilityDTO(
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}
