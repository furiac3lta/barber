package com.marcedev.barberapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateServiceRequest(
        @NotNull Long businessId,
        @NotBlank String name,
        @NotNull Integer durationMin,
        @NotNull Integer price,
        @PositiveOrZero Integer breakMin
) {}
