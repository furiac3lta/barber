package com.marcedev.barberapp.dto;

public record LoginRequest(
        String phone,
        String password
) {}
