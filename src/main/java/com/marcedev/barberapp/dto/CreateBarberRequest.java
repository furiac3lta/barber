package com.marcedev.barberapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBarberRequest(
        @NotNull Long businessId,
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String phone,
        @NotBlank String password
) {}
