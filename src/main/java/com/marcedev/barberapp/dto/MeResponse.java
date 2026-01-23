package com.marcedev.barberapp.dto;

public record MeResponse(
        Long id,
        String name,
        String email,
        String phone,
        String role,
        Long businessId
) {}
