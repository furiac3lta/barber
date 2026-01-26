package com.marcedev.barberapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateServiceRequest(

        @NotBlank
        String name,

        @NotNull
        @Positive
        Integer durationMin,

        @NotNull
        @Positive
        Integer price,

        @PositiveOrZero
        Integer breakMin
) {}
