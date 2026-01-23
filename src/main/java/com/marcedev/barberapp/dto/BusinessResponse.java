package com.marcedev.barberapp.dto;

public record BusinessResponse(
        Long id,
        String name,
        String phone,
        String address,
        Boolean active

) {}
