package com.marcedev.barberapp.dto;

import jakarta.validation.constraints.NotBlank;

public record RescheduleAppointmentRequest(
        @NotBlank String date, // yyyy-MM-dd
        @NotBlank String time  // HH:mm
) {}
