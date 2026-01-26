package com.marcedev.barberapp.dto;


import jakarta.validation.constraints.NotBlank;

public record BusinessUpdateRequest(
        @NotBlank String name,
        String description,
        @NotBlank String phone,
        String address
) {}
