package com.marcedev.barberapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateServiceRequest(
        @NotNull Long businessId,
        @NotBlank String name,
        @NotNull Integer durationMin,
        @NotNull Integer price
) {}

