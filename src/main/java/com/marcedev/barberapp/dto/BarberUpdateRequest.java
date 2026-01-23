package com.marcedev.barberapp.dto;

import jakarta.validation.constraints.NotBlank;

public record BarberUpdateRequest(
        @NotBlank String name
) {}
