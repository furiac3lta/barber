package com.marcedev.barberapp.dto;


import java.time.LocalDate;
import java.util.List;

public record AdminDashboardResponse(
        Long businessId,
        LocalDate date,
        Stats today,
        Stats week,
        Counts counts,
        List<AppointmentMini> lastAppointments
) {
    public record Stats(
            long total,
            long reserved,
            long canceled,
            long attended
    ) {}

    public record Counts(
            long activeServices,
            long activeBarbers,
            long clientsTotal
    ) {}

    public record AppointmentMini(
            Long id,
            String time,
            String status,
            String barberName,
            String serviceName,
            String clientName,
            String clientPhone
    ) {}
}
