package com.marcedev.barberapp.dto;

import jakarta.validation.constraints.NotBlank;

public record BusinessRequest(
        @NotBlank String name,
        @NotBlank String phone,
        String address
) {}
