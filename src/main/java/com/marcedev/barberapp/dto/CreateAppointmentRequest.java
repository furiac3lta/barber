package com.marcedev.barberapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateAppointmentRequest(
        @NotNull Long businessId,
        @NotNull Long barberId,
        @NotNull Long serviceId,
        @NotNull Long clientId,
        @NotNull LocalDate date,
        @NotNull LocalTime time,
        @Size(max = 300) String reason


        ) {}
