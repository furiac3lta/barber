package com.marcedev.barberapp.dto;

public record LoginResponse(
        String token,
        Long userId,
        Long businessId,
        String role,
        String name,
        String email,
        String phone
) {}
