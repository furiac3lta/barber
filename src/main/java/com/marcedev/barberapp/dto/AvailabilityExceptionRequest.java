// src/main/java/com/marcedev/barberapp/dto/AvailabilityExceptionRequest.java
package com.marcedev.barberapp.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AvailabilityExceptionRequest(
        @NotNull Long businessId,
        @NotNull LocalDate date,
        @NotNull Boolean closed,
        String startTime, // "HH:mm" o "HH:mm:ss"
        String endTime    // "HH:mm" o "HH:mm:ss"
) {}
