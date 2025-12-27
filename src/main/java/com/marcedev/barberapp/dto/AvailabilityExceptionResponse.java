// src/main/java/com/marcedev/barberapp/dto/AvailabilityExceptionResponse.java
package com.marcedev.barberapp.dto;

import com.marcedev.barberapp.entity.AvailabilityException;

import java.time.LocalDate;
import java.time.LocalTime;

public record AvailabilityExceptionResponse(
        Long id,
        Long businessId,
        LocalDate date,
        boolean closed,
        LocalTime startTime,
        LocalTime endTime
) {
    public static AvailabilityExceptionResponse from(AvailabilityException ex) {
        return new AvailabilityExceptionResponse(
                ex.getId(),
                ex.getBusiness().getId(),
                ex.getDate(),
                ex.isClosed(),
                ex.getStartTime(),
                ex.getEndTime()
        );
    }
}
