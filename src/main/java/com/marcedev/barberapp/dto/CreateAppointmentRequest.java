package com.marcedev.barberapp.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateAppointmentRequest(
        @NotNull Long businessId,
        @NotNull Long serviceId,
        @NotNull Long clientId,
        @NotNull LocalDate date,
        @NotNull LocalTime time
) {}
