package com.marcedev.barberapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateServiceRequest(

        @NotBlank
        String name,

        @NotNull
        @Positive
        Integer durationMin,

        @NotNull
        @Positive
        Integer price
) {}
