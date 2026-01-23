package com.marcedev.barberapp.dto;

import com.marcedev.barberapp.entity.Barber;

public record BarberResponse(
        Long id,
        String name,
        boolean active,
        java.util.List<Long> serviceIds
) {
    public static BarberResponse from(Barber b) {
        var serviceIds = b.getServices() == null
                ? java.util.List.<Long>of()
                : b.getServices().stream().map(com.marcedev.barberapp.entity.ServiceItem::getId).toList();
        return new BarberResponse(b.getId(), b.getName(), b.isActive(), serviceIds);
    }
}
