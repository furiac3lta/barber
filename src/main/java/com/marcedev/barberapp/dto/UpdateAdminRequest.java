package com.marcedev.barberapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateAdminRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String phone,
        String password,
        @NotNull Long businessId
) {}
