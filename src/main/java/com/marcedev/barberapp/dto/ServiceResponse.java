package com.marcedev.barberapp.dto;

import com.marcedev.barberapp.entity.ServiceItem;

public record ServiceResponse(
        Long id,
        String name,
        Integer durationMin,
        Integer price,
        Integer breakMin,
        boolean active

) {
    public static ServiceResponse from(ServiceItem s) {
        return new ServiceResponse(
                s.getId(),
                s.getName(),
                s.getDurationMin(),
                s.getPrice(),
                s.getBreakMin(),
                s.isActive()
        );
    }
}
