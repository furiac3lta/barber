package com.marcedev.barberapp.dto;

public record AdminUserResponse(
        Long id,
        String name,
        String email,
        String phone,
        Long businessId,
        String businessName,
        Boolean active
) {}
