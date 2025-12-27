package com.marcedev.barberapp.dto;

import com.marcedev.barberapp.entity.Barber;

public record BarberResponse(
        Long id,
        String name
) {
    public static BarberResponse from(Barber b) {
        return new BarberResponse(b.getId(), b.getName());
    }
}
